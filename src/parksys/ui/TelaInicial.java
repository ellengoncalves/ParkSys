package parksys.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import parksys.observer.PainelMonitor;
import parksys.services.DadosParkSys;
import parksys.services.GerenciadorArquivo;
import parksys.services.GerenciadorEstacionamento;
import parksys.services.MonitorRunnable;

public class TelaInicial extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final String CAMINHO_DADOS = "dados/parksys.ser";
    private static final Color ROXO_FECHADO = new Color(94, 58, 135);
    private static final Color LILAS_SUAVE = new Color(237, 231, 246);
    private static final Color LILAS_CLARO = new Color(247, 243, 251);
    private static final Color ROSA_QUEIMADO = new Color(181, 101, 118);
    private static final Color FUNDO_CLARO = new Color(250, 247, 251);
    private static final Color TEXTO_ESCURO = new Color(46, 46, 46);
    private static final Color TEXTO_SECUNDARIO = new Color(92, 82, 101);
    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 38);
    private static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.PLAIN, 17);
    private static final Font FONTE_SECAO = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONTE_BOTAO = new Font("Segoe UI", Font.BOLD, 16);

    private final GerenciadorEstacionamento gerenciador;
    private final PainelMonitor painelMonitor;
    private final Thread monitorThread;

    public TelaInicial() {
        this.gerenciador = GerenciadorEstacionamento.getInstance();
        this.painelMonitor = new PainelMonitor();
        this.monitorThread = new Thread(new MonitorRunnable(gerenciador), "Monitor-Aplicacao");

        carregarDados();
        painelMonitor.carregarStatusAtual(gerenciador.getVagas());
        gerenciador.addObserver(painelMonitor);
        painelMonitor.setVisible(true);
        iniciarMonitorDaemon();

        configurarJanela();
        montarComponentes();
    }

    private void configurarJanela() {
        setTitle("ParkSys");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(760, 520));
        setSize(900, 600);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                fecharAplicacao();
            }
        });
    }

    private void montarComponentes() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(0, 26));
        painelPrincipal.setBackground(FUNDO_CLARO);
        painelPrincipal.setBorder(new EmptyBorder(42, 56, 42, 56));

        JLabel titulo = new JLabel("ParkSys", SwingConstants.CENTER);
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(ROXO_FECHADO);

        JLabel subtitulo = new JLabel("Sistema de Gest\u00e3o de Estacionamento", SwingConstants.CENTER);
        subtitulo.setFont(FONTE_SUBTITULO);
        subtitulo.setForeground(TEXTO_SECUNDARIO);

        JPanel painelCabecalho = new JPanel(new GridLayout(2, 1, 0, 6));
        painelCabecalho.setBackground(FUNDO_CLARO);
        painelCabecalho.add(titulo);
        painelCabecalho.add(subtitulo);

        JLabel tituloAcoes = new JLabel("Opera\u00e7\u00f5es", SwingConstants.LEFT);
        tituloAcoes.setFont(FONTE_SECAO);
        tituloAcoes.setForeground(TEXTO_ESCURO);

        JButton botaoEntrada = new JButton("Registrar Entrada");
        estilizarBotao(botaoEntrada, ROXO_FECHADO);
        botaoEntrada.addActionListener(event -> abrirTelaRegistroEntrada());

        JButton botaoSaida = new JButton("Registrar Sa\u00edda");
        estilizarBotao(botaoSaida, ROXO_FECHADO);
        botaoSaida.addActionListener(event -> abrirTelaSaida());

        JButton botaoCadastroMensalista = new JButton("Cadastro de Mensalista");
        estilizarBotao(botaoCadastroMensalista, ROSA_QUEIMADO);
        botaoCadastroMensalista.addActionListener(event -> abrirTelaCadastroMensalista());

        JButton botaoRelatorio = new JButton("Ver Relat\u00f3rio");
        estilizarBotao(botaoRelatorio, ROSA_QUEIMADO);
        botaoRelatorio.addActionListener(event -> abrirTelaRelatorio());

        JPanel painelBotoes = new JPanel(new GridLayout(2, 2, 18, 18));
        painelBotoes.setBackground(Color.WHITE);
        painelBotoes.add(botaoEntrada);
        painelBotoes.add(botaoSaida);
        painelBotoes.add(botaoCadastroMensalista);
        painelBotoes.add(botaoRelatorio);

        JPanel painelCartao = new JPanel(new BorderLayout(0, 18));
        painelCartao.setBackground(Color.WHITE);
        painelCartao.setBorder(new EmptyBorder(30, 34, 34, 34));
        painelCartao.add(tituloAcoes, BorderLayout.NORTH);
        painelCartao.add(painelBotoes, BorderLayout.CENTER);

        JPanel painelCentral = new JPanel(new GridBagLayout());
        painelCentral.setBackground(LILAS_SUAVE);
        painelCentral.setBorder(new EmptyBorder(34, 34, 34, 34));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 0, 0, 0);
        painelCartao.setPreferredSize(new Dimension(720, 300));
        painelCentral.add(painelCartao, constraints);

        painelPrincipal.add(painelCabecalho, BorderLayout.NORTH);
        painelPrincipal.add(painelCentral, BorderLayout.CENTER);
        add(painelPrincipal, BorderLayout.CENTER);
    }

    private void estilizarBotao(JButton botao, Color corFundo) {
        botao.setFont(FONTE_BOTAO);
        botao.setForeground(Color.WHITE);
        botao.setBackground(corFundo);
        botao.setOpaque(true);
        botao.setFocusPainted(false);
        botao.setBorder(new EmptyBorder(18, 24, 18, 24));
        botao.setPreferredSize(new Dimension(260, 72));
    }

    private void abrirTelaRegistroEntrada() {
        new TelaRegistroEntrada().setVisible(true);
    }

    private void abrirTelaSaida() {
        new TelaSaida().setVisible(true);
    }

    private void abrirTelaCadastroMensalista() {
        new TelaCadastroMensalista().setVisible(true);
    }

    private void abrirTelaRelatorio() {
        new TelaRelatorio().setVisible(true);
    }

    private void iniciarMonitorDaemon() {
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    private void fecharAplicacao() {
        monitorThread.interrupt();
        salvarDados();
        gerenciador.removeObserver(painelMonitor);
        painelMonitor.dispose();
        dispose();
        System.exit(0);
    }

    private void carregarDados() {
        DadosParkSys dados = GerenciadorArquivo.desserializar(CAMINHO_DADOS);

        if (dados.getVagas() != null && !dados.getVagas().isEmpty()) {
            gerenciador.carregarDados(dados);
        }
    }

    private void salvarDados() {
        GerenciadorArquivo.serializar(
                gerenciador.getVagas(),
                gerenciador.getRegistros(),
                gerenciador.getMensalistas(),
                CAMINHO_DADOS);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaInicial().setVisible(true));
    }
}

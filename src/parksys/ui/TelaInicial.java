package parksys.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import parksys.entities.Vaga;
import parksys.enums.StatusVaga;
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
    private static final Color ROSA_QUEIMADO = new Color(181, 101, 118);
    private static final Color FUNDO_CLARO = new Color(250, 247, 251);
    private static final Color BRANCO = Color.WHITE;
    private static final Color VERDE_STATUS = new Color(45, 128, 91);
    private static final Color VERMELHO_STATUS = new Color(181, 82, 92);
    private static final Color AMARELO_STATUS = new Color(150, 112, 34);
    private static final Color TEXTO_SECUNDARIO = new Color(92, 82, 101);
    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 38);
    private static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.PLAIN, 17);
    private static final Font FONTE_BOTAO = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONTE_CARD_NUMERO = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font FONTE_CARD_TITULO = new Font("Segoe UI", Font.BOLD, 13);

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
        iniciarMonitorDaemon();

        configurarJanela();
        montarComponentes();
    }

    private void configurarJanela() {
        setTitle("ParkSys");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(840, 540));
        setSize(920, 590);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                fecharAplicacao();
            }
        });
    }

    private void montarComponentes() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(0, 18));
        painelPrincipal.setBackground(FUNDO_CLARO);
        painelPrincipal.setBorder(new EmptyBorder(30, 44, 30, 44));

        JLabel titulo = new JLabel("ParkSys", SwingConstants.CENTER);
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(ROXO_FECHADO);

        JLabel subtitulo = new JLabel("Sistema de gest\u00e3o de estacionamento", SwingConstants.CENTER);
        subtitulo.setFont(FONTE_SUBTITULO);
        subtitulo.setForeground(TEXTO_SECUNDARIO);

        JPanel painelCabecalho = new JPanel(new GridLayout(2, 1, 0, 6));
        painelCabecalho.setBackground(FUNDO_CLARO);
        painelCabecalho.add(titulo);
        painelCabecalho.add(subtitulo);

        JPanel painelCentral = new JPanel(new GridBagLayout());
        painelCentral.setBackground(LILAS_SUAVE);
        painelCentral.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel painelConteudo = new JPanel(new BorderLayout(0, 16));
        painelConteudo.setOpaque(false);
        painelConteudo.add(criarPainelResumoVagas(), BorderLayout.NORTH);
        painelConteudo.add(criarPainelOperacoes(), BorderLayout.CENTER);

        GridBagConstraints restricoesConteudo = new GridBagConstraints();
        restricoesConteudo.gridx = 0;
        restricoesConteudo.gridy = 0;
        restricoesConteudo.weightx = 1.0;
        restricoesConteudo.fill = GridBagConstraints.HORIZONTAL;
        painelCentral.add(painelConteudo, restricoesConteudo);

        painelPrincipal.add(painelCabecalho, BorderLayout.NORTH);
        painelPrincipal.add(painelCentral, BorderLayout.CENTER);
        add(painelPrincipal, BorderLayout.CENTER);
    }

    private JPanel criarPainelOperacoes() {
        JButton botaoEntrada = new JButton("Registrar entrada");
        estilizarBotao(botaoEntrada, ROXO_FECHADO);
        botaoEntrada.addActionListener(event -> abrirTelaRegistroEntrada());

        JButton botaoSaida = new JButton("Registrar sa\u00edda");
        estilizarBotao(botaoSaida, ROXO_FECHADO);
        botaoSaida.addActionListener(event -> abrirTelaSaida());

        JButton botaoCadastroMensalista = new JButton("Cadastro de mensalista");
        estilizarBotao(botaoCadastroMensalista, ROSA_QUEIMADO);
        botaoCadastroMensalista.addActionListener(event -> abrirTelaCadastroMensalista());

        JButton botaoRelatorio = new JButton("Ver relat\u00f3rio");
        estilizarBotao(botaoRelatorio, ROSA_QUEIMADO);
        botaoRelatorio.addActionListener(event -> abrirTelaRelatorio());

        JPanel painelBotoes = new JPanel(new GridLayout(2, 2, 12, 12));
        painelBotoes.setBackground(BRANCO);
        painelBotoes.setPreferredSize(new Dimension(0, 118));
        painelBotoes.add(botaoEntrada);
        painelBotoes.add(botaoSaida);
        painelBotoes.add(botaoCadastroMensalista);
        painelBotoes.add(botaoRelatorio);

        JPanel painelCartao = new JPanel(new BorderLayout(0, 18));
        painelCartao.setBackground(BRANCO);
        painelCartao.setBorder(new EmptyBorder(30, 34, 34, 34));
        painelCartao.add(painelBotoes, BorderLayout.NORTH);
        return painelCartao;
    }

    private JPanel criarPainelResumoVagas() {
        int livres = contarVagas(StatusVaga.LIVRE);
        int ocupadas = contarVagas(StatusVaga.OCUPADA);
        int reservadas = contarVagas(StatusVaga.RESERVADA);
        int total = gerenciador.getVagas().size();

        JPanel painelResumo = new JPanel(new GridLayout(1, 4, 14, 0));
        painelResumo.setOpaque(false);
        painelResumo.add(criarCardResumo("Livres", livres, VERDE_STATUS));
        painelResumo.add(criarCardResumo("Ocupadas", ocupadas, VERMELHO_STATUS));
        painelResumo.add(criarCardResumo("Reservadas", reservadas, AMARELO_STATUS));
        painelResumo.add(criarCardResumo("Total", total, ROXO_FECHADO));
        return painelResumo;
    }

    private JPanel criarCardResumo(String titulo, int valor, Color cor) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(BRANCO);
        card.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel labelValor = new JLabel(String.valueOf(valor), SwingConstants.LEFT);
        labelValor.setFont(FONTE_CARD_NUMERO);
        labelValor.setForeground(cor);

        JLabel labelTitulo = new JLabel(titulo, SwingConstants.LEFT);
        labelTitulo.setFont(FONTE_CARD_TITULO);
        labelTitulo.setForeground(TEXTO_SECUNDARIO);

        card.add(labelValor, BorderLayout.NORTH);
        card.add(labelTitulo, BorderLayout.CENTER);
        return card;
    }

    private int contarVagas(StatusVaga status) {
        int total = 0;

        for (Vaga vaga : gerenciador.getVagas().values()) {
            if (vaga.getStatus() == status) {
                total++;
            }
        }

        return total;
    }

    private void estilizarBotao(JButton botao, Color corFundo) {
        botao.setFont(FONTE_BOTAO);
        botao.setForeground(Color.WHITE);
        botao.setBackground(corFundo);
        botao.setOpaque(true);
        botao.setFocusPainted(false);
        botao.setBorder(new EmptyBorder(8, 24, 8, 24));
        botao.setPreferredSize(new Dimension(260, 50));
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

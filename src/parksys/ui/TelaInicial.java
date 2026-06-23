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
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

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
    private static final Color LILAS_CLARO = new Color(247, 243, 251);
    private static final Color ROSA_QUEIMADO = new Color(181, 101, 118);
    private static final Color FUNDO_CLARO = new Color(250, 247, 251);
    private static final Color BRANCO = Color.WHITE;
    private static final Color VERDE_STATUS = new Color(45, 128, 91);
    private static final Color VERMELHO_STATUS = new Color(181, 82, 92);
    private static final Color AMARELO_STATUS = new Color(150, 112, 34);
    private static final Color TEXTO_ESCURO = new Color(46, 46, 46);
    private static final Color TEXTO_SECUNDARIO = new Color(92, 82, 101);
    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 38);
    private static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.PLAIN, 17);
    private static final Font FONTE_SECAO = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONTE_BOTAO = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONTE_CARD_NUMERO = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font FONTE_CARD_TITULO = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONTE_VAGA = new Font("Segoe UI", Font.BOLD, 11);

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
        setMinimumSize(new Dimension(900, 680));
        setSize(1020, 760);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                fecharAplicacao();
            }
        });
    }

    private void montarComponentes() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(0, 22));
        painelPrincipal.setBackground(FUNDO_CLARO);
        painelPrincipal.setBorder(new EmptyBorder(34, 48, 34, 48));

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

        JPanel painelCentral = new JPanel(new BorderLayout(0, 16));
        painelCentral.setBackground(LILAS_SUAVE);
        painelCentral.setBorder(new EmptyBorder(24, 24, 24, 24));
        painelCentral.add(criarPainelResumoVagas(), BorderLayout.NORTH);
        painelCentral.add(criarPainelOperacoes(), BorderLayout.CENTER);
        painelCentral.add(criarPainelMapaRapido(), BorderLayout.SOUTH);

        painelPrincipal.add(painelCabecalho, BorderLayout.NORTH);
        painelPrincipal.add(painelCentral, BorderLayout.CENTER);
        add(painelPrincipal, BorderLayout.CENTER);
    }

    private JPanel criarPainelOperacoes() {
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
        painelBotoes.setBackground(BRANCO);
        painelBotoes.add(botaoEntrada);
        painelBotoes.add(botaoSaida);
        painelBotoes.add(botaoCadastroMensalista);
        painelBotoes.add(botaoRelatorio);

        JPanel painelCartao = new JPanel(new BorderLayout(0, 18));
        painelCartao.setBackground(BRANCO);
        painelCartao.setBorder(new EmptyBorder(30, 34, 34, 34));
        painelCartao.add(tituloAcoes, BorderLayout.NORTH);
        painelCartao.add(painelBotoes, BorderLayout.CENTER);
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

    private JPanel criarPainelMapaRapido() {
        JPanel painelMapa = new JPanel(new BorderLayout(0, 12));
        painelMapa.setBackground(BRANCO);
        painelMapa.setBorder(javax.swing.BorderFactory.createTitledBorder(
                new LineBorder(LILAS_SUAVE, 1, true),
                "Mapa rapido de vagas",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                FONTE_CARD_TITULO,
                ROXO_FECHADO));

        JPanel gradeVagas = new JPanel(new GridLayout(2, 15, 4, 4));
        gradeVagas.setBackground(BRANCO);
        gradeVagas.setBorder(new EmptyBorder(4, 10, 0, 10));

        for (Map.Entry<String, Vaga> entrada : new TreeMap<>(gerenciador.getVagas()).entrySet()) {
            gradeVagas.add(criarIndicadorVaga(entrada.getKey(), entrada.getValue().getStatus()));
        }

        painelMapa.add(gradeVagas, BorderLayout.CENTER);
        painelMapa.add(criarLegendaVagas(), BorderLayout.SOUTH);
        return painelMapa;
    }

    private JLabel criarIndicadorVaga(String idVaga, StatusVaga status) {
        JLabel vaga = new JLabel(idVaga, SwingConstants.CENTER);
        vaga.setFont(FONTE_VAGA);
        vaga.setForeground(Color.WHITE);
        vaga.setOpaque(true);
        vaga.setPreferredSize(new Dimension(42, 28));
        vaga.setBackground(corStatus(status));
        vaga.setBorder(new EmptyBorder(6, 4, 6, 4));
        return vaga;
    }

    private JPanel criarLegendaVagas() {
        JPanel legenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        legenda.setBackground(BRANCO);
        legenda.setBorder(new EmptyBorder(0, 8, 8, 8));
        legenda.add(criarItemLegenda("Livre", VERDE_STATUS));
        legenda.add(criarItemLegenda("Ocupada", VERMELHO_STATUS));
        legenda.add(criarItemLegenda("Reservada", AMARELO_STATUS));
        return legenda;
    }

    private JPanel criarItemLegenda(String texto, Color cor) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        item.setBackground(BRANCO);

        JLabel marcador = new JLabel(" ");
        marcador.setOpaque(true);
        marcador.setBackground(cor);
        marcador.setPreferredSize(new Dimension(14, 14));

        JLabel label = new JLabel(texto);
        label.setFont(FONTE_CARD_TITULO);
        label.setForeground(TEXTO_SECUNDARIO);

        item.add(marcador);
        item.add(label);
        return item;
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

    private Color corStatus(StatusVaga status) {
        if (status == StatusVaga.LIVRE) {
            return VERDE_STATUS;
        }

        if (status == StatusVaga.OCUPADA) {
            return VERMELHO_STATUS;
        }

        if (status == StatusVaga.RESERVADA) {
            return AMARELO_STATUS;
        }

        return ROXO_FECHADO;
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

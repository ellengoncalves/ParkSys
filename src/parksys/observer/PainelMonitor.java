package parksys.observer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import parksys.entities.Vaga;
import parksys.enums.StatusVaga;

public class PainelMonitor extends JFrame implements EstacionamentoObserver {
    private static final long serialVersionUID = 1L;
    private static final Color FUNDO_CLARO = new Color(250, 247, 251);
    private static final Color BRANCO = Color.WHITE;
    private static final Color TEXTO_ESCURO = new Color(46, 46, 46);
    private static final Color TEXTO_SECUNDARIO = new Color(92, 82, 101);
    private static final Color ROXO_FECHADO = new Color(94, 58, 135);
    private static final Color LILAS_SUAVE = new Color(237, 231, 246);
    private static final Color VERDE_STATUS = new Color(45, 128, 91);
    private static final Color VERMELHO_STATUS = new Color(181, 82, 92);
    private static final Color AMARELO_STATUS = new Color(150, 112, 34);
    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_CABECALHO = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONTE_VAGA = new Font("Segoe UI", Font.BOLD, 14);

    private final Map<String, StatusVaga> statusVagas;
    private final JPanel gradeMapaVagas;
    private final Consumer<String> aoSelecionarVaga;
    private final Predicate<StatusVaga> podeSelecionarStatus;
    private String vagaSelecionada;

    public PainelMonitor() {
        this(null);
    }

    public PainelMonitor(Consumer<String> aoSelecionarVaga) {
        this(aoSelecionarVaga, status -> status == StatusVaga.LIVRE);
    }

    public PainelMonitor(Consumer<String> aoSelecionarVaga, Predicate<StatusVaga> podeSelecionarStatus) {
        this.statusVagas = new TreeMap<>();
        this.gradeMapaVagas = new JPanel(new GridLayout(10, 3, 4, 4));
        this.aoSelecionarVaga = aoSelecionarVaga;
        this.podeSelecionarStatus = podeSelecionarStatus;

        configurarJanela();
        montarComponentes();
    }

    @Override
    public void onVagaAlterada(String idVaga, StatusVaga novoStatus) {
        statusVagas.put(idVaga, novoStatus);

        SwingUtilities.invokeLater(() -> {
            atualizarMapaVisual();
            System.out.println("Vaga " + idVaga + " alterada para " + novoStatus.name());
        });
    }

    public StatusVaga getStatusVaga(String idVaga) {
        return statusVagas.get(idVaga);
    }

    public Map<String, StatusVaga> getStatusVagas() {
        return Collections.unmodifiableMap(statusVagas);
    }

    public void carregarStatusAtual(Map<String, Vaga> vagas) {
        statusVagas.clear();

        for (Map.Entry<String, Vaga> entrada : vagas.entrySet()) {
            statusVagas.put(entrada.getKey(), entrada.getValue().getStatus());
        }

        SwingUtilities.invokeLater(() -> atualizarMapaVisual());
    }

    private void configurarJanela() {
        setTitle("Monitor de vagas");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setMinimumSize(new Dimension(390, 560));
        setSize(440, 620);
        setLocationByPlatform(true);
    }

    private void montarComponentes() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(0, 18));
        painelPrincipal.setBackground(FUNDO_CLARO);
        painelPrincipal.setBorder(new EmptyBorder(22, 22, 22, 22));

        painelPrincipal.add(criarCabecalho(), BorderLayout.NORTH);
        painelPrincipal.add(criarPainelMapaVagas(), BorderLayout.CENTER);
        painelPrincipal.add(criarRodape(), BorderLayout.SOUTH);

        add(painelPrincipal, BorderLayout.CENTER);
        atualizarMapaVisual();
    }

    private void atualizarMapaVisual() {
        gradeMapaVagas.removeAll();

        if (statusVagas.isEmpty()) {
            JLabel vazio = new JLabel("Nenhuma alteracao registrada ainda.", SwingConstants.CENTER);
            vazio.setFont(FONTE_SUBTITULO);
            vazio.setForeground(TEXTO_SECUNDARIO);
            gradeMapaVagas.add(vazio);
            gradeMapaVagas.revalidate();
            gradeMapaVagas.repaint();
            return;
        }

        for (Map.Entry<String, StatusVaga> entrada : statusVagas.entrySet()) {
            gradeMapaVagas.add(criarIndicadorVaga(entrada.getKey(), entrada.getValue()));
        }

        gradeMapaVagas.revalidate();
        gradeMapaVagas.repaint();
    }

    private JPanel criarCabecalho() {
        JPanel painelCabecalho = new JPanel(new BorderLayout(0, 4));
        painelCabecalho.setBackground(FUNDO_CLARO);

        JLabel titulo = new JLabel("Monitor de vagas");
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(ROXO_FECHADO);

        String textoSubtitulo = aoSelecionarVaga == null
                ? "Acompanhe a situacao atual de cada vaga do estacionamento."
                : "Clique em uma vaga e confirme a selecao.";
        JLabel subtitulo = new JLabel(textoSubtitulo);
        subtitulo.setFont(FONTE_SUBTITULO);
        subtitulo.setForeground(TEXTO_SECUNDARIO);

        painelCabecalho.add(titulo, BorderLayout.NORTH);
        painelCabecalho.add(subtitulo, BorderLayout.CENTER);
        return painelCabecalho;
    }

    private JPanel criarPainelMapaVagas() {
        JPanel painelMapa = new JPanel(new BorderLayout());
        painelMapa.setBackground(BRANCO);
        painelMapa.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(LILAS_SUAVE, 1, true),
                new EmptyBorder(16, 18, 16, 18)));

        gradeMapaVagas.setBackground(BRANCO);
        painelMapa.add(gradeMapaVagas, BorderLayout.CENTER);
        return painelMapa;
    }

    private void adicionarIndicadorVaga(String idVaga) {
        StatusVaga status = statusVagas.get(idVaga);
        gradeMapaVagas.add(criarIndicadorVaga(idVaga, status));
    }

    private JLabel criarIndicadorVaga(String idVaga, StatusVaga status) {
        JLabel vaga = new JLabel(idVaga, SwingConstants.CENTER);
        vaga.setFont(FONTE_VAGA);
        vaga.setForeground(Color.WHITE);
        vaga.setOpaque(true);
        vaga.setPreferredSize(new Dimension(86, 28));
        vaga.setBackground(corStatusMapa(status));
        vaga.setBorder(criarBordaVaga(idVaga));
        vaga.setToolTipText(idVaga + " - " + descricaoStatus(status));
        vaga.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                selecionarVaga(idVaga);

                if (event.getClickCount() == 2 && aoSelecionarVaga != null) {
                    selecionarVagaAtual();
                }
            }
        });
        return vaga;
    }

    private javax.swing.border.Border criarBordaVaga(String idVaga) {
        if (idVaga.equals(vagaSelecionada)) {
            return BorderFactory.createCompoundBorder(
                    new LineBorder(ROXO_FECHADO, 3, true),
                new EmptyBorder(4, 4, 4, 4));
        }

        return BorderFactory.createCompoundBorder(
                new LineBorder(BRANCO, 2, true),
                new EmptyBorder(5, 4, 5, 4));
    }

    private void selecionarVaga(String idVaga) {
        vagaSelecionada = idVaga;
        atualizarMapaVisual();
    }

    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new BorderLayout(0, 10));
        rodape.setBackground(FUNDO_CLARO);
        rodape.add(criarLegenda(), BorderLayout.NORTH);

        if (aoSelecionarVaga != null) {
            rodape.add(criarPainelAcoes(), BorderLayout.CENTER);
        }

        return rodape;
    }

    private JPanel criarLegenda() {
        JPanel legenda = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        legenda.setBackground(FUNDO_CLARO);
        legenda.add(criarItemLegenda("Livre", VERDE_STATUS));
        legenda.add(criarItemLegenda("Ocupada", VERMELHO_STATUS));
        legenda.add(criarItemLegenda("Reservada", AMARELO_STATUS));
        return legenda;
    }

    private JPanel criarItemLegenda(String texto, Color cor) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        item.setBackground(FUNDO_CLARO);

        JLabel marcador = new JLabel(" ");
        marcador.setOpaque(true);
        marcador.setBackground(cor);
        marcador.setPreferredSize(new Dimension(14, 14));

        JLabel label = new JLabel(texto);
        label.setFont(FONTE_SUBTITULO);
        label.setForeground(TEXTO_SECUNDARIO);

        item.add(marcador);
        item.add(label);
        return item;
    }

    private JPanel criarPainelAcoes() {
        JButton botaoUsarVaga = new JButton("Usar vaga");
        estilizarBotao(botaoUsarVaga, ROXO_FECHADO, 112);
        botaoUsarVaga.addActionListener(event -> selecionarVagaAtual());

        JButton botaoFechar = new JButton("Fechar");
        estilizarBotao(botaoFechar, VERMELHO_STATUS, 96);
        botaoFechar.addActionListener(event -> dispose());

        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        painelAcoes.setBackground(FUNDO_CLARO);
        painelAcoes.add(botaoUsarVaga);
        painelAcoes.add(botaoFechar);
        return painelAcoes;
    }

    private void estilizarBotao(JButton botao, Color corFundo, int largura) {
        botao.setFont(FONTE_CABECALHO);
        botao.setForeground(Color.WHITE);
        botao.setBackground(corFundo);
        botao.setOpaque(true);
        botao.setFocusPainted(false);
        botao.setBorder(new EmptyBorder(10, 14, 10, 14));
        botao.setPreferredSize(new Dimension(largura, 40));
    }

    private void selecionarVagaAtual() {
        if (vagaSelecionada == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma vaga no mapa.",
                    "Selecionar vaga",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        StatusVaga status = statusVagas.get(vagaSelecionada);

        if (!podeSelecionarStatus.test(status)) {
            JOptionPane.showMessageDialog(
                    this,
                    "A vaga " + vagaSelecionada + " nao pode ser selecionada para este cadastro.",
                    "Selecionar vaga",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        aoSelecionarVaga.accept(vagaSelecionada);
        dispose();
    }

    private Color corStatusMapa(StatusVaga status) {
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

    private String descricaoStatus(StatusVaga status) {
        return status == null ? "Sem status" : status.getDescricao();
    }
}

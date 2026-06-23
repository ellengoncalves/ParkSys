package parksys.observer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

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
    private static final Color LILAS_CLARO = new Color(247, 243, 251);
    private static final Color VERDE_STATUS = new Color(45, 128, 91);
    private static final Color VERMELHO_STATUS = new Color(181, 82, 92);
    private static final Color AMARELO_STATUS = new Color(150, 112, 34);
    private static final Color VERDE_CLARO = new Color(232, 246, 239);
    private static final Color VERMELHO_CLARO = new Color(251, 235, 238);
    private static final Color AMARELO_CLARO = new Color(252, 244, 224);
    private static final Color SELECAO = new Color(237, 231, 246);
    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_TABELA = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONTE_CABECALHO = new Font("Segoe UI", Font.BOLD, 14);

    private final Map<String, StatusVaga> statusVagas;
    private final DefaultTableModel modeloTabela;
    private final JTable tabelaVagas;
    private final Consumer<String> aoSelecionarVaga;
    private final Predicate<StatusVaga> podeSelecionarStatus;

    public PainelMonitor() {
        this(null);
    }

    public PainelMonitor(Consumer<String> aoSelecionarVaga) {
        this(aoSelecionarVaga, status -> status == StatusVaga.LIVRE);
    }

    public PainelMonitor(Consumer<String> aoSelecionarVaga, Predicate<StatusVaga> podeSelecionarStatus) {
        this.statusVagas = new TreeMap<>();
        this.modeloTabela = criarModeloTabela();
        this.tabelaVagas = new JTable(modeloTabela);
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
        setTitle("Monitor de Vagas");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setMinimumSize(new Dimension(420, 480));
        setSize(480, 560);
        setLocationByPlatform(true);
    }

    private void montarComponentes() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(0, 16));
        painelPrincipal.setBackground(FUNDO_CLARO);
        painelPrincipal.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel painelCabecalho = criarCabecalho();

        tabelaVagas.setFont(FONTE_TABELA);
        tabelaVagas.setForeground(TEXTO_ESCURO);
        tabelaVagas.setBackground(BRANCO);
        tabelaVagas.setGridColor(LILAS_SUAVE);
        tabelaVagas.setRowHeight(32);
        tabelaVagas.setFillsViewportHeight(true);
        tabelaVagas.setCellSelectionEnabled(true);
        tabelaVagas.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tabelaVagas.setSelectionBackground(SELECAO);
        tabelaVagas.setSelectionForeground(TEXTO_ESCURO);
        tabelaVagas.setShowVerticalLines(false);
        tabelaVagas.setIntercellSpacing(new Dimension(0, 1));
        tabelaVagas.setDefaultRenderer(Object.class, new MonitorTableCellRenderer());
        tabelaVagas.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabelaVagas.getColumnModel().getColumn(0).setPreferredWidth(160);
        tabelaVagas.getColumnModel().getColumn(1).setPreferredWidth(160);
        tabelaVagas.getTableHeader().setFont(FONTE_CABECALHO);
        tabelaVagas.getTableHeader().setForeground(BRANCO);
        tabelaVagas.getTableHeader().setBackground(ROXO_FECHADO);
        tabelaVagas.getTableHeader().setReorderingAllowed(false);
        tabelaVagas.getTableHeader().setPreferredSize(new Dimension(0, 34));
        tabelaVagas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2 && aoSelecionarVaga != null) {
                    selecionarVagaAtual();
                }
            }
        });

        JScrollPane painelRolagem = new JScrollPane(tabelaVagas);
        painelRolagem.getViewport().setBackground(BRANCO);
        painelRolagem.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(LILAS_SUAVE, 1, true),
                "Status atual das vagas",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                FONTE_CABECALHO,
                ROXO_FECHADO));

        painelPrincipal.add(painelCabecalho, BorderLayout.NORTH);
        painelPrincipal.add(painelRolagem, BorderLayout.CENTER);
        painelPrincipal.add(criarRodape(), BorderLayout.SOUTH);

        add(painelPrincipal, BorderLayout.CENTER);
        atualizarMapaVisual();
    }

    private void atualizarMapaVisual() {
        modeloTabela.setRowCount(0);

        if (statusVagas.isEmpty()) {
            modeloTabela.addRow(new Object[] {"-", "Nenhuma alteracao registrada ainda"});
            return;
        }

        for (Map.Entry<String, StatusVaga> entrada : statusVagas.entrySet()) {
            modeloTabela.addRow(new Object[] {entrada.getKey(), entrada.getValue().getDescricao()});
        }
    }

    private DefaultTableModel criarModeloTabela() {
        return new DefaultTableModel(new Object[] {"Vaga", "Status"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JPanel criarCabecalho() {
        JPanel painelCabecalho = new JPanel(new BorderLayout(0, 4));
        painelCabecalho.setBackground(FUNDO_CLARO);

        JLabel titulo = new JLabel("Monitor de Vagas");
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(ROXO_FECHADO);

        String textoSubtitulo = aoSelecionarVaga == null
                ? "Acompanhe a situacao atual de cada vaga do estacionamento."
                : "Use duplo clique ou o botao para escolher uma vaga permitida.";
        JLabel subtitulo = new JLabel(textoSubtitulo);
        subtitulo.setFont(FONTE_SUBTITULO);
        subtitulo.setForeground(TEXTO_SECUNDARIO);

        painelCabecalho.add(titulo, BorderLayout.NORTH);
        painelCabecalho.add(subtitulo, BorderLayout.CENTER);
        return painelCabecalho;
    }

    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new BorderLayout(12, 0));
        rodape.setBackground(FUNDO_CLARO);
        rodape.add(criarLegenda(), BorderLayout.WEST);

        if (aoSelecionarVaga != null) {
            rodape.add(criarPainelAcoes(), BorderLayout.EAST);
        }

        return rodape;
    }

    private JPanel criarLegenda() {
        JPanel legenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
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
        JButton botaoUsarVaga = new JButton("Usar vaga selecionada");
        estilizarBotao(botaoUsarVaga, ROXO_FECHADO, 180);
        botaoUsarVaga.addActionListener(event -> selecionarVagaAtual());

        JButton botaoFechar = new JButton("Fechar");
        estilizarBotao(botaoFechar, VERMELHO_STATUS, 110);
        botaoFechar.addActionListener(event -> dispose());

        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
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
        botao.setBorder(new EmptyBorder(10, 16, 10, 16));
        botao.setPreferredSize(new Dimension(largura, 40));
    }

    private void selecionarVagaAtual() {
        int linhaSelecionada = tabelaVagas.getSelectedRow();

        if (linhaSelecionada < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma vaga livre para preencher o campo.",
                    "Selecionar vaga",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int linhaModelo = tabelaVagas.convertRowIndexToModel(linhaSelecionada);
        String idVaga = String.valueOf(modeloTabela.getValueAt(linhaModelo, 0));
        StatusVaga status = statusVagas.get(idVaga);

        if (!podeSelecionarStatus.test(status)) {
            JOptionPane.showMessageDialog(
                    this,
                    "A vaga " + idVaga + " nao pode ser selecionada para este cadastro.",
                    "Selecionar vaga",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        aoSelecionarVaga.accept(idVaga);
        dispose();
    }

    private static class MonitorTableCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBorder(new EmptyBorder(0, 12, 0, 12));
            setHorizontalAlignment(CENTER);

            if (!isSelected) {
                component.setBackground(row % 2 == 0 ? BRANCO : LILAS_CLARO);
                component.setForeground(column == 1 ? corStatus(String.valueOf(value)) : TEXTO_ESCURO);
                setFont(column == 1 ? FONTE_CABECALHO : FONTE_TABELA);

                if (column == 1) {
                    component.setBackground(corFundoStatus(String.valueOf(value)));
                }
            }

            return component;
        }

        private Color corStatus(String status) {
            if ("Livre".equalsIgnoreCase(status)) {
                return VERDE_STATUS;
            }

            if ("Ocupada".equalsIgnoreCase(status)) {
                return VERMELHO_STATUS;
            }

            if ("Reservada".equalsIgnoreCase(status)) {
                return AMARELO_STATUS;
            }

            return TEXTO_ESCURO;
        }

        private Color corFundoStatus(String status) {
            if ("Livre".equalsIgnoreCase(status)) {
                return VERDE_CLARO;
            }

            if ("Ocupada".equalsIgnoreCase(status)) {
                return VERMELHO_CLARO;
            }

            if ("Reservada".equalsIgnoreCase(status)) {
                return AMARELO_CLARO;
            }

            return BRANCO;
        }
    }
}

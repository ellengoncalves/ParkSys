package parksys.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import parksys.entities.Registro;
import parksys.entities.Vaga;
import parksys.entities.Veiculo;
import parksys.enums.StatusVaga;
import parksys.services.GerenciadorArquivo;
import parksys.services.GerenciadorEstacionamento;

public class TelaRelatorio extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final String CAMINHO_DADOS = "dados/parksys.ser";
    private static final Color ROXO_FECHADO = new Color(94, 58, 135);
    private static final Color LILAS_SUAVE = new Color(237, 231, 246);
    private static final Color ROSA_QUEIMADO = new Color(181, 101, 118);
    private static final Color FUNDO_CLARO = new Color(250, 247, 251);
    private static final Color TEXTO_ESCURO = new Color(46, 46, 46);
    private static final Color TEXTO_SECUNDARIO = new Color(92, 82, 101);
    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONTE_LABEL = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONTE_BOTAO = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONTE_TABELA = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_RESUMO = new Font("Segoe UI", Font.BOLD, 13);

    private static final DateTimeFormatter FORMATADOR_DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat FORMATADOR_MOEDA =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    private final GerenciadorEstacionamento gerenciador;
    private final JLabel labelVagasLivres;
    private final JLabel labelVagasOcupadas;
    private final JLabel labelVagasReservadas;
    private final JLabel labelVeiculosEmAberto;
    private final JLabel labelReceitaTotal;
    private final JLabel labelObservacao;
    private final DefaultTableModel modeloRegistrosDia;
    private final DefaultTableModel modeloRegistrosReceita;

    public TelaRelatorio() {
        this.gerenciador = GerenciadorEstacionamento.getInstance();
        this.labelVagasLivres = criarLabelResumo();
        this.labelVagasOcupadas = criarLabelResumo();
        this.labelVagasReservadas = criarLabelResumo();
        this.labelVeiculosEmAberto = criarLabelResumo();
        this.labelReceitaTotal = criarLabelResumo();
        this.labelObservacao = criarLabelResumo();
        this.modeloRegistrosDia = criarModeloRegistros();
        this.modeloRegistrosReceita = criarModeloRegistros();

        configurarJanela();
        montarComponentes();
        atualizarRelatorio();
    }

    private void configurarJanela() {
        setTitle("Relat\u00f3rio do Estacionamento");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(900, 620));
        setSize(980, 680);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                salvarDados();
            }
        });
    }

    private void montarComponentes() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(0, 22));
        painelPrincipal.setBackground(FUNDO_CLARO);
        painelPrincipal.setBorder(new EmptyBorder(30, 38, 30, 38));

        JPanel painelCabecalho = criarCabecalho(
                "Relat\u00f3rio do Estacionamento",
                "Acompanhe vagas, receita e registros do dia.");

        JPanel painelResumo = criarPainelResumo();
        JTabbedPane abasRegistros = criarAbasRegistros();

        JPanel painelConteudo = new JPanel(new BorderLayout(0, 16));
        painelConteudo.setBackground(FUNDO_CLARO);
        painelConteudo.add(painelResumo, BorderLayout.NORTH);
        painelConteudo.add(abasRegistros, BorderLayout.CENTER);
        painelConteudo.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(LILAS_SUAVE, 1, true),
                "Resumo e registros",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                FONTE_LABEL,
                ROXO_FECHADO));

        JButton botaoAtualizar = new JButton("Atualizar");
        estilizarBotao(botaoAtualizar, ROXO_FECHADO);
        botaoAtualizar.addActionListener(event -> atualizarRelatorio());

        JButton botaoFechar = new JButton("Fechar");
        estilizarBotao(botaoFechar, ROSA_QUEIMADO);
        botaoFechar.addActionListener(event -> fecharJanela());

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painelBotoes.setBackground(FUNDO_CLARO);
        painelBotoes.add(botaoAtualizar);
        painelBotoes.add(botaoFechar);

        painelPrincipal.add(painelCabecalho, BorderLayout.NORTH);
        painelPrincipal.add(painelConteudo, BorderLayout.CENTER);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);
        add(painelPrincipal, BorderLayout.CENTER);
    }

    private JPanel criarCabecalho(String titulo, String subtitulo) {
        JPanel painelCabecalho = new JPanel(new BorderLayout(0, 6));
        painelCabecalho.setBackground(FUNDO_CLARO);

        JLabel labelTitulo = new JLabel(titulo, SwingConstants.LEFT);
        labelTitulo.setFont(FONTE_TITULO);
        labelTitulo.setForeground(ROXO_FECHADO);

        JLabel labelSubtitulo = new JLabel(subtitulo, SwingConstants.LEFT);
        labelSubtitulo.setFont(FONTE_SUBTITULO);
        labelSubtitulo.setForeground(TEXTO_SECUNDARIO);

        painelCabecalho.add(labelTitulo, BorderLayout.NORTH);
        painelCabecalho.add(labelSubtitulo, BorderLayout.CENTER);
        return painelCabecalho;
    }

    private void estilizarBotao(JButton botao, Color corFundo) {
        botao.setFont(FONTE_BOTAO);
        botao.setForeground(Color.WHITE);
        botao.setBackground(corFundo);
        botao.setOpaque(true);
        botao.setFocusPainted(false);
        botao.setBorder(new EmptyBorder(12, 24, 12, 24));
        botao.setPreferredSize(new Dimension(140, 44));
    }

    private void atualizarRelatorio() {
        modeloRegistrosDia.setRowCount(0);
        modeloRegistrosReceita.setRowCount(0);

        int vagasLivres = 0;
        int vagasOcupadas = 0;
        int vagasReservadas = 0;

        for (Map.Entry<String, Vaga> entradaVaga : gerenciador.getVagas().entrySet()) {
            StatusVaga status = entradaVaga.getValue().getStatus();

            if (status == StatusVaga.LIVRE) {
                vagasLivres++;
            } else if (status == StatusVaga.OCUPADA) {
                vagasOcupadas++;
            } else if (status == StatusVaga.RESERVADA) {
                vagasReservadas++;
            }
        }

        double receitaTotal = 0.0;
        int veiculosEmAberto = 0;
        LocalDate hoje = LocalDate.now();

        for (Registro registro : gerenciador.getRegistrosOrdenados()) {
            receitaTotal += registro.getValorPago();

            if (registro.getDataSaida() == null) {
                veiculosEmAberto++;
            }

            if (registro.getDataEntrada() != null
                    && registro.getDataEntrada().toLocalDate().equals(hoje)) {
                modeloRegistrosDia.addRow(criarLinhaRegistro(registro));
            }
        }

        for (Registro registro : gerenciador.getRegistrosPorReceitaDecrescente()) {
            modeloRegistrosReceita.addRow(criarLinhaRegistro(registro));
        }

        labelVagasLivres.setText("Vagas livres: " + vagasLivres);
        labelVagasOcupadas.setText("Vagas ocupadas: " + vagasOcupadas + " (espacos fisicos)");
        labelVagasReservadas.setText("Vagas reservadas: " + vagasReservadas);
        labelVeiculosEmAberto.setText("Veiculos em aberto: " + veiculosEmAberto);
        labelReceitaTotal.setText("Receita total: " + FORMATADOR_MOEDA.format(receitaTotal));
        labelObservacao.setText("Moto/carro ocupam 1 vaga, SUV ocupa 2 e caminhao ocupa 3.");
    }

    private Object[] criarLinhaRegistro(Registro registro) {
        Veiculo veiculo = registro.getVeiculo();
        String status = registro.getDataSaida() == null ? "[EM ABERTO]" : "[FINALIZADO]";
        String placa = veiculo != null ? veiculo.getPlaca() : "-";
        String tipo = veiculo != null ? veiculo.getTipo().toString() : "-";
        int vagasUsadas = veiculo != null && veiculo.getTipo() != null ? veiculo.getTipo().getVagasOcupadas() : 0;
        String vagas = formatarVagasOcupadas(registro.getIdVaga(), vagasUsadas);
        String entrada = registro.getDataEntrada() != null
                ? registro.getDataEntrada().format(FORMATADOR_DATA_HORA)
                : "-";
        String saida = registro.getDataSaida() != null
                ? registro.getDataSaida().format(FORMATADOR_DATA_HORA)
                : "-";

        return new Object[] {
                status,
                vagas,
                placa,
                tipo,
                vagasUsadas,
                entrada,
                saida,
                FORMATADOR_MOEDA.format(registro.getValorPago())
        };
    }

    private String formatarVagasOcupadas(String idVagaInicial, int vagasUsadas) {
        String idNormalizado = Objects.toString(idVagaInicial, "-").toUpperCase(Locale.ROOT);

        if (vagasUsadas <= 1 || !idNormalizado.matches("[A-Z][0-9]{2}")) {
            return idNormalizado;
        }

        char fileira = idNormalizado.charAt(0);
        int numeroInicial = Integer.parseInt(idNormalizado.substring(1));
        int numeroFinal = numeroInicial + vagasUsadas - 1;
        return String.format("%s-%c%02d", idNormalizado, fileira, numeroFinal);
    }

    private JPanel criarPainelResumo() {
        JPanel painelResumo = new JPanel(new GridLayout(2, 3, 12, 8));
        painelResumo.setBackground(FUNDO_CLARO);
        painelResumo.setBorder(new EmptyBorder(12, 12, 0, 12));
        painelResumo.add(labelVagasLivres);
        painelResumo.add(labelVagasOcupadas);
        painelResumo.add(labelVagasReservadas);
        painelResumo.add(labelVeiculosEmAberto);
        painelResumo.add(labelReceitaTotal);
        painelResumo.add(labelObservacao);
        return painelResumo;
    }

    private JTabbedPane criarAbasRegistros() {
        JTabbedPane abas = new JTabbedPane();
        abas.setFont(FONTE_LABEL);
        abas.addTab("Registros do dia", new JScrollPane(criarTabelaRegistros(modeloRegistrosDia)));
        abas.addTab("Receita decrescente", new JScrollPane(criarTabelaRegistros(modeloRegistrosReceita)));
        return abas;
    }

    private JTable criarTabelaRegistros(DefaultTableModel modelo) {
        JTable tabela = new JTable(modelo);
        tabela.setFont(FONTE_TABELA);
        tabela.setForeground(TEXTO_ESCURO);
        tabela.setRowHeight(28);
        tabela.setFillsViewportHeight(true);
        tabela.setAutoCreateRowSorter(true);
        tabela.getTableHeader().setFont(FONTE_LABEL);
        tabela.getTableHeader().setForeground(ROXO_FECHADO);
        tabela.getTableHeader().setReorderingAllowed(false);
        return tabela;
    }

    private DefaultTableModel criarModeloRegistros() {
        return new DefaultTableModel(
                new Object[] {"Status", "Vaga(s)", "Placa", "Tipo", "Qtd. vagas", "Entrada", "Saida", "Valor"},
                0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JLabel criarLabelResumo() {
        JLabel label = new JLabel();
        label.setFont(FONTE_RESUMO);
        label.setForeground(TEXTO_ESCURO);
        return label;
    }

    private void fecharJanela() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private void salvarDados() {
        GerenciadorArquivo.serializar(
                gerenciador.getVagas(),
                gerenciador.getRegistros(),
                gerenciador.getMensalistas(),
                CAMINHO_DADOS);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaRelatorio().setVisible(true));
    }
}

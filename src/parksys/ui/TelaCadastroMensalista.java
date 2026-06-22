package parksys.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import parksys.entities.Mensalista;
import parksys.enums.TipoVeiculo;
import parksys.services.GerenciadorArquivo;
import parksys.services.GerenciadorEstacionamento;

public class TelaCadastroMensalista extends JFrame {
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
    private static final Font FONTE_PADRAO = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font FONTE_LABEL = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONTE_BOTAO = new Font("Segoe UI", Font.BOLD, 15);

    private final GerenciadorEstacionamento gerenciador;
    private final JTextField campoNome;
    private final JTextField campoDocumento;
    private final JTextField campoTelefone;
    private final JTextField campoPlaca;
    private final JComboBox<TipoVeiculo> comboTipoVeiculo;
    private final PainelDesenhoVeiculo painelDesenhoVeiculo;
    private final JTextField campoVagaReservada;
    private final JTextField campoValorMensalidade;
    private Border bordaCampoPadrao;

    public TelaCadastroMensalista() {
        this.gerenciador = GerenciadorEstacionamento.getInstance();
        this.campoNome = new JTextField(18);
        this.campoDocumento = new JTextField(14);
        this.campoTelefone = new JTextField(12);
        this.campoPlaca = new JTextField(12);
        this.comboTipoVeiculo = new JComboBox<>();
        this.painelDesenhoVeiculo = new PainelDesenhoVeiculo();
        this.campoVagaReservada = new JTextField(8);
        this.campoValorMensalidade = new JTextField(8);

        configurarJanela();
        preencherTiposVeiculo();
        montarComponentes();
    }

    private void configurarJanela() {
        setTitle("Cadastro de Mensalista");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(960, 620));
        setSize(1020, 660);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                salvarDados();
            }
        });
    }

    private void preencherTiposVeiculo() {
        for (TipoVeiculo tipoVeiculo : TipoVeiculo.values()) {
            comboTipoVeiculo.addItem(tipoVeiculo);
        }
    }

    private void montarComponentes() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(0, 22));
        painelPrincipal.setBackground(FUNDO_CLARO);
        painelPrincipal.setBorder(new EmptyBorder(30, 38, 30, 38));

        JPanel painelCabecalho = criarCabecalho(
                "Cadastro de Mensalista",
                "Registre os dados do cliente, ve\u00edculo e mensalidade.");

        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBackground(Color.WHITE);
        painelFormulario.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(LILAS_SUAVE, 1, true),
                "Dados do mensalista",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                FONTE_LABEL,
                ROXO_FECHADO));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(9, 14, 9, 14);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        estilizarCampo(campoNome);
        estilizarCampo(campoDocumento);
        estilizarCampo(campoTelefone);
        estilizarCampo(campoPlaca);
        estilizarCampo(campoVagaReservada);
        estilizarCampo(campoValorMensalidade);
        comboTipoVeiculo.setFont(FONTE_PADRAO);
        comboTipoVeiculo.setPreferredSize(new Dimension(390, 36));
        comboTipoVeiculo.addActionListener(event -> atualizarDesenhoVeiculo());
        atualizarDesenhoVeiculo();

        adicionarCampo(painelFormulario, constraints, 0, "Nome:", campoNome);
        adicionarCampo(painelFormulario, constraints, 1, "Documento:", campoDocumento);
        adicionarCampo(painelFormulario, constraints, 2, "Telefone:", campoTelefone);
        adicionarCampo(
                painelFormulario,
                constraints,
                3,
                "Placa:",
                FormularioHelper.criarCampoPlacaComAjuda(campoPlaca, bordaCampoPadrao));
        adicionarCampo(painelFormulario, constraints, 4, "Tipo de ve\u00edculo:", comboTipoVeiculo);
        adicionarCampo(painelFormulario, constraints, 5, "Vaga reservada:", campoVagaReservada);
        adicionarCampo(painelFormulario, constraints, 6, "Mensalidade:", campoValorMensalidade);

        JPanel painelConteudo = new JPanel(new BorderLayout(18, 0));
        painelConteudo.setOpaque(false);
        painelConteudo.add(painelFormulario, BorderLayout.CENTER);
        painelConteudo.add(painelDesenhoVeiculo, BorderLayout.EAST);

        JButton botaoCadastrar = new JButton("Cadastrar");
        estilizarBotao(botaoCadastrar);
        botaoCadastrar.addActionListener(event -> cadastrarMensalista());

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        painelBotoes.setBackground(FUNDO_CLARO);
        painelBotoes.add(botaoCadastrar);

        painelPrincipal.add(painelCabecalho, BorderLayout.NORTH);
        painelPrincipal.add(painelConteudo, BorderLayout.CENTER);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);
        add(painelPrincipal, BorderLayout.CENTER);
    }

    private JPanel criarCabecalho(String titulo, String subtitulo) {
        JPanel painelCabecalho = new JPanel(new BorderLayout(0, 6));
        painelCabecalho.setBackground(FUNDO_CLARO);

        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setFont(FONTE_TITULO);
        labelTitulo.setForeground(ROXO_FECHADO);

        JLabel labelSubtitulo = new JLabel(subtitulo);
        labelSubtitulo.setFont(FONTE_SUBTITULO);
        labelSubtitulo.setForeground(TEXTO_SECUNDARIO);

        painelCabecalho.add(labelTitulo, BorderLayout.NORTH);
        painelCabecalho.add(labelSubtitulo, BorderLayout.CENTER);
        return painelCabecalho;
    }

    private void adicionarCampo(
            JPanel painel,
            GridBagConstraints constraints,
            int linha,
            String rotulo,
            java.awt.Component campo) {
        constraints.gridx = 0;
        constraints.gridy = linha;
        constraints.weightx = 0;
        constraints.ipadx = 6;
        JLabel label = new JLabel(rotulo);
        label.setFont(FONTE_LABEL);
        label.setForeground(TEXTO_ESCURO);
        painel.add(label, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.ipadx = 0;
        painel.add(campo, constraints);
    }

    private void estilizarCampo(JTextField campo) {
        campo.setFont(FONTE_PADRAO);
        campo.setForeground(TEXTO_ESCURO);
        campo.setPreferredSize(new Dimension(390, 36));
        bordaCampoPadrao = BorderFactory.createCompoundBorder(
                new LineBorder(LILAS_SUAVE, 1, true),
                new EmptyBorder(8, 10, 8, 10));
        campo.setBorder(bordaCampoPadrao);
    }

    private void estilizarBotao(JButton botao) {
        botao.setFont(FONTE_BOTAO);
        botao.setForeground(Color.WHITE);
        botao.setBackground(ROSA_QUEIMADO);
        botao.setOpaque(true);
        botao.setFocusPainted(false);
        botao.setBorder(new EmptyBorder(12, 24, 12, 24));
        botao.setPreferredSize(new Dimension(160, 44));
    }

    private void atualizarDesenhoVeiculo() {
        painelDesenhoVeiculo.setTipoVeiculo((TipoVeiculo) comboTipoVeiculo.getSelectedItem());
    }

    private void cadastrarMensalista() {
        String nome = campoNome.getText();
        String documento = campoDocumento.getText();
        String telefone = campoTelefone.getText();
        String placa = campoPlaca.getText();
        TipoVeiculo tipoVeiculo = (TipoVeiculo) comboTipoVeiculo.getSelectedItem();
        String idVagaReservada = campoVagaReservada.getText();

        try {
            double valorMensalidade = Double.parseDouble(campoValorMensalidade.getText().replace(',', '.'));
            Mensalista mensalista = new Mensalista(
                    nome,
                    documento,
                    telefone,
                    placa,
                    tipoVeiculo,
                    idVagaReservada,
                    valorMensalidade);

            gerenciador.cadastrarMensalista(mensalista);
            JOptionPane.showMessageDialog(
                    this,
                    "Mensalista cadastrado com sucesso.",
                    "Cadastro de Mensalista",
                    JOptionPane.INFORMATION_MESSAGE);
            salvarDados();
            dispose();
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "Erro ao cadastrar mensalista",
                    JOptionPane.ERROR_MESSAGE);
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
        SwingUtilities.invokeLater(() -> new TelaCadastroMensalista().setVisible(true));
    }
}

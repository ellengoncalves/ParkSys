package parksys.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import parksys.entities.Mensalista;
import parksys.enums.TipoVeiculo;
import parksys.services.GerenciadorArquivo;
import parksys.services.GerenciadorEstacionamento;

public class TelaCadastroMensalista extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final String CAMINHO_DADOS = "dados/parksys.ser";

    private final GerenciadorEstacionamento gerenciador;
    private final JTextField campoNome;
    private final JTextField campoDocumento;
    private final JTextField campoTelefone;
    private final JTextField campoPlaca;
    private final JComboBox<TipoVeiculo> comboTipoVeiculo;
    private final JTextField campoVagaReservada;
    private final JTextField campoValorMensalidade;

    public TelaCadastroMensalista() {
        this.gerenciador = GerenciadorEstacionamento.getInstance();
        this.campoNome = new JTextField(18);
        this.campoDocumento = new JTextField(14);
        this.campoTelefone = new JTextField(12);
        this.campoPlaca = new JTextField(12);
        this.comboTipoVeiculo = new JComboBox<>();
        this.campoVagaReservada = new JTextField(8);
        this.campoValorMensalidade = new JTextField(8);

        configurarJanela();
        preencherTiposVeiculo();
        montarComponentes();
    }

    private void configurarJanela() {
        setTitle("Cadastro de Mensalista");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(420, 300);
        setLocationRelativeTo(null);
        setResizable(false);

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
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 8, 6, 8);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        adicionarCampo(painelFormulario, constraints, 0, "Nome:", campoNome);
        adicionarCampo(painelFormulario, constraints, 1, "Documento:", campoDocumento);
        adicionarCampo(painelFormulario, constraints, 2, "Telefone:", campoTelefone);
        adicionarCampo(painelFormulario, constraints, 3, "Placa:", campoPlaca);
        adicionarCampo(painelFormulario, constraints, 4, "Tipo:", comboTipoVeiculo);
        adicionarCampo(painelFormulario, constraints, 5, "Vaga reservada:", campoVagaReservada);
        adicionarCampo(painelFormulario, constraints, 6, "Mensalidade:", campoValorMensalidade);

        JButton botaoCadastrar = new JButton("Cadastrar");
        botaoCadastrar.addActionListener(event -> cadastrarMensalista());

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.add(botaoCadastrar);

        add(painelFormulario, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);
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
        painel.add(new JLabel(rotulo), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        painel.add(campo, constraints);
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
            limparCampos();
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "Erro ao cadastrar mensalista",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        campoNome.setText("");
        campoDocumento.setText("");
        campoTelefone.setText("");
        campoPlaca.setText("");
        campoVagaReservada.setText("");
        campoValorMensalidade.setText("");
        campoNome.requestFocusInWindow();
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

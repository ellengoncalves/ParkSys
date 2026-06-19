package parksys.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import parksys.enums.TipoVeiculo;
import parksys.services.GerenciadorEstacionamento;

public class TelaRegistroEntrada extends JFrame {
    private static final long serialVersionUID = 1L;

    private final GerenciadorEstacionamento gerenciador;
    private final JTextField campoPlaca;
    private final JTextField campoVaga;
    private final JComboBox<TipoVeiculo> comboTipoVeiculo;

    public TelaRegistroEntrada() {
        this.gerenciador = GerenciadorEstacionamento.getInstance();
        this.campoPlaca = new JTextField(12);
        this.campoVaga = new JTextField(6);
        this.comboTipoVeiculo = new JComboBox<>();

        configurarJanela();
        preencherTiposVeiculo();
        montarComponentes();
    }

    private void configurarJanela() {
        setTitle("Registro de Entrada");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(360, 190);
        setLocationRelativeTo(null);
        setResizable(false);
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

        adicionarCampo(painelFormulario, constraints, 0, "Placa:", campoPlaca);
        adicionarCampo(painelFormulario, constraints, 1, "Tipo:", comboTipoVeiculo);
        adicionarCampo(painelFormulario, constraints, 2, "Vaga desejada:", campoVaga);

        JButton botaoRegistrar = new JButton("Registrar entrada");
        botaoRegistrar.addActionListener(event -> registrarEntrada());

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.add(botaoRegistrar);

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

    private void registrarEntrada() {
        String placa = campoPlaca.getText();
        String idVaga = campoVaga.getText();
        TipoVeiculo tipoVeiculo = (TipoVeiculo) comboTipoVeiculo.getSelectedItem();

        try {
            gerenciador.registrarEntrada(placa, tipoVeiculo, idVaga);
            JOptionPane.showMessageDialog(
                    this,
                    "Entrada registrada com sucesso.",
                    "Registro de Entrada",
                    JOptionPane.INFORMATION_MESSAGE);
            limparCampos();
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "Erro ao registrar entrada",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        campoPlaca.setText("");
        campoVaga.setText("");
        campoPlaca.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaRegistroEntrada().setVisible(true));
    }
}

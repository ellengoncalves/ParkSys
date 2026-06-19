package parksys.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import parksys.services.GerenciadorArquivo;
import parksys.services.GerenciadorEstacionamento;

public class TelaSaida extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final String CAMINHO_DADOS = "dados/parksys.ser";

    private final GerenciadorEstacionamento gerenciador;
    private final JTextField campoPlaca;

    public TelaSaida() {
        this.gerenciador = GerenciadorEstacionamento.getInstance();
        this.campoPlaca = new JTextField(12);

        configurarJanela();
        montarComponentes();
    }

    private void configurarJanela() {
        setTitle("Registro de Saida");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(340, 140);
        setLocationRelativeTo(null);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                salvarDados();
            }
        });
    }

    private void montarComponentes() {
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 8, 6, 8);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        adicionarCampo(painelFormulario, constraints, 0, "Placa:", campoPlaca);

        JButton botaoRegistrar = new JButton("Registrar saida");
        botaoRegistrar.addActionListener(event -> registrarSaida());

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

    private void registrarSaida() {
        String placa = campoPlaca.getText();

        try {
            gerenciador.registrarSaida(placa);
            JOptionPane.showMessageDialog(
                    this,
                    "Saida registrada com sucesso.",
                    "Registro de Saida",
                    JOptionPane.INFORMATION_MESSAGE);
            limparCampos();
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "Erro ao registrar saida",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        campoPlaca.setText("");
        campoPlaca.requestFocusInWindow();
    }

    private void salvarDados() {
        GerenciadorArquivo.serializar(
                gerenciador.getVagas(),
                gerenciador.getRegistros(),
                gerenciador.getMensalistas(),
                CAMINHO_DADOS);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaSaida().setVisible(true));
    }
}

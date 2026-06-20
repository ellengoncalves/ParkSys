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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import parksys.services.GerenciadorArquivo;
import parksys.services.GerenciadorEstacionamento;

public class TelaSaida extends JFrame {
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
    private final JTextField campoPlaca;

    public TelaSaida() {
        this.gerenciador = GerenciadorEstacionamento.getInstance();
        this.campoPlaca = new JTextField(12);

        configurarJanela();
        montarComponentes();
    }

    private void configurarJanela() {
        setTitle("Registro de Sa\u00edda");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(680, 360));
        setSize(720, 400);
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
                "Registrar Sa\u00edda",
                "Informe a placa do ve\u00edculo para finalizar o registro.");

        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBackground(Color.WHITE);
        painelFormulario.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(LILAS_SUAVE, 1, true),
                "Dados da sa\u00edda",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                FONTE_LABEL,
                ROXO_FECHADO));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(14, 14, 14, 14);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        estilizarCampo(campoPlaca);
        adicionarCampo(painelFormulario, constraints, 0, "Placa:", campoPlaca);

        JButton botaoRegistrar = new JButton("Registrar Sa\u00edda");
        estilizarBotao(botaoRegistrar);
        botaoRegistrar.addActionListener(event -> registrarSaida());

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        painelBotoes.setBackground(FUNDO_CLARO);
        painelBotoes.add(botaoRegistrar);

        painelPrincipal.add(painelCabecalho, BorderLayout.NORTH);
        painelPrincipal.add(painelFormulario, BorderLayout.CENTER);
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
        campo.setPreferredSize(new Dimension(360, 36));
        campo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(LILAS_SUAVE, 1, true),
                new EmptyBorder(8, 10, 8, 10)));
    }

    private void estilizarBotao(JButton botao) {
        botao.setFont(FONTE_BOTAO);
        botao.setForeground(Color.WHITE);
        botao.setBackground(ROSA_QUEIMADO);
        botao.setOpaque(true);
        botao.setFocusPainted(false);
        botao.setBorder(new EmptyBorder(12, 24, 12, 24));
        botao.setPreferredSize(new Dimension(180, 44));
    }

    private void registrarSaida() {
        String placa = campoPlaca.getText();

        try {
            gerenciador.registrarSaida(placa);
            JOptionPane.showMessageDialog(
                    this,
                    "Sa\u00edda registrada com sucesso.",
                    "Registro de Sa\u00edda",
                    JOptionPane.INFORMATION_MESSAGE);
            salvarDados();
            dispose();
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "Erro ao registrar sa\u00edda",
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
        SwingUtilities.invokeLater(() -> new TelaSaida().setVisible(true));
    }
}

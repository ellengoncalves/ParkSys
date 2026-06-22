package parksys.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import parksys.observer.PainelMonitor;
import parksys.services.GerenciadorEstacionamento;

final class FormularioHelper {
    private static final String AJUDA_PLACA = "Formatos aceitos: ABC1234 ou ABC1D23";
    private static final String TOOLTIP_PLACA =
            "Digite a placa no formato antigo ABC1234 ou Mercosul ABC1D23.";
    private static final Color COR_ERRO = new Color(190, 60, 72);
    private static final Color COR_AJUDA = new Color(92, 82, 101);
    private static final Font FONTE_AJUDA = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONTE_BOTAO_ACAO = new Font("Segoe UI", Font.BOLD, 13);

    private FormularioHelper() {
    }

    static JPanel criarCampoPlacaComAjuda(JTextField campoPlaca, Border bordaPadrao) {
        configurarCampoPlaca(campoPlaca, bordaPadrao);
        return criarPainelCampoComAjuda(campoPlaca, AJUDA_PLACA);
    }

    static JPanel criarCampoComAjuda(JTextField campo, String ajuda, String tooltip) {
        campo.setToolTipText(tooltip);
        return criarPainelCampoComAjuda(campo, ajuda);
    }

    static JPanel criarCampoComAcaoEAjuda(JTextField campo, JButton botaoAcao, String ajuda, String tooltip) {
        campo.setToolTipText(tooltip);

        JPanel linhaCampo = new JPanel(new BorderLayout(8, 0));
        linhaCampo.setOpaque(false);
        linhaCampo.add(campo, BorderLayout.CENTER);
        linhaCampo.add(botaoAcao, BorderLayout.EAST);

        JLabel textoAjuda = new JLabel(ajuda);
        textoAjuda.setFont(FONTE_AJUDA);
        textoAjuda.setForeground(COR_AJUDA);
        textoAjuda.setBorder(new EmptyBorder(5, 2, 0, 0));

        JPanel painelCampo = new JPanel(new BorderLayout());
        painelCampo.setOpaque(false);
        painelCampo.add(linhaCampo, BorderLayout.NORTH);
        painelCampo.add(textoAjuda, BorderLayout.CENTER);
        return painelCampo;
    }

    static JButton criarBotaoVerVagas(Color corFundo) {
        JButton botao = new JButton("Ver vagas");
        botao.setFont(FONTE_BOTAO_ACAO);
        botao.setForeground(Color.WHITE);
        botao.setBackground(corFundo);
        botao.setOpaque(true);
        botao.setFocusPainted(false);
        botao.setBorder(new EmptyBorder(8, 14, 8, 14));
        botao.setPreferredSize(new Dimension(112, 36));
        return botao;
    }

    static void abrirMonitorVagas(JFrame janelaOrigem, GerenciadorEstacionamento gerenciador, JTextField campoVaga) {
        PainelMonitor painelMonitor = new PainelMonitor(vagaSelecionada -> campoVaga.setText(vagaSelecionada));
        painelMonitor.carregarStatusAtual(gerenciador.getVagas());
        gerenciador.addObserver(painelMonitor);
        painelMonitor.addWindowListener(new WindowAdapter() {
            private boolean removido;

            @Override
            public void windowClosing(WindowEvent event) {
                removerObserver();
            }

            @Override
            public void windowClosed(WindowEvent event) {
                removerObserver();
            }

            private void removerObserver() {
                if (!removido) {
                    gerenciador.removeObserver(painelMonitor);
                    removido = true;
                }
            }
        });
        painelMonitor.setLocationRelativeTo(janelaOrigem);
        painelMonitor.setVisible(true);
    }

    private static JPanel criarPainelCampoComAjuda(JTextField campo, String ajuda) {
        JLabel textoAjuda = new JLabel(ajuda);
        textoAjuda.setFont(FONTE_AJUDA);
        textoAjuda.setForeground(COR_AJUDA);
        textoAjuda.setBorder(new EmptyBorder(5, 2, 0, 0));

        JPanel painelCampo = new JPanel(new BorderLayout());
        painelCampo.setOpaque(false);
        painelCampo.add(campo, BorderLayout.NORTH);
        painelCampo.add(textoAjuda, BorderLayout.CENTER);
        return painelCampo;
    }

    private static void configurarCampoPlaca(JTextField campoPlaca, Border bordaPadrao) {
        campoPlaca.setToolTipText(TOOLTIP_PLACA);

        ((AbstractDocument) campoPlaca.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String texto, AttributeSet attrs)
                    throws BadLocationException {
                super.insertString(fb, offset, normalizarDigitacao(texto), attrs);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String texto, AttributeSet attrs)
                    throws BadLocationException {
                super.replace(fb, offset, length, normalizarDigitacao(texto), attrs);
            }
        });

        campoPlaca.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                atualizarBorda(campoPlaca, bordaPadrao);
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                atualizarBorda(campoPlaca, bordaPadrao);
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                atualizarBorda(campoPlaca, bordaPadrao);
            }
        });
    }

    private static String normalizarDigitacao(String texto) {
        return texto == null ? "" : texto.toUpperCase();
    }

    private static void atualizarBorda(JTextField campoPlaca, Border bordaPadrao) {
        String placa = campoPlaca.getText().trim().replace("-", "").replace(" ", "").toUpperCase();

        if (placa.isEmpty() || placa.matches("[A-Z]{3}[0-9]{4}") || placa.matches("[A-Z]{3}[0-9][A-Z][0-9]{2}")) {
            campoPlaca.setBorder(bordaPadrao);
            return;
        }

        campoPlaca.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COR_ERRO, 1, true),
                new EmptyBorder(8, 10, 8, 10)));
    }
}

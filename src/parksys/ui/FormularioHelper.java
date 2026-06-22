package parksys.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
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

final class FormularioHelper {
    private static final String AJUDA_PLACA = "Formatos aceitos: ABC1234 ou ABC1D23";
    private static final String TOOLTIP_PLACA =
            "Digite a placa no formato antigo ABC1234 ou Mercosul ABC1D23.";
    private static final Color COR_ERRO = new Color(190, 60, 72);
    private static final Color COR_AJUDA = new Color(92, 82, 101);
    private static final Font FONTE_AJUDA = new Font("Segoe UI", Font.PLAIN, 12);

    private FormularioHelper() {
    }

    static JPanel criarCampoPlacaComAjuda(JTextField campoPlaca, Border bordaPadrao) {
        configurarCampoPlaca(campoPlaca, bordaPadrao);

        JLabel textoAjuda = new JLabel(AJUDA_PLACA);
        textoAjuda.setFont(FONTE_AJUDA);
        textoAjuda.setForeground(COR_AJUDA);
        textoAjuda.setBorder(new EmptyBorder(5, 2, 0, 0));

        JPanel painelCampo = new JPanel(new BorderLayout());
        painelCampo.setOpaque(false);
        painelCampo.add(campoPlaca, BorderLayout.NORTH);
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

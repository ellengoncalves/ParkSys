package parksys.main;

import javax.swing.SwingUtilities;

import parksys.ui.TelaInicial;

public class Principal {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaInicial().setVisible(true));
    }
}

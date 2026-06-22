package parksys.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;

import parksys.enums.TipoVeiculo;

class PainelDesenhoVeiculo extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Color FUNDO = new Color(247, 243, 251);
    private static final Color BORDA = new Color(237, 231, 246);
    private static final Color ROXO = new Color(94, 58, 135);
    private static final Color ROSA = new Color(181, 101, 118);
    private static final Color TEXTO = new Color(46, 46, 46);
    private static final Color CINZA = new Color(90, 90, 96);
    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONTE_AJUDA = new Font("Segoe UI", Font.PLAIN, 14);

    private TipoVeiculo tipoVeiculo;

    PainelDesenhoVeiculo() {
        this.tipoVeiculo = TipoVeiculo.MOTO;
        setOpaque(false);
        setPreferredSize(new Dimension(220, 150));
        setMinimumSize(new Dimension(220, 150));
    }

    void setTipoVeiculo(TipoVeiculo tipoVeiculo) {
        this.tipoVeiculo = tipoVeiculo == null ? TipoVeiculo.MOTO : tipoVeiculo;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int largura = getWidth();
        int altura = getHeight();
        g2.setColor(FUNDO);
        g2.fill(new RoundRectangle2D.Double(0, 0, largura - 1, altura - 1, 16, 16));
        g2.setColor(BORDA);
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(new RoundRectangle2D.Double(0.5, 0.5, largura - 2, altura - 2, 16, 16));

        g2.setFont(FONTE_TITULO);
        g2.setColor(ROXO);
        desenharTextoCentralizado(g2, tipoVeiculo.toString(), largura, 30);

        g2.setFont(FONTE_AJUDA);
        g2.setColor(TEXTO);
        desenharTextoCentralizado(
                g2,
                "Ocupa " + tipoVeiculo.getVagasOcupadas() + textoVaga(),
                largura,
                altura - 18);

        desenharVeiculo(g2, largura, altura);
        g2.dispose();
    }

    private String textoVaga() {
        return tipoVeiculo.getVagasOcupadas() == 1 ? " vaga" : " vagas";
    }

    private void desenharTextoCentralizado(Graphics2D g2, String texto, int largura, int y) {
        int larguraTexto = g2.getFontMetrics().stringWidth(texto);
        int x = (largura - larguraTexto) / 2;
        g2.drawString(texto, x, y);
    }

    private void desenharVeiculo(Graphics2D g2, int largura, int altura) {
        if (tipoVeiculo == TipoVeiculo.MOTO) {
            desenharMoto(g2, largura, altura);
        } else if (tipoVeiculo == TipoVeiculo.CAMINHAO) {
            desenharCaminhao(g2, largura, altura);
        } else if (tipoVeiculo == TipoVeiculo.SUV) {
            desenharSuv(g2, largura, altura);
        } else {
            desenharCarro(g2, largura, altura);
        }
    }

    private void desenharMoto(Graphics2D g2, int largura, int altura) {
        int baseY = altura / 2 + 24;
        int x = largura / 2 - 62;
        int rodaTraseiraX = x + 26;
        int rodaDianteiraX = x + 100;

        desenharRoda(g2, rodaTraseiraX, baseY, 17);
        desenharRoda(g2, rodaDianteiraX, baseY, 17);

        g2.setColor(ROSA);
        g2.fillRoundRect(x + 34, baseY - 30, 64, 24, 18, 18);
        g2.fillRoundRect(x + 62, baseY - 42, 34, 18, 16, 16);

        g2.setColor(ROXO);
        g2.fillRoundRect(x + 34, baseY - 47, 48, 9, 8, 8);
        g2.fillOval(x + 58, baseY - 22, 24, 22);

        g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x + 96, baseY - 24, x + 108, baseY - 48);
        g2.drawLine(x + 108, baseY - 48, x + 123, baseY - 46);
        g2.drawLine(x + 40, baseY - 12, rodaTraseiraX + 2, baseY - 2);

        g2.setColor(ROSA);
        g2.fillOval(x + 116, baseY - 51, 10, 9);
    }

    private void desenharCarro(Graphics2D g2, int largura, int altura) {
        int x = largura / 2 - 76;
        int y = altura / 2 - 16;

        g2.setColor(ROSA);
        g2.fillRoundRect(x + 18, y + 22, 118, 34, 18, 18);
        g2.fillRoundRect(x + 42, y + 2, 68, 34, 18, 18);

        g2.setColor(FUNDO);
        g2.fillRoundRect(x + 52, y + 10, 22, 18, 8, 8);
        g2.fillRoundRect(x + 80, y + 10, 22, 18, 8, 8);

        desenharRoda(g2, x + 42, y + 58, 14);
        desenharRoda(g2, x + 116, y + 58, 14);
    }

    private void desenharSuv(Graphics2D g2, int largura, int altura) {
        int x = largura / 2 - 86;
        int y = altura / 2 - 22;

        g2.setColor(ROXO);
        g2.fillRoundRect(x + 12, y + 28, 146, 42, 14, 14);
        g2.fillRoundRect(x + 42, y + 4, 72, 38, 14, 14);

        g2.setColor(FUNDO);
        g2.fillRoundRect(x + 120, y + 10, 38, 52, 10, 10);
        g2.setColor(ROXO);
        g2.fillRect(x + 118, y + 32, 36, 34);
        g2.fillRoundRect(x + 104, y + 22, 24, 18, 8, 8);

        g2.setColor(FUNDO);
        g2.fillRoundRect(x + 52, y + 14, 22, 17, 7, 7);
        g2.fillRoundRect(x + 80, y + 14, 22, 17, 7, 7);

        g2.setColor(ROSA);
        g2.fillRoundRect(x + 18, y + 62, 138, 8, 6, 6);

        desenharRoda(g2, x + 42, y + 72, 16);
        desenharRoda(g2, x + 126, y + 72, 16);
    }

    private void desenharCaminhao(Graphics2D g2, int largura, int altura) {
        int x = largura / 2 - 88;
        int y = altura / 2 - 16;

        g2.setColor(ROXO);
        g2.fillRoundRect(x + 8, y + 14, 96, 42, 10, 10);
        g2.setColor(ROSA);
        g2.fillRoundRect(x + 104, y + 26, 48, 30, 10, 10);
        g2.fillRoundRect(x + 118, y + 8, 28, 28, 8, 8);

        g2.setColor(FUNDO);
        g2.fillRoundRect(x + 124, y + 14, 16, 14, 5, 5);

        desenharRoda(g2, x + 36, y + 60, 15);
        desenharRoda(g2, x + 92, y + 60, 15);
        desenharRoda(g2, x + 134, y + 60, 15);
    }

    private void desenharRoda(Graphics2D g2, int x, int y, int raio) {
        g2.setColor(CINZA);
        g2.fillOval(x - raio, y - raio, raio * 2, raio * 2);
        g2.setColor(FUNDO);
        g2.fillOval(x - raio / 2, y - raio / 2, raio, raio);
    }
}

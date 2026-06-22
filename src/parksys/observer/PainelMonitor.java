package parksys.observer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import parksys.entities.Vaga;
import parksys.enums.StatusVaga;

public class PainelMonitor extends JFrame implements EstacionamentoObserver {
    private static final long serialVersionUID = 1L;
    private static final Color FUNDO_CLARO = new Color(250, 247, 251);
    private static final Color TEXTO_ESCURO = new Color(46, 46, 46);
    private static final Font FONTE_MONITOR = new Font("Consolas", Font.PLAIN, 13);

    private final Map<String, StatusVaga> statusVagas;
    private final JTextArea areaStatus;

    public PainelMonitor() {
        this.statusVagas = new TreeMap<>();
        this.areaStatus = new JTextArea();

        configurarJanela();
        montarComponentes();
    }

    @Override
    public void onVagaAlterada(String idVaga, StatusVaga novoStatus) {
        statusVagas.put(idVaga, novoStatus);

        SwingUtilities.invokeLater(() -> {
            atualizarMapaVisual();
            System.out.println("Vaga " + idVaga + " alterada para " + novoStatus.name());
        });
    }

    public StatusVaga getStatusVaga(String idVaga) {
        return statusVagas.get(idVaga);
    }

    public Map<String, StatusVaga> getStatusVagas() {
        return Collections.unmodifiableMap(statusVagas);
    }

    public void carregarStatusAtual(Map<String, Vaga> vagas) {
        statusVagas.clear();

        for (Map.Entry<String, Vaga> entrada : vagas.entrySet()) {
            statusVagas.put(entrada.getKey(), entrada.getValue().getStatus());
        }

        SwingUtilities.invokeLater(() -> atualizarMapaVisual());
    }

    private void configurarJanela() {
        setTitle("Monitor de Vagas");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setMinimumSize(new Dimension(360, 420));
        setSize(420, 520);
        setLocationByPlatform(true);
    }

    private void montarComponentes() {
        areaStatus.setEditable(false);
        areaStatus.setFont(FONTE_MONITOR);
        areaStatus.setForeground(TEXTO_ESCURO);
        areaStatus.setBackground(FUNDO_CLARO);
        areaStatus.setBorder(new EmptyBorder(14, 14, 14, 14));

        JScrollPane painelRolagem = new JScrollPane(areaStatus);
        add(painelRolagem, BorderLayout.CENTER);
        atualizarMapaVisual();
    }

    private void atualizarMapaVisual() {
        StringBuilder mapa = new StringBuilder();
        mapa.append("STATUS ATUAL DAS VAGAS").append(System.lineSeparator());
        mapa.append("=======================").append(System.lineSeparator()).append(System.lineSeparator());

        if (statusVagas.isEmpty()) {
            mapa.append("Nenhuma alteracao registrada ainda.").append(System.lineSeparator());
        } else {
            for (Map.Entry<String, StatusVaga> entrada : statusVagas.entrySet()) {
                mapa.append(entrada.getKey())
                        .append(" -> ")
                        .append(entrada.getValue().getDescricao())
                        .append(System.lineSeparator());
            }
        }

        areaStatus.setText(mapa.toString());
        areaStatus.setCaretPosition(0);
    }
}

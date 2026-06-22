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
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import parksys.entities.Vaga;
import parksys.enums.StatusVaga;

public class PainelMonitor extends JFrame implements EstacionamentoObserver {
    private static final long serialVersionUID = 1L;
    private static final Color FUNDO_CLARO = new Color(250, 247, 251);
    private static final Color TEXTO_ESCURO = new Color(46, 46, 46);
    private static final Color ROXO_FECHADO = new Color(94, 58, 135);
    private static final Font FONTE_TABELA = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONTE_CABECALHO = new Font("Segoe UI", Font.BOLD, 14);

    private final Map<String, StatusVaga> statusVagas;
    private final DefaultTableModel modeloTabela;
    private final JTable tabelaVagas;

    public PainelMonitor() {
        this.statusVagas = new TreeMap<>();
        this.modeloTabela = criarModeloTabela();
        this.tabelaVagas = new JTable(modeloTabela);

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
        tabelaVagas.setFont(FONTE_TABELA);
        tabelaVagas.setForeground(TEXTO_ESCURO);
        tabelaVagas.setBackground(FUNDO_CLARO);
        tabelaVagas.setRowHeight(28);
        tabelaVagas.setFillsViewportHeight(true);
        tabelaVagas.getTableHeader().setFont(FONTE_CABECALHO);
        tabelaVagas.getTableHeader().setForeground(ROXO_FECHADO);
        tabelaVagas.getTableHeader().setReorderingAllowed(false);

        JScrollPane painelRolagem = new JScrollPane(tabelaVagas);
        add(painelRolagem, BorderLayout.CENTER);
        atualizarMapaVisual();
    }

    private void atualizarMapaVisual() {
        modeloTabela.setRowCount(0);

        if (statusVagas.isEmpty()) {
            modeloTabela.addRow(new Object[] {"-", "Nenhuma alteracao registrada ainda"});
            return;
        }

        for (Map.Entry<String, StatusVaga> entrada : statusVagas.entrySet()) {
            modeloTabela.addRow(new Object[] {entrada.getKey(), entrada.getValue().getDescricao()});
        }
    }

    private DefaultTableModel criarModeloTabela() {
        return new DefaultTableModel(new Object[] {"Vaga", "Status"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
}

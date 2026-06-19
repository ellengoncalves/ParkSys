package parksys.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import parksys.entities.Registro;
import parksys.entities.Vaga;
import parksys.entities.Veiculo;
import parksys.enums.StatusVaga;
import parksys.services.GerenciadorEstacionamento;

public class TelaRelatorio extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FORMATADOR_DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat FORMATADOR_MOEDA =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    private final GerenciadorEstacionamento gerenciador;
    private final JTextArea areaRelatorio;

    public TelaRelatorio() {
        this.gerenciador = GerenciadorEstacionamento.getInstance();
        this.areaRelatorio = new JTextArea();

        configurarJanela();
        montarComponentes();
        atualizarRelatorio();
    }

    private void configurarJanela() {
        setTitle("Relatorio do Estacionamento");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(620, 460);
        setLocationRelativeTo(null);
    }

    private void montarComponentes() {
        areaRelatorio.setEditable(false);
        areaRelatorio.setLineWrap(false);

        JButton botaoAtualizar = new JButton("Atualizar");
        botaoAtualizar.addActionListener(event -> atualizarRelatorio());

        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.addActionListener(event -> dispose());

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.add(botaoAtualizar);
        painelBotoes.add(botaoFechar);

        add(new JScrollPane(areaRelatorio), BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void atualizarRelatorio() {
        int vagasLivres = 0;
        int vagasOcupadas = 0;
        int vagasReservadas = 0;

        for (Map.Entry<String, Vaga> entradaVaga : gerenciador.getVagas().entrySet()) {
            StatusVaga status = entradaVaga.getValue().getStatus();

            if (status == StatusVaga.LIVRE) {
                vagasLivres++;
            } else if (status == StatusVaga.OCUPADA) {
                vagasOcupadas++;
            } else if (status == StatusVaga.RESERVADA) {
                vagasReservadas++;
            }
        }

        double receitaTotal = 0.0;
        LocalDate hoje = LocalDate.now();
        StringBuilder registrosDoDia = new StringBuilder();

        for (Registro registro : gerenciador.getRegistros()) {
            receitaTotal += registro.getValorPago();

            if (registro.getDataEntrada() != null
                    && registro.getDataEntrada().toLocalDate().equals(hoje)) {
                registrosDoDia.append(formatarRegistro(registro)).append(System.lineSeparator());
            }
        }

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("RELATORIO DO ESTACIONAMENTO").append(System.lineSeparator());
        relatorio.append(System.lineSeparator());
        relatorio.append("Vagas livres: ").append(vagasLivres).append(System.lineSeparator());
        relatorio.append("Vagas ocupadas: ").append(vagasOcupadas).append(System.lineSeparator());
        relatorio.append("Vagas reservadas: ").append(vagasReservadas).append(System.lineSeparator());
        relatorio.append("Receita total: ").append(FORMATADOR_MOEDA.format(receitaTotal)).append(System.lineSeparator());
        relatorio.append(System.lineSeparator());
        relatorio.append("REGISTROS DO DIA").append(System.lineSeparator());

        if (registrosDoDia.length() == 0) {
            relatorio.append("Nenhum registro encontrado para hoje.").append(System.lineSeparator());
        } else {
            relatorio.append(registrosDoDia);
        }

        areaRelatorio.setText(relatorio.toString());
        areaRelatorio.setCaretPosition(0);
    }

    private String formatarRegistro(Registro registro) {
        Veiculo veiculo = registro.getVeiculo();
        String placa = veiculo != null ? veiculo.getPlaca() : "-";
        String tipo = veiculo != null ? veiculo.getTipo().toString() : "-";
        String entrada = registro.getDataEntrada() != null
                ? registro.getDataEntrada().format(FORMATADOR_DATA_HORA)
                : "-";
        String saida = registro.getDataSaida() != null
                ? registro.getDataSaida().format(FORMATADOR_DATA_HORA)
                : "-";

        return "Vaga: " + registro.getIdVaga()
                + " | Placa: " + placa
                + " | Tipo: " + tipo
                + " | Entrada: " + entrada
                + " | Saida: " + saida
                + " | Valor: " + FORMATADOR_MOEDA.format(registro.getValorPago());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaRelatorio().setVisible(true));
    }
}

package parksys.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import parksys.observer.PainelMonitor;
import parksys.services.DadosParkSys;
import parksys.services.GerenciadorArquivo;
import parksys.services.GerenciadorEstacionamento;

public class TelaInicial extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final String CAMINHO_DADOS = "dados/parksys.ser";

    private final GerenciadorEstacionamento gerenciador;
    private final PainelMonitor painelMonitor;

    public TelaInicial() {
        this.gerenciador = GerenciadorEstacionamento.getInstance();
        this.painelMonitor = new PainelMonitor();

        carregarDados();
        gerenciador.addObserver(painelMonitor);

        configurarJanela();
        montarComponentes();
    }

    private void configurarJanela() {
        setTitle("ParkSys");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(360, 220);
        setLocationRelativeTo(null);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                fecharAplicacao();
            }
        });
    }

    private void montarComponentes() {
        JLabel titulo = new JLabel("ParkSys", SwingConstants.CENTER);

        JButton botaoEntrada = new JButton("Registrar entrada");
        botaoEntrada.addActionListener(event -> abrirTelaRegistroEntrada());

        JButton botaoSaida = new JButton("Registrar saida");
        botaoSaida.addActionListener(event -> abrirTelaSaida());

        JButton botaoRelatorio = new JButton("Relatorio");
        botaoRelatorio.addActionListener(event -> abrirTelaRelatorio());

        JPanel painelBotoes = new JPanel(new GridLayout(3, 1, 8, 8));
        painelBotoes.add(botaoEntrada);
        painelBotoes.add(botaoSaida);
        painelBotoes.add(botaoRelatorio);

        JPanel painelCentral = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 16));
        painelCentral.add(painelBotoes);

        add(titulo, BorderLayout.NORTH);
        add(painelCentral, BorderLayout.CENTER);
    }

    private void abrirTelaRegistroEntrada() {
        new TelaRegistroEntrada().setVisible(true);
    }

    private void abrirTelaSaida() {
        new TelaSaida().setVisible(true);
    }

    private void abrirTelaRelatorio() {
        new TelaRelatorio().setVisible(true);
    }

    private void fecharAplicacao() {
        salvarDados();
        gerenciador.removeObserver(painelMonitor);
        dispose();
        System.exit(0);
    }

    private void carregarDados() {
        DadosParkSys dados = GerenciadorArquivo.desserializar(CAMINHO_DADOS);

        if (dados.getVagas() != null && !dados.getVagas().isEmpty()) {
            gerenciador.carregarDados(dados);
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
        SwingUtilities.invokeLater(() -> new TelaInicial().setVisible(true));
    }
}

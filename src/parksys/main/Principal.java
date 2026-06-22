package parksys.main;

import javax.swing.SwingUtilities;

import parksys.entities.Registro;
import parksys.enums.TipoVeiculo;
import parksys.services.DadosParkSys;
import parksys.services.EntradaRunnable;
import parksys.services.GerenciadorArquivo;
import parksys.services.GerenciadorEstacionamento;
import parksys.services.MonitorRunnable;
import parksys.ui.TelaInicial;

public class Principal {
    private static final String CAMINHO_DEMO_THREADS = "dados/parksys-demo-threads.ser";

    public static void main(String[] args) {
        executarDemonstracaoThreads();
        SwingUtilities.invokeLater(() -> new TelaInicial().setVisible(true));
    }

    private static void executarDemonstracaoThreads() {
        GerenciadorEstacionamento gerenciador = GerenciadorEstacionamento.getInstance();
        Thread monitorThread = new Thread(new MonitorRunnable(gerenciador), "Monitor-Vagas");
        Thread[] threadsEntrada = {
                new Thread(new EntradaRunnable("ABC1234", TipoVeiculo.CARRO, "A01", gerenciador), "Entrada-Carro"),
                new Thread(new EntradaRunnable("DEF5678", TipoVeiculo.MOTO, "A02", gerenciador), "Entrada-Moto"),
                new Thread(new EntradaRunnable("GHI1J23", TipoVeiculo.SUV, "A03", gerenciador), "Entrada-SUV"),
                new Thread(new EntradaRunnable("JKL4M56", TipoVeiculo.CAMINHAO, "A05", gerenciador), "Entrada-Caminhao")
        };

        monitorThread.setDaemon(true);
        monitorThread.start();

        for (Thread threadEntrada : threadsEntrada) {
            threadEntrada.start();
        }

        for (Thread threadEntrada : threadsEntrada) {
            try {
                threadEntrada.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                interromperThreads(threadsEntrada);
                monitorThread.interrupt();
                return;
            }
        }

        monitorThread.interrupt();
        exibirRelatorioFinal(gerenciador);
        demonstrarThreadOrigemTransient(gerenciador);
    }

    private static void interromperThreads(Thread[] threadsEntrada) {
        for (Thread threadEntrada : threadsEntrada) {
            threadEntrada.interrupt();
        }
    }

    private static void exibirRelatorioFinal(GerenciadorEstacionamento gerenciador) {
        System.out.println("=== Relatorio final da demonstracao de threads ===");

        for (Registro registro : gerenciador.getRegistrosOrdenados()) {
            System.out.println(registro);
        }
    }

    private static void demonstrarThreadOrigemTransient(GerenciadorEstacionamento gerenciador) {
        GerenciadorArquivo.serializar(
                gerenciador.getVagas(),
                gerenciador.getRegistros(),
                gerenciador.getMensalistas(),
                CAMINHO_DEMO_THREADS);

        DadosParkSys dadosDesserializados = GerenciadorArquivo.desserializar(CAMINHO_DEMO_THREADS);

        System.out.println("=== Demonstracao do campo transient threadOrigem ===");

        for (Registro registro : dadosDesserializados.getRegistros()) {
            // threadOrigem guarda apenas qual Thread criou o registro em tempo de execucao.
            // Como o campo e transient, ele nao deve ser gravado no arquivo serializado;
            // por isso, apos a desserializacao, o valor esperado para o campo e null.
            System.out.println("Placa: " + registro.getVeiculo().getPlaca()
                    + " | threadOrigem apos desserializar: " + registro.getThreadOrigem());
        }
    }
}

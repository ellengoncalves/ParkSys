package parksys.main;

import javax.swing.SwingUtilities;

import parksys.entities.Registro;
import parksys.enums.TipoVeiculo;
import parksys.services.EntradaRunnable;
import parksys.services.GerenciadorEstacionamento;
import parksys.ui.TelaInicial;

public class Principal {
    public static void main(String[] args) {
        executarDemonstracaoThreads();
        SwingUtilities.invokeLater(() -> new TelaInicial().setVisible(true));
    }

    private static void executarDemonstracaoThreads() {
        GerenciadorEstacionamento gerenciador = GerenciadorEstacionamento.getInstance();
        Thread[] threadsEntrada = {
                new Thread(new EntradaRunnable("ABC1234", TipoVeiculo.CARRO, "A01", gerenciador), "Entrada-Carro"),
                new Thread(new EntradaRunnable("DEF5678", TipoVeiculo.MOTO, "A02", gerenciador), "Entrada-Moto"),
                new Thread(new EntradaRunnable("GHI1J23", TipoVeiculo.SUV, "A03", gerenciador), "Entrada-SUV"),
                new Thread(new EntradaRunnable("JKL4M56", TipoVeiculo.CAMINHAO, "A05", gerenciador), "Entrada-Caminhao")
        };

        for (Thread threadEntrada : threadsEntrada) {
            threadEntrada.start();
        }

        for (Thread threadEntrada : threadsEntrada) {
            try {
                threadEntrada.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                interromperThreads(threadsEntrada);
                return;
            }
        }

        exibirRelatorioFinal(gerenciador);
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
}

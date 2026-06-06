package parksys.main;

import parksys.entities.Registro;
import parksys.enums.TipoVeiculo;
import parksys.services.EntradaRunnable;
import parksys.services.GerenciadorEstacionamento;
import parksys.services.MonitorRunnable;

public class Principal {
    public static void main(String[] args) {
        GerenciadorEstacionamento gerenciador = new GerenciadorEstacionamento();

        Thread monitor = new Thread(new MonitorRunnable(gerenciador), "Monitor-Vagas");
        monitor.setDaemon(true);
        monitor.start();

        Thread[] threadsEntrada = {
            new Thread(new EntradaRunnable("ABC1D23", TipoVeiculo.MOTO, "A01", gerenciador), "Entrada-1"),
            new Thread(new EntradaRunnable("DEF4G56", TipoVeiculo.CARRO, "A02", gerenciador), "Entrada-2"),
            new Thread(new EntradaRunnable("GHI7J89", TipoVeiculo.SUV, "A03", gerenciador), "Entrada-3"),
            new Thread(new EntradaRunnable("JKL0M12", TipoVeiculo.CAMINHAO, "A05", gerenciador), "Entrada-4")
        };

        for (Thread threadEntrada : threadsEntrada) {
            threadEntrada.start();
        }

        try {
            for (Thread threadEntrada : threadsEntrada) {
                threadEntrada.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            interromperThreads(threadsEntrada, monitor);
            return;
        }

        monitor.interrupt();
        exibirRelatorioFinal(gerenciador);
    }

    private static void interromperThreads(Thread[] threadsEntrada, Thread monitor) {
        for (Thread threadEntrada : threadsEntrada) {
            threadEntrada.interrupt();
        }

        monitor.interrupt();
    }

    private static void exibirRelatorioFinal(GerenciadorEstacionamento gerenciador) {
        System.out.println();
        System.out.println("Relatorio final de entradas");
        System.out.println("==========================");

        for (Registro registro : gerenciador.getRegistrosOrdenados()) {
            System.out.println(
                    "Placa: " + registro.getVeiculo().getPlaca()
                            + " | Tipo: " + registro.getVeiculo().getTipo()
                            + " | Vaga inicial: " + registro.getIdVaga()
                            + " | Thread: " + registro.getThreadOrigem());
        }
    }
}

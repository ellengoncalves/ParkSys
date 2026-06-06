package parksys.services;

import java.util.Map;

import parksys.entities.Vaga;
import parksys.enums.StatusVaga;

public class MonitorRunnable implements Runnable {
    private final GerenciadorEstacionamento gerenciadorEstacionamento;

    public MonitorRunnable(GerenciadorEstacionamento gerenciadorEstacionamento) {
        this.gerenciadorEstacionamento = gerenciadorEstacionamento;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            imprimirResumoVagas();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void imprimirResumoVagas() {
        Map<String, Vaga> vagas = gerenciadorEstacionamento.getVagas();
        int livres = 0;
        int ocupadas = 0;
        int reservadas = 0;

        for (Vaga vaga : vagas.values()) {
            if (vaga.getStatus() == StatusVaga.LIVRE) {
                livres++;
            } else if (vaga.getStatus() == StatusVaga.OCUPADA) {
                ocupadas++;
            } else if (vaga.getStatus() == StatusVaga.RESERVADA) {
                reservadas++;
            }
        }

        System.out.println(
                "[Monitor] Livres: " + livres
                        + " | Ocupadas: " + ocupadas
                        + " | Reservadas: " + reservadas);
    }
}

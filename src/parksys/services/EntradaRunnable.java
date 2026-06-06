package parksys.services;

import parksys.enums.TipoVeiculo;

public class EntradaRunnable implements Runnable {
    private final String placa;
    private final TipoVeiculo tipoVeiculo;
    private final String idVagaDesejada;
    private final GerenciadorEstacionamento gerenciadorEstacionamento;

    public EntradaRunnable(
            String placa,
            TipoVeiculo tipoVeiculo,
            String idVagaDesejada,
            GerenciadorEstacionamento gerenciadorEstacionamento) {
        this.placa = placa;
        this.tipoVeiculo = tipoVeiculo;
        this.idVagaDesejada = idVagaDesejada;
        this.gerenciadorEstacionamento = gerenciadorEstacionamento;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        gerenciadorEstacionamento.registrarEntrada(placa, tipoVeiculo, idVagaDesejada);
    }
}

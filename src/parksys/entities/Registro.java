package parksys.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Registro implements Serializable, Comparable<Registro> {
    private static final long serialVersionUID = 1L;

    private Veiculo veiculo;
    private String idVaga;
    private LocalDateTime dataEntrada;
    private LocalDateTime dataSaida;
    private double valorPago;
    private transient String threadOrigem;

    public Registro(Veiculo veiculo, String idVaga, LocalDateTime dataEntrada) {
        this.veiculo = veiculo;
        this.idVaga = idVaga;
        this.dataEntrada = dataEntrada;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public String getIdVaga() {
        return idVaga;
    }

    public void setIdVaga(String idVaga) {
        this.idVaga = idVaga;
    }

    public LocalDateTime getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDateTime dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public LocalDateTime getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(LocalDateTime dataSaida) {
        this.dataSaida = dataSaida;
    }

    public double getValorPago() {
        return valorPago;
    }

    public void setValorPago(double valorPago) {
        this.valorPago = valorPago;
    }

    public String getThreadOrigem() {
        return threadOrigem;
    }

    public void setThreadOrigem(String threadOrigem) {
        this.threadOrigem = threadOrigem;
    }

    @Override
    public int compareTo(Registro outro) {
        int comparacaoData = this.dataEntrada.compareTo(outro.dataEntrada);

        if (comparacaoData != 0) {
            return comparacaoData;
        }

        int comparacaoPlaca = this.veiculo.getPlaca().compareToIgnoreCase(outro.veiculo.getPlaca());

        if (comparacaoPlaca != 0) {
            return comparacaoPlaca;
        }

        return this.idVaga.compareTo(outro.idVaga);
    }

    @Override
    public String toString() {
        return "Registro{"
                + "veiculo=" + veiculo
                + ", idVaga='" + idVaga + '\''
                + ", dataEntrada=" + dataEntrada
                + ", dataSaida=" + dataSaida
                + ", valorPago=" + valorPago
                + ", threadOrigem='" + threadOrigem + '\''
                + '}';
    }
}

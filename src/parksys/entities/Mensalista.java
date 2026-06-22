package parksys.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import parksys.enums.TipoVeiculo;

public class Mensalista implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String documento;
    private String telefone;
    private String placa;
    private TipoVeiculo tipoVeiculo;
    private String idVagaReservada;
    private double valorMensalidade;
    private LocalDate dataCadastro;
    private LocalDateTime dataHoraCadastro;
    private boolean ativo;

    public Mensalista(String nome, String documento, String telefone, String placa, TipoVeiculo tipoVeiculo, 
        String idVagaReservada, double valorMensalidade) {
        this.nome = nome;
        this.documento = documento;
        this.telefone = telefone;
        this.placa = placa;
        this.tipoVeiculo = tipoVeiculo;
        this.idVagaReservada = idVagaReservada;
        this.valorMensalidade = valorMensalidade;
        this.dataCadastro = LocalDate.now();
        this.dataHoraCadastro = LocalDateTime.now();
        this.ativo = true;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public TipoVeiculo getTipoVeiculo() {
        return tipoVeiculo;
    }

    public void setTipoVeiculo(TipoVeiculo tipoVeiculo) {
        this.tipoVeiculo = tipoVeiculo;
    }

    public String getIdVagaReservada() {
        return idVagaReservada;
    }

    public void setIdVagaReservada(String idVagaReservada) {
        this.idVagaReservada = idVagaReservada;
    }

    public double getValorMensalidade() {
        return valorMensalidade;
    }

    public void setValorMensalidade(double valorMensalidade) {
        this.valorMensalidade = valorMensalidade;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
        this.dataHoraCadastro = dataCadastro != null ? dataCadastro.atStartOfDay() : null;
    }

    public LocalDateTime getDataHoraCadastro() {
        if (dataHoraCadastro != null) {
            return dataHoraCadastro;
        }

        return dataCadastro != null ? dataCadastro.atStartOfDay() : null;
    }

    public void setDataHoraCadastro(LocalDateTime dataHoraCadastro) {
        this.dataHoraCadastro = dataHoraCadastro;
        this.dataCadastro = dataHoraCadastro != null ? dataHoraCadastro.toLocalDate() : null;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "Mensalista{"
                + "nome='" + nome + '\''
                + ", documento='" + documento + '\''
                + ", telefone='" + telefone + '\''
                + ", placa='" + placa + '\''
                + ", tipoVeiculo=" + tipoVeiculo
                + ", idVagaReservada='" + idVagaReservada + '\''
                + ", valorMensalidade=" + valorMensalidade
                + ", dataCadastro=" + dataCadastro
                + ", dataHoraCadastro=" + dataHoraCadastro
                + ", ativo=" + ativo
                + '}';
    }
}

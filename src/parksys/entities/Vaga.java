package parksys.entities;

import java.io.Serializable;

import parksys.enums.StatusVaga;

public class Vaga implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private StatusVaga status;
    private Veiculo veiculoAtual;

    public Vaga(String id) {
        this(id, StatusVaga.LIVRE, null);
    }

    public Vaga(String id, StatusVaga status, Veiculo veiculoAtual) {
        this.id = id;
        this.status = status;
        this.veiculoAtual = veiculoAtual;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StatusVaga getStatus() {
        return status;
    }

    public void setStatus(StatusVaga status) {
        this.status = status;
    }

    public Veiculo getVeiculoAtual() {
        return veiculoAtual;
    }

    public void setVeiculoAtual(Veiculo veiculoAtual) {
        this.veiculoAtual = veiculoAtual;
    }
}
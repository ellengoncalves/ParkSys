package parksys.entities;

import java.io.Serializable;

import parksys.enums.TipoVeiculo;

public class Veiculo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String placa;
    private TipoVeiculo tipo;

    public Veiculo(String placa, TipoVeiculo tipo) {
        this.placa = placa;
        this.tipo = tipo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public TipoVeiculo getTipo() {
        return tipo;
    }

    public void setTipo(TipoVeiculo tipo) {
        this.tipo = tipo;
    }
}
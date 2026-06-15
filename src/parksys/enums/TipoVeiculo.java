package parksys.enums;

public enum TipoVeiculo {
    MOTO("Motocicleta", 5.00, 1),
    CARRO("Automovel", 10.00, 1),
    SUV("Caminhonete / SUV", 18.00, 2),
    CAMINHAO("Caminhao", 30.00, 3);

    private final String descricao;
    private final double tarifaHora;
    private final int vagasOcupadas;

    TipoVeiculo(String descricao, double tarifaHora, int vagasOcupadas) {
        this.descricao = descricao;
        this.tarifaHora = tarifaHora;
        this.vagasOcupadas = vagasOcupadas;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getTarifaHora() {
        return tarifaHora;
    }

    public int getVagasOcupadas() {
        return vagasOcupadas;
    }

    @Override
    public String toString() {
        return descricao;
    }
}

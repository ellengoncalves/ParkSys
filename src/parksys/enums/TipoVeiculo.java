package parksys.enums;

public enum TipoVeiculo {
    MOTO("Motocicleta", 5.00, 1),
    CARRO("Automovel", 10.00, 1),
    SUV("Caminhonete / SUV", 18.00, 2),
    CAMINHAO("Caminhao", 30.00, 3);

    private final String nomeLegivel;
    private final double tarifaHora;
    private final int vagasOcupadas;

    TipoVeiculo(String nomeLegivel, double tarifaHora, int vagasOcupadas) {
        this.nomeLegivel = nomeLegivel;
        this.tarifaHora = tarifaHora;
        this.vagasOcupadas = vagasOcupadas;
    }

    public String getNomeLegivel() {
        return nomeLegivel;
    }

    public double getTarifaHora() {
        return tarifaHora;
    }

    public int getVagasOcupadas() {
        return vagasOcupadas;
    }

    @Override
    public String toString() {
        return nomeLegivel;
    }
}

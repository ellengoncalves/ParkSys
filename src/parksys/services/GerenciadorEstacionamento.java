package parksys.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import parksys.entities.Registro;
import parksys.entities.Vaga;
import parksys.entities.Veiculo;
import parksys.enums.TipoVeiculo;

public class GerenciadorEstacionamento {
    private static final int TOTAL_FILEIRAS = 2;
    private static final int VAGAS_POR_FILEIRA = 15;
    private static final char PRIMEIRA_FILEIRA = 'A';

    private final HashMap<String, Vaga> vagas;
    private final ArrayList<Registro> registros;

    public GerenciadorEstacionamento() {
        this.vagas = new HashMap<>();
        this.registros = new ArrayList<>();
        inicializarVagas();
    }

    private void inicializarVagas() {
        for (int fileira = 0; fileira < TOTAL_FILEIRAS; fileira++) {
            char letraFileira = (char) (PRIMEIRA_FILEIRA + fileira);

            for (int numero = 1; numero <= VAGAS_POR_FILEIRA; numero++) {
                String idVaga = String.format("%c%02d", letraFileira, numero);
                vagas.put(idVaga, new Vaga(idVaga));
            }
        }
    }

    public Vaga getVagaPorId(String idVaga) {
        return vagas.get(idVaga);
    }

    public Map<String, Vaga> getVagas() {
        return Collections.unmodifiableMap(vagas);
    }

    public Registro registrarEntrada(String placa, TipoVeiculo tipoVeiculo, String idVaga) {
        Veiculo veiculo = new Veiculo(placa, tipoVeiculo);
        Registro registro = new Registro(veiculo, idVaga, LocalDateTime.now());
        registros.add(registro);
        return registro;
    }

    public Registro registrarSaida(String placa, LocalDateTime dataSaida, double valorPago) {
        for (Registro registro : registros) {
            boolean mesmaPlaca = registro.getVeiculo().getPlaca().equalsIgnoreCase(placa);
            boolean registroAberto = registro.getDataSaida() == null;

            if (mesmaPlaca && registroAberto) {
                registro.setDataSaida(dataSaida);
                registro.setValorPago(valorPago);
                return registro;
            }
        }

        return null;
    }

    public List<Registro> getRegistros() {
        return Collections.unmodifiableList(registros);
    }
}

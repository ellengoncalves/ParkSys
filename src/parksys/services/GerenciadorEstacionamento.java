package parksys.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import parksys.entities.Vaga;

public class GerenciadorEstacionamento {
    private static final int TOTAL_FILEIRAS = 2;
    private static final int VAGAS_POR_FILEIRA = 15;
    private static final char PRIMEIRA_FILEIRA = 'A';

    private final HashMap<String, Vaga> vagas;

    public GerenciadorEstacionamento() {
        this.vagas = new HashMap<>();
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
}

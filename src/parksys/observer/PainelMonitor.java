package parksys.observer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import parksys.enums.StatusVaga;

public class PainelMonitor implements EstacionamentoObserver {
    private final Map<String, StatusVaga> statusVagas;

    public PainelMonitor() {
        this.statusVagas = new HashMap<>();
    }

    @Override
    public void onVagaAlterada(String idVaga, StatusVaga novoStatus) {
        statusVagas.put(idVaga, novoStatus);
        System.out.println("Vaga " + idVaga + " alterada para " + novoStatus.name());
    }

    public StatusVaga getStatusVaga(String idVaga) {
        return statusVagas.get(idVaga);
    }

    public Map<String, StatusVaga> getStatusVagas() {
        return Collections.unmodifiableMap(statusVagas);
    }
}

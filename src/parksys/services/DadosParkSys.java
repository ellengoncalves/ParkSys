package parksys.services;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import parksys.entities.Mensalista;
import parksys.entities.Registro;
import parksys.entities.Vaga;

public class DadosParkSys implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Vaga> vagas;
    private List<Registro> registros;
    private List<Mensalista> mensalistas;

    public DadosParkSys(Map<String, Vaga> vagas, List<Registro> registros, List<Mensalista> mensalistas) {
        this.vagas = vagas;
        this.registros = registros;
        this.mensalistas = mensalistas;
    }

    public Map<String, Vaga> getVagas() {
        return vagas;
    }

    public List<Registro> getRegistros() {
        return registros;
    }

    public List<Mensalista> getMensalistas() {
        return mensalistas;
    }
}

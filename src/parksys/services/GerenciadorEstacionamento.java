package parksys.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import parksys.entities.Mensalista;
import parksys.entities.Registro;
import parksys.entities.Vaga;
import parksys.entities.Veiculo;
import parksys.enums.StatusVaga;
import parksys.enums.TipoVeiculo;
import parksys.exceptions.PlacaInvalidaException;
import parksys.exceptions.VagaOcupadaException;
import parksys.exceptions.VeiculoNaoEncontradoException;

public class GerenciadorEstacionamento {
    private static final GerenciadorEstacionamento INSTANCE = new GerenciadorEstacionamento();

    private static final int TOTAL_FILEIRAS = 2;
    private static final int VAGAS_POR_FILEIRA = 15;
    private static final char PRIMEIRA_FILEIRA = 'A';

    private final HashMap<String, Vaga> vagas;
    private final ArrayList<Registro> registros;
    private final LinkedList<Mensalista> mensalistas;

    private GerenciadorEstacionamento() {
        this.vagas = new HashMap<>();
        this.registros = new ArrayList<>();
        this.mensalistas = new LinkedList<>();
        inicializarVagas();
    }

    public static GerenciadorEstacionamento getInstance() {
        return INSTANCE;
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

    public synchronized Vaga getVagaPorId(String idVaga) {
        return vagas.get(idVaga);
    }

    public synchronized Map<String, Vaga> getVagas() {
        return Collections.unmodifiableMap(new HashMap<>(vagas));
    }

    public synchronized Registro registrarEntrada(String placa, TipoVeiculo tipoVeiculo, String idVaga)
            throws PlacaInvalidaException, VagaOcupadaException {
        String placaNormalizada = normalizarPlaca(placa);
        Veiculo veiculo = new Veiculo(placaNormalizada, tipoVeiculo);
        List<Vaga> vagasParaOcupar = buscarVagasConsecutivasDisponiveis(idVaga, tipoVeiculo.getVagasOcupadas());

        // Sem synchronized, duas threads poderiam verificar a mesma vaga como livre
        // ao mesmo tempo e registrar entradas simultaneas nela, causando race condition.
        for (Vaga vaga : vagasParaOcupar) {
            vaga.setStatus(StatusVaga.OCUPADA);
            vaga.setVeiculoAtual(veiculo);
        }

        Registro registro = new Registro(veiculo, idVaga, LocalDateTime.now());
        registro.setThreadOrigem(Thread.currentThread().getName());
        registros.add(registro);
        return registro;
    }

    public synchronized Registro registrarSaida(String placa, LocalDateTime dataSaida)
            throws PlacaInvalidaException, VeiculoNaoEncontradoException {
        String placaNormalizada = normalizarPlaca(placa);

        for (Registro registro : registros) {
            boolean mesmaPlaca = registro.getVeiculo().getPlaca().equalsIgnoreCase(placaNormalizada);
            boolean registroAberto = registro.getDataSaida() == null;

            if (mesmaPlaca && registroAberto) {
                double valorPago = calcularValorEstadia(registro, dataSaida);
                registro.setDataSaida(dataSaida);
                registro.setValorPago(valorPago);
                liberarVagasDoVeiculo(placaNormalizada);
                return registro;
            }
        }

        throw new VeiculoNaoEncontradoException("Veiculo nao encontrado ou sem registro aberto: " + placaNormalizada);
    }

    public synchronized Registro registrarSaida(String placa)
            throws PlacaInvalidaException, VeiculoNaoEncontradoException {
        return registrarSaida(placa, LocalDateTime.now());
    }

    private String normalizarPlaca(String placa) throws PlacaInvalidaException {
        if (placa == null) {
            throw new PlacaInvalidaException("Placa nao pode ser nula.");
        }

        String placaNormalizada = placa.trim().replace("-", "").replace(" ", "").toUpperCase();
        boolean placaAntiga = placaNormalizada.matches("[A-Z]{3}[0-9]{4}");
        boolean placaMercosul = placaNormalizada.matches("[A-Z]{3}[0-9][A-Z][0-9]{2}");

        if (!placaAntiga && !placaMercosul) {
            throw new PlacaInvalidaException("Placa invalida: " + placa);
        }

        return placaNormalizada;
    }

    private double calcularValorEstadia(Registro registro, LocalDateTime dataSaida) {
        long minutos = Duration.between(registro.getDataEntrada(), dataSaida).toMinutes();
        long horasCobradas = Math.max(1, (long) Math.ceil(minutos / 60.0));

        return horasCobradas * registro.getVeiculo().getTipo().getTarifaHora();
    }

    private synchronized List<Vaga> buscarVagasConsecutivasDisponiveis(String idVagaInicial, int quantidadeVagas)
            throws VagaOcupadaException {
        ArrayList<Vaga> vagasEncontradas = new ArrayList<>();
        String idNormalizado = normalizarIdVaga(idVagaInicial);
        char fileira = idNormalizado.charAt(0);
        int numeroInicial = Integer.parseInt(idNormalizado.substring(1));

        for (int deslocamento = 0; deslocamento < quantidadeVagas; deslocamento++) {
            int numeroAtual = numeroInicial + deslocamento;

            if (numeroAtual > VAGAS_POR_FILEIRA) {
                throw new VagaOcupadaException("Nao ha vagas consecutivas suficientes na fileira " + fileira);
            }

            String idVagaAtual = String.format("%c%02d", fileira, numeroAtual);
            Vaga vagaAtual = vagas.get(idVagaAtual);

            if (vagaAtual == null) {
                throw new VagaOcupadaException("Vaga nao encontrada: " + idVagaAtual);
            }

            if (!vagaAtual.getStatus().isDisponivel()) {
                throw new VagaOcupadaException("Vaga indisponivel: " + idVagaAtual);
            }

            vagasEncontradas.add(vagaAtual);
        }

        return vagasEncontradas;
    }

    private String normalizarIdVaga(String idVaga) throws VagaOcupadaException {
        if (idVaga == null) {
            throw new VagaOcupadaException("ID da vaga nao pode ser nulo.");
        }

        String idNormalizado = idVaga.trim().toUpperCase();

        if (!idNormalizado.matches("[AB][0-9]{2}")) {
            throw new VagaOcupadaException("ID da vaga invalido: " + idVaga);
        }

        return idNormalizado;
    }

    private synchronized void liberarVagasDoVeiculo(String placa) {
        for (Vaga vaga : vagas.values()) {
            Veiculo veiculoAtual = vaga.getVeiculoAtual();

            if (veiculoAtual != null && veiculoAtual.getPlaca().equalsIgnoreCase(placa)) {
                vaga.setStatus(StatusVaga.LIVRE);
                vaga.setVeiculoAtual(null);
            }
        }
    }

    public synchronized List<Registro> getRegistros() {
        return Collections.unmodifiableList(new ArrayList<>(registros));
    }

    public synchronized TreeSet<Registro> getRegistrosOrdenados() {
        return new TreeSet<>(registros);
    }

    public synchronized List<Registro> getRegistrosPorReceitaDecrescente() {
        ArrayList<Registro> registrosOrdenados = new ArrayList<>(registros);

        // O Comparable implementado em Registro representa a ordem natural da classe, 
        //      usada quando queremos os registros em ordem cronologica pela dataEntrada
        // Ja o Comparator eh usado quando precisamos de uma ordenacao especifica para
        //      um relatorio, sem alterar essa ordem natural; aqui a copia da lista e
        //      ordenada por valorPago decrescente para exibir as maiores receitas primeiro
        registrosOrdenados.sort(Comparator.comparingDouble(Registro::getValorPago).reversed());

        return registrosOrdenados;
    }

    public void cadastrarMensalista(Mensalista mensalista) {
        // LinkedList eh adequada aqui porque o cadastro de mensalistas pode ter
        // insercoes no fim e remocoes no inicio com frequencia, operacoes que
        // nao exigem deslocar os demais elementos da lista
        mensalistas.addLast(mensalista);
    }

    public Mensalista removerPrimeiroMensalista() {
        if (mensalistas.isEmpty()) {
            return null;
        }

        return mensalistas.removeFirst();
    }

    public List<Mensalista> getMensalistas() {
        return Collections.unmodifiableList(mensalistas);
    }
}

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
import parksys.observer.EstacionamentoObserver;

public class GerenciadorEstacionamento {
    private static final GerenciadorEstacionamento INSTANCE = new GerenciadorEstacionamento();

    private static final int TOTAL_FILEIRAS = 2;
    private static final int VAGAS_POR_FILEIRA = 15;
    private static final char PRIMEIRA_FILEIRA = 'A';
    private static final double VALOR_MENSALIDADE_FIXA = 250.00;

    private final HashMap<String, Vaga> vagas;
    private final ArrayList<Registro> registros;
    private final LinkedList<Mensalista> mensalistas;
    private final List<EstacionamentoObserver> observadores;
    private boolean dadosCarregados;

    private GerenciadorEstacionamento() {
        this.vagas = new HashMap<>();
        this.registros = new ArrayList<>();
        this.mensalistas = new LinkedList<>();
        this.observadores = new ArrayList<>();
        this.dadosCarregados = false;
        inicializarVagas();
    }

    public static synchronized GerenciadorEstacionamento getInstance() {
        return INSTANCE;
    }

    public double getValorMensalidadeFixa() {
        return VALOR_MENSALIDADE_FIXA;
    }

    public synchronized Mensalista consultarMensalistaAtivoPorPlaca(String placa)
            throws PlacaInvalidaException {
        return buscarMensalistaAtivoPorPlaca(normalizarPlaca(placa));
    }

    public synchronized boolean possuiRegistroAberto(String placa)
            throws PlacaInvalidaException {
        return buscarRegistroAbertoPorPlaca(normalizarPlaca(placa)) != null;
    }

    public synchronized void carregarDadosSalvos(String caminhoDados) {
        if (dadosCarregados) {
            return;
        }

        DadosParkSys dados = GerenciadorArquivo.desserializar(caminhoDados);

        if (dados.getVagas() != null && !dados.getVagas().isEmpty()) {
            carregarDados(dados);
        }

        dadosCarregados = true;
    }

    public synchronized void carregarDados(DadosParkSys dados) {
        if (dados == null) {
            return;
        }

        vagas.clear();
        registros.clear();
        mensalistas.clear();

        if (dados.getVagas() != null) {
            vagas.putAll(dados.getVagas());
        }

        if (dados.getRegistros() != null) {
            registros.addAll(dados.getRegistros());
        }

        if (dados.getMensalistas() != null) {
            mensalistas.addAll(dados.getMensalistas());
        }

        aplicarMensalidadeFixaMensalistas();
        restaurarReservasMensalistas();
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
        String idVagaNormalizado = normalizarIdVaga(idVaga);

        if (buscarRegistroAbertoPorPlaca(placaNormalizada) != null) {
            throw new VagaOcupadaException("Ja existe registro de entrada aberto para a placa: " + placaNormalizada);
        }

        Mensalista mensalista = buscarMensalistaAtivoPorPlaca(placaNormalizada);
        TipoVeiculo tipoRegistro = mensalista != null ? mensalista.getTipoVeiculo() : tipoVeiculo;
        Veiculo veiculo = new Veiculo(placaNormalizada, tipoRegistro);
        List<Vaga> vagasParaOcupar;

        if (mensalista != null) {
            if (!mensalista.getIdVagaReservada().equalsIgnoreCase(idVagaNormalizado)) {
                throw new VagaOcupadaException(
                        "Mensalista deve usar a vaga reservada: " + mensalista.getIdVagaReservada());
            }

            vagasParaOcupar = buscarVagasReservadasDoMensalista(mensalista);
        } else {
            vagasParaOcupar = buscarVagasConsecutivasDisponiveis(idVagaNormalizado, tipoRegistro.getVagasOcupadas());
        }

        // Sem synchronized, duas threads poderiam verificar a mesma vaga como livre
        // ao mesmo tempo e registrar entradas simultaneas nela, causando race condition.
        for (Vaga vaga : vagasParaOcupar) {
            vaga.setStatus(StatusVaga.OCUPADA);
            notificarObservadores(vaga.getId(), StatusVaga.OCUPADA);
            vaga.setVeiculoAtual(veiculo);
        }

        Registro registro = new Registro(veiculo, idVagaNormalizado, LocalDateTime.now());
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
                Mensalista mensalista = buscarMensalistaAtivoPorPlaca(placaNormalizada);
                double valorPago = mensalista != null ? 0.0 : calcularValorEstadia(registro, dataSaida);
                StatusVaga statusAposSaida = mensalista != null ? StatusVaga.RESERVADA : StatusVaga.LIVRE;
                registro.setDataSaida(dataSaida);
                registro.setValorPago(valorPago);
                liberarVagasDoVeiculo(placaNormalizada, statusAposSaida);
                return registro;
            }
        }

        throw new VeiculoNaoEncontradoException("Veiculo nao encontrado ou sem registro aberto: " + placaNormalizada);
    }

    public synchronized Registro registrarSaida(String placa)
            throws PlacaInvalidaException, VeiculoNaoEncontradoException {
        return registrarSaida(placa, LocalDateTime.now());
    }

    public synchronized void addObserver(EstacionamentoObserver observer) {
        observadores.add(observer);
    }

    public synchronized void removeObserver(EstacionamentoObserver observer) {
        observadores.remove(observer);
    }

    private void notificarObservadores(String idVaga, StatusVaga novoStatus) {
        for (EstacionamentoObserver observer : observadores) {
            observer.onVagaAlterada(idVaga, novoStatus);
        }
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

    private Registro buscarRegistroAbertoPorPlaca(String placaNormalizada) {
        for (Registro registro : registros) {
            if (registro.getVeiculo().getPlaca().equalsIgnoreCase(placaNormalizada)
                    && registro.getDataSaida() == null) {
                return registro;
            }
        }

        return null;
    }

    private Mensalista buscarMensalistaAtivoPorPlaca(String placaNormalizada) {
        for (Mensalista mensalista : mensalistas) {
            if (mensalista.isAtivo() && mensalista.getPlaca().equalsIgnoreCase(placaNormalizada)) {
                return mensalista;
            }
        }

        return null;
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

    private void restaurarReservasMensalistas() {
        for (Mensalista mensalista : mensalistas) {
            try {
                List<Vaga> vagasReservadas = buscarVagasConsecutivasPorId(
                        mensalista.getIdVagaReservada(),
                        mensalista.getTipoVeiculo().getVagasOcupadas());
                Registro registroAberto = buscarRegistroAbertoPorPlaca(mensalista.getPlaca());
                StatusVaga statusRestaurado = registroAberto != null ? StatusVaga.OCUPADA : StatusVaga.RESERVADA;
                Veiculo veiculoAtual = registroAberto != null ? registroAberto.getVeiculo() : null;

                for (Vaga vagaReservada : vagasReservadas) {
                    vagaReservada.setStatus(statusRestaurado);
                    vagaReservada.setVeiculoAtual(veiculoAtual);
                }
            } catch (VagaOcupadaException e) {
                System.out.println("Reserva de mensalista nao restaurada: " + e.getMessage());
            }
        }
    }

    private void aplicarMensalidadeFixaMensalistas() {
        for (Mensalista mensalista : mensalistas) {
            mensalista.setValorMensalidade(VALOR_MENSALIDADE_FIXA);
        }
    }

    private List<Vaga> buscarVagasConsecutivasPorId(String idVagaInicial, int quantidadeVagas)
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

            vagasEncontradas.add(vagaAtual);
        }

        return vagasEncontradas;
    }

    private List<Vaga> buscarVagasReservadasDoMensalista(Mensalista mensalista)
            throws VagaOcupadaException {
        List<Vaga> vagasReservadas = buscarVagasConsecutivasPorId(
                mensalista.getIdVagaReservada(),
                mensalista.getTipoVeiculo().getVagasOcupadas());

        for (Vaga vaga : vagasReservadas) {
            if (vaga.getStatus() != StatusVaga.RESERVADA) {
                throw new VagaOcupadaException("Vaga reservada indisponivel: " + vaga.getId());
            }
        }

        return vagasReservadas;
    }

    private synchronized void liberarVagasDoVeiculo(String placa, StatusVaga statusAposSaida) {
        for (Vaga vaga : vagas.values()) {
            Veiculo veiculoAtual = vaga.getVeiculoAtual();

            if (veiculoAtual != null && veiculoAtual.getPlaca().equalsIgnoreCase(placa)) {
                vaga.setStatus(statusAposSaida);
                notificarObservadores(vaga.getId(), statusAposSaida);
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

    public synchronized void cadastrarMensalista(Mensalista mensalista)
            throws PlacaInvalidaException, VagaOcupadaException {
        String placaNormalizada = normalizarPlaca(mensalista.getPlaca());
        String idVagaReservada = normalizarIdVaga(mensalista.getIdVagaReservada());
        List<Vaga> vagasParaReservar = buscarVagasConsecutivasDisponiveis(
                idVagaReservada,
                mensalista.getTipoVeiculo().getVagasOcupadas());

        mensalista.setPlaca(placaNormalizada);
        mensalista.setIdVagaReservada(idVagaReservada);
        mensalista.setValorMensalidade(VALOR_MENSALIDADE_FIXA);

        for (Vaga vagaReservada : vagasParaReservar) {
            vagaReservada.setStatus(StatusVaga.RESERVADA);
            vagaReservada.setVeiculoAtual(null);
            notificarObservadores(vagaReservada.getId(), StatusVaga.RESERVADA);
        }

        // LinkedList eh adequada aqui porque o cadastro de mensalistas pode ter
        // insercoes no fim e remocoes no inicio com frequencia, operacoes que
        // nao exigem deslocar os demais elementos da lista
        mensalistas.addLast(mensalista);
    }

    public synchronized Mensalista removerPrimeiroMensalista() {
        if (mensalistas.isEmpty()) {
            return null;
        }

        return mensalistas.removeFirst();
    }

    public synchronized List<Mensalista> getMensalistas() {
        return Collections.unmodifiableList(new LinkedList<>(mensalistas));
    }
}

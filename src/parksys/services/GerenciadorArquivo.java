package parksys.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import parksys.entities.Mensalista;
import parksys.entities.Registro;
import parksys.entities.Vaga;
import parksys.entities.Veiculo;

public class GerenciadorArquivo {
    private static final DateTimeFormatter FORMATADOR_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final NumberFormat FORMATADOR_MOEDA =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    public static void serializar(Map<String, Vaga> vagas, List<Registro> registros,
        List<Mensalista> mensalistas, String path) {
        String resultado = "Serializacao nao concluida.";

        criarDiretorioPaiSeNecessario(path);

        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(path))) {
            outputStream.writeObject(new DadosParkSys(vagas, registros, mensalistas));
            resultado = "Serializacao concluida com sucesso: " + path;
        } catch (IOException e) {
            resultado = "Erro ao serializar dados: " + e.getMessage();
        } finally {
            logarResultado(resultado);
        }
    }

    public static DadosParkSys desserializar(String path) {
        DadosParkSys dados = criarDadosVazios();
        String resultado = "Desserializacao nao concluida.";
        File arquivo = new File(path);

        try {
            if (!arquivo.exists()) {
                resultado = "Arquivo nao encontrado. Estruturas vazias retornadas: " + path;
                return dados;
            }

            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(arquivo))) {
                dados = (DadosParkSys) inputStream.readObject();
            }
            resultado = "Desserializacao concluida com sucesso: " + path;
        } catch (IOException e) {
            resultado = "Erro ao desserializar dados: " + e.getMessage();
        } catch (ClassNotFoundException e) {
            resultado = "Classe nao encontrada ao desserializar dados: " + e.getMessage();
        } finally {
            logarResultado(resultado);
        }

        return dados;
    }

    public static boolean exportarRelatorioTxt(List<Registro> registros, String path) {
        return exportarRelatorioTxt(registros, new LinkedList<>(), path);
    }

    public static boolean exportarRelatorioTxt(List<Registro> registros, List<Mensalista> mensalistas, String path) {
        String resultado = "Exportacao de relatorio nao concluida.";
        boolean exportado = false;

        criarDiretorioPaiSeNecessario(path);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            double receitaAvulsa = calcularReceitaAvulsa(registros);
            double receitaMensalistas = calcularReceitaMensalistas(mensalistas);
            double receitaTotal = receitaAvulsa + receitaMensalistas;

            writer.write("Relatorio do Estacionamento - ParkSys");
            writer.newLine();
            writer.write("Gerado em: " + LocalDateTime.now().format(FORMATADOR_DATA_HORA));
            writer.newLine();
            writer.write("========================================");
            writer.newLine();
            writer.newLine();

            writer.write("Resumo financeiro");
            writer.newLine();
            writer.write("Receita avulsa: " + formatarMoeda(receitaAvulsa));
            writer.newLine();
            writer.write("Receita mensalistas: " + formatarMoeda(receitaMensalistas));
            writer.newLine();
            writer.write("Receita total: " + formatarMoeda(receitaTotal));
            writer.newLine();
            writer.newLine();

            writer.write("Registros");
            writer.newLine();
            writer.write("----------------------------------------");
            writer.newLine();

            for (int i = 0; i < registros.size(); i++) {
                Registro registro = registros.get(i);
                Veiculo veiculo = registro.getVeiculo();
                Mensalista mensalista = veiculo != null
                        ? buscarMensalistaPorPlaca(mensalistas, veiculo.getPlaca())
                        : null;

                writer.write("Registro " + (i + 1));
                writer.newLine();
                writer.write("Categoria: " + (mensalista != null ? "Mensalista" : "Avulso"));
                writer.newLine();
                writer.write("Placa: " + (veiculo != null ? veiculo.getPlaca() : "-"));
                writer.newLine();
                writer.write("Tipo: " + (veiculo != null ? veiculo.getTipo() : "-"));
                writer.newLine();
                writer.write("Vaga: " + registro.getIdVaga());
                writer.newLine();
                writer.write("Entrada: " + formatarDataHora(registro.getDataEntrada()));
                writer.newLine();
                writer.write("Saida: " + formatarDataHora(registro.getDataSaida()));
                writer.newLine();
                writer.write("Valor do registro: " + formatarMoeda(registro.getValorPago()));
                writer.newLine();
                writer.write("Mensalidade: " + formatarMensalidade(mensalista));
                writer.newLine();
                writer.write("----------------------------------------");
                writer.newLine();
            }

            writer.newLine();
            writer.write("Mensalistas cadastrados");
            writer.newLine();
            writer.write("----------------------------------------");
            writer.newLine();

            for (int i = 0; i < mensalistas.size(); i++) {
                Mensalista mensalista = mensalistas.get(i);
                writer.write("Mensalista " + (i + 1));
                writer.newLine();
                writer.write("Nome: " + mensalista.getNome());
                writer.newLine();
                writer.write("Placa: " + mensalista.getPlaca());
                writer.newLine();
                writer.write("Tipo: " + mensalista.getTipoVeiculo());
                writer.newLine();
                writer.write("Vaga reservada: " + mensalista.getIdVagaReservada());
                writer.newLine();
                writer.write("Mensalidade: " + formatarMoeda(mensalista.getValorMensalidade()));
                writer.newLine();
                writer.write("Ativo: " + (mensalista.isAtivo() ? "Sim" : "Nao"));
                writer.newLine();
                writer.write("----------------------------------------");
                writer.newLine();
            }

            writer.newLine();
            writer.write("Total de registros: " + registros.size());
            writer.newLine();
            writer.write("Total de mensalistas: " + mensalistas.size());
            writer.newLine();

            resultado = "Relatorio exportado com sucesso: " + path;
            exportado = true;
        } catch (IOException e) {
            resultado = "Erro ao exportar relatorio: " + e.getMessage();
        } finally {
            logarResultado(resultado);
        }

        return exportado;
    }

    private static DadosParkSys criarDadosVazios() {
        return new DadosParkSys(new HashMap<>(), new ArrayList<>(), new LinkedList<>());
    }

    private static String formatarDataHora(LocalDateTime dataHora) {
        if (dataHora == null) {
            return "Nao registrada";
        }

        return dataHora.format(FORMATADOR_DATA_HORA);
    }

    private static double calcularReceitaAvulsa(List<Registro> registros) {
        double total = 0.0;

        for (Registro registro : registros) {
            total += registro.getValorPago();
        }

        return total;
    }

    private static double calcularReceitaMensalistas(List<Mensalista> mensalistas) {
        double total = 0.0;

        for (Mensalista mensalista : mensalistas) {
            if (mensalista.isAtivo()) {
                total += mensalista.getValorMensalidade();
            }
        }

        return total;
    }

    private static Mensalista buscarMensalistaPorPlaca(List<Mensalista> mensalistas, String placa) {
        if (placa == null) {
            return null;
        }

        for (Mensalista mensalista : mensalistas) {
            if (mensalista.getPlaca().equalsIgnoreCase(placa)) {
                return mensalista;
            }
        }

        return null;
    }

    private static String formatarMensalidade(Mensalista mensalista) {
        return mensalista != null ? formatarMoeda(mensalista.getValorMensalidade()) : "-";
    }

    private static String formatarMoeda(double valor) {
        return FORMATADOR_MOEDA.format(valor);
    }

    private static void logarResultado(String resultado) {
        System.out.println(resultado);
    }

    private static void criarDiretorioPaiSeNecessario(String path) {
        File arquivo = new File(path);
        File diretorioPai = arquivo.getParentFile();

        if (diretorioPai != null) {
            diretorioPai.mkdirs();
        }
    }
}

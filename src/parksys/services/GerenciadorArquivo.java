package parksys.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import parksys.entities.Mensalista;
import parksys.entities.Registro;
import parksys.entities.Vaga;

public class GerenciadorArquivo {
    private static final DateTimeFormatter FORMATADOR_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void serializar(Map<String, Vaga> vagas, List<Registro> registros,
        List<Mensalista> mensalistas, String path) {
        ObjectOutputStream outputStream = null;
        String resultado = "Serializacao nao concluida.";

        try {
            criarDiretorioPaiSeNecessario(path);
            outputStream = new ObjectOutputStream(new FileOutputStream(path));
            outputStream.writeObject(new DadosParkSys(vagas, registros, mensalistas));
            resultado = "Serializacao concluida com sucesso: " + path;
        } catch (Exception e) {
            resultado = "Erro ao serializar dados: " + e.getMessage();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    resultado = "Erro ao fechar arquivo de serializacao: " + e.getMessage();
                }
            }

            System.out.println(resultado);
        }
    }

    public static DadosParkSys desserializar(String path) {
        ObjectInputStream inputStream = null;
        DadosParkSys dados = criarDadosVazios();
        String resultado = "Desserializacao nao concluida.";
        File arquivo = new File(path);

        try {
            if (!arquivo.exists()) {
                resultado = "Arquivo nao encontrado. Estruturas vazias retornadas: " + path;
                return dados;
            }

            inputStream = new ObjectInputStream(new FileInputStream(arquivo));
            dados = (DadosParkSys) inputStream.readObject();
            resultado = "Desserializacao concluida com sucesso: " + path;
        } catch (Exception e) {
            resultado = "Erro ao desserializar dados: " + e.getMessage();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    resultado = "Erro ao fechar arquivo de desserializacao: " + e.getMessage();
                }
            }

            System.out.println(resultado);
        }

        return dados;
    }

    public static void exportarRelatorioTxt(List<Registro> registros, String path) {
        BufferedWriter writer = null;
        String resultado = "Exportacao de relatorio nao concluida.";

        try {
            criarDiretorioPaiSeNecessario(path);
            writer = new BufferedWriter(new FileWriter(path));
            writer.write("Relatorio de Registros - ParkSys");
            writer.newLine();
            writer.write("Gerado em: " + LocalDateTime.now().format(FORMATADOR_DATA_HORA));
            writer.newLine();
            writer.write("========================================");
            writer.newLine();
            writer.newLine();

            double totalReceita = 0.0;

            for (int i = 0; i < registros.size(); i++) {
                Registro registro = registros.get(i);
                totalReceita += registro.getValorPago();

                writer.write("Registro " + (i + 1));
                writer.newLine();
                writer.write("Placa: " + registro.getVeiculo().getPlaca());
                writer.newLine();
                writer.write("Tipo: " + registro.getVeiculo().getTipo());
                writer.newLine();
                writer.write("Vaga: " + registro.getIdVaga());
                writer.newLine();
                writer.write("Entrada: " + formatarDataHora(registro.getDataEntrada()));
                writer.newLine();
                writer.write("Saida: " + formatarDataHora(registro.getDataSaida()));
                writer.newLine();
                writer.write(String.format("Valor pago: R$ %.2f", registro.getValorPago()));
                writer.newLine();
                writer.write("----------------------------------------");
                writer.newLine();
            }

            writer.newLine();
            writer.write("Total de registros: " + registros.size());
            writer.newLine();
            writer.write(String.format("Total de receita: R$ %.2f", totalReceita));
            writer.newLine();

            resultado = "Relatorio exportado com sucesso: " + path;
        } catch (Exception e) {
            resultado = "Erro ao exportar relatorio: " + e.getMessage();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    resultado = "Erro ao fechar relatorio: " + e.getMessage();
                }
            }

            System.out.println(resultado);
        }
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

    private static void criarDiretorioPaiSeNecessario(String path) {
        File arquivo = new File(path);
        File diretorioPai = arquivo.getParentFile();

        if (diretorioPai != null) {
            diretorioPai.mkdirs();
        }
    }
}

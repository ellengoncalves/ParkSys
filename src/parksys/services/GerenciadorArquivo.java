package parksys.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import parksys.entities.Mensalista;
import parksys.entities.Registro;
import parksys.entities.Vaga;

public class GerenciadorArquivo {
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

    private static DadosParkSys criarDadosVazios() {
        return new DadosParkSys(new HashMap<>(), new ArrayList<>(), new LinkedList<>());
    }

    private static void criarDiretorioPaiSeNecessario(String path) {
        File arquivo = new File(path);
        File diretorioPai = arquivo.getParentFile();

        if (diretorioPai != null) {
            diretorioPai.mkdirs();
        }
    }
}

import java.io.File;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Define o caminho para o ficheiro que desejas testar
        File testFile = new File("files/On Sight.mp3");  // Substitui pelo caminho real do ficheiro de teste

        // Verifica se o ficheiro existe
        if (testFile.exists() && testFile.isFile()) {
            // Cria a lista de blocos a partir do ficheiro
            List<FileBlockRequestMessage> blockList = FileBlockManager.createBlockList(testFile);

            // Exibe o número de blocos gerados
            System.out.println("Número de blocos gerados: " + blockList.size());

            // Exibe as informações de cada bloco
            for (FileBlockRequestMessage block : blockList) {
                System.out.println(block);  // O método toString() da classe FileBlockRequestMessage será chamado
            }
        } else {
            System.out.println("Ficheiro de teste não encontrado ou não é um ficheiro válido.");
        }
    }
}

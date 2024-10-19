import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileBlockManager {

    private static final int BLOCK_SIZE = 10240;  // Tamanho de cada bloco (10 KB)

    // Função para dividir o ficheiro em blocos
    public static List<FileBlockRequestMessage> createBlockList(File file) {
        List<FileBlockRequestMessage> blockList = new ArrayList<>();
        long fileSize = file.length();  // Tamanho total do ficheiro
        String fileName = file.getName();  // Nome do ficheiro

        // Criar blocos com base no tamanho do ficheiro
        long offset = 0;
        while (offset < fileSize) {
            int blockSize = (int) Math.min(BLOCK_SIZE, fileSize - offset);  // Último bloco pode ser menor
            blockList.add(new FileBlockRequestMessage(fileName, offset, blockSize));
            offset += blockSize;
        }

        return blockList;  // Retorna a lista de blocos
    }
}

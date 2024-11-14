import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileBlockManager {

    private static final int TAMANHO_BLOCO = 10240; // Tamanho do bloco em bytes
    private static int blocosDescarregados = 0;
    private static int totalBlocos = 0;

    // Incrementa o número de blocos descarregados e exibe progresso
    public static synchronized void incrementarBlocosDescarregados() {
        blocosDescarregados++;
        System.out.println("Progresso: " + blocosDescarregados + "/" + totalBlocos + " blocos descarregados.");
    }

    // Cria a lista de blocos para um arquivo e define o total de blocos
    public static List<FileBlockRequestMessage> createBlockList(File file) {
        List<FileBlockRequestMessage> blockList = new ArrayList<>();
        long fileSize = file.length();
        String fileName = file.getName();

        long offset = 0;
        while (offset < fileSize) {
            int blockSize = (int) Math.min(TAMANHO_BLOCO, fileSize - offset);
            blockList.add(new FileBlockRequestMessage(fileName, offset, blockSize));
            offset += blockSize;
        }

        totalBlocos = blockList.size(); // Define o número total de blocos
        return blockList;
    }

    // Inicia o download dos blocos na lista
    public static void iniciarDescarregamento(List<FileBlockRequestMessage> blockList) {
        for (FileBlockRequestMessage block : blockList) {
            Thread thread = new Thread(new DownloadTaskManager(block));
            thread.start();
        }
    }

    // Representa uma mensagem de requisição de bloco de arquivo
    public static class FileBlockRequestMessage {
        private String fileName;
        private long offset;
        private int length;

        public FileBlockRequestMessage(String fileName, long offset, int length) {
            this.fileName = fileName;
            this.offset = offset;
            this.length = length;
        }

        @Override
        public String toString() {
            return "FileBlockRequestMessage{" +
                    "fileName='" + fileName + '\'' +
                    ", offset=" + offset +
                    ", length=" + length +
                    '}';
        }
    }

    // Tarefa de descarregamento que simula a transferência de um bloco
    public static class DownloadTaskManager implements Runnable {
        private FileBlockRequestMessage block;

        public DownloadTaskManager(FileBlockRequestMessage block) {
            this.block = block;
        }

        @Override
        public void run() {
            System.out.println("A descarregar o bloco: " + block);
            try {
                Thread.sleep(100); // Simula tempo de descarregamento
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            incrementarBlocosDescarregados();
        }
    }

    public static void main(String[] args) {
        File testFile = new File("files2/ficheiro de teste.txt");

        if (testFile.exists() && testFile.isFile()) {
            List<FileBlockRequestMessage> blockList = FileBlockManager.createBlockList(testFile);
            System.out.println("Número total de blocos a descarregar: " + totalBlocos);
            FileBlockManager.iniciarDescarregamento(blockList);
        } else {
            System.out.println("Ficheiro de teste não encontrado ou não é um ficheiro válido.");
        }
    }
}

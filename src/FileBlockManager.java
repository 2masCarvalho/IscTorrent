import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileBlockManager {

    private static final int tamanhoBloco = 10240;
    private static int blocosDescarregados = 0;
    private static int totalBlocos = 0;

    public static synchronized void incrementarBlocosDescarregados() {
        blocosDescarregados++;
        System.out.println("Progresso: " + blocosDescarregados + "/" + totalBlocos + " blocos descarregados.");
    }

    public static List<FileBlockRequestMessage> createBlockList(File file) {
        List<FileBlockRequestMessage> blockList = new ArrayList<>();
        long fileSize = file.length();
        String fileName = file.getName();

        long offset = 0;
        while (offset < fileSize) {
            int blockSize = (int) Math.min(tamanhoBloco, fileSize - offset);
            blockList.add(new FileBlockRequestMessage(fileName, offset, blockSize));
            offset += blockSize;
        }

        totalBlocos = blockList.size(); // Define o número total de blocos
        return blockList;
    }

    public static void iniciarDescarregamento(List<FileBlockRequestMessage> blockList) {
        for (FileBlockRequestMessage block : blockList) {
            Thread thread = new Thread(new DownloadTask(block));
            thread.start();
        }
    }

    public static class FileBlockRequestMessage {
        private String fileName;
        private long offset;
        private int length;

        public FileBlockRequestMessage(String fileName, long offset, int length) {
            this.fileName = fileName;
            this.offset = offset;
            this.length = length;
        }

        public String getFileName() {
            return fileName;
        }

        public long getOffset() {
            return offset;
        }

        public int getLength() {
            return length;
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

    public static class DownloadTask implements Runnable {
        private FileBlockRequestMessage block;

        public DownloadTask(FileBlockRequestMessage block) {
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
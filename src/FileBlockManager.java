import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileBlockManager {

    private static final int BLOCK_SIZE = 10240;

    public static List<FileBlockRequestMessage> createBlockList(File file) {
        List<FileBlockRequestMessage> blockList = new ArrayList<>();
        long fileSize = file.length();
        String fileName = file.getName();

        long offset = 0;
        while (offset < fileSize) {
            int blockSize = (int) Math.min(BLOCK_SIZE, fileSize - offset);
            blockList.add(new FileBlockRequestMessage(fileName, offset, blockSize));
            offset += blockSize;
        }

        return blockList;
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

    public static void main(String[] args) {
        File testFile = new File("files/On Sight.mp3");

        if (testFile.exists() && testFile.isFile()) {

            List<FileBlockRequestMessage> blockList = FileBlockManager.createBlockList(testFile);
            System.out.println("Número de blocos gerados: " + blockList.size());

            for (FileBlockRequestMessage block : blockList) {
                System.out.println(block);
            }
        } else {
            System.out.println("Ficheiro de teste não encontrado ou não é um ficheiro válido.");
        }
    }
}

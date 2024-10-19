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
}

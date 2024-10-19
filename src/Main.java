import java.io.File;
import java.util.List;

public class Main {

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

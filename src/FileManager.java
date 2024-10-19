import java.io.File;
import java.io.FileFilter;

public class FileManager {

    private String folderPath;

    public FileManager(String folderPath) {
        this.folderPath = folderPath;
    }

    public File[] getFiles() {
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            return folder.listFiles(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isFile();
                }
            });
        } else {
            System.out.println("A pasta de trabalho não existe ou não é um diretório.");
            return new File[0];
        }
    }
}

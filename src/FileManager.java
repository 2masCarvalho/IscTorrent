import java.io.File;
import java.io.FileFilter;

public class FileManager {

    private String folderPath; // Caminho da pasta que será gerenciada

    // Construtor que define o caminho da pasta
    public FileManager(String folderPath) {
        this.folderPath = folderPath;
    }

    // Retorna uma lista de arquivos na pasta especificada
    public File[] getFiles() {
        File folder = new File(folderPath);

        // Verifica se o caminho é válido e se é uma pasta
        if (folder.exists() && folder.isDirectory()) {
            // Retorna apenas arquivos (não diretórios) na pasta
            return folder.listFiles(File::isFile);
        } else {
            // Retorna um array vazio se a pasta não existe ou não é válida
            return new File[0];
        }
    }
}
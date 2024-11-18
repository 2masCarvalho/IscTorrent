import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class FileManager {

    private String folderPath; // Caminho da pasta que será gerenciada
    private Map<String, String> fileHashes; // Armazena hashes dos arquivos (nome -> hash)

    public FileManager(String folderPath) {
        this.folderPath = folderPath;
        this.fileHashes = new HashMap<>();
        generateFileHashes();
    }

    // Retorna uma lista de arquivos na pasta especificada
    public File[] getFiles() {
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            return folder.listFiles(File::isFile);
        } else {
            return new File[0];
        }
    }

    // Gera hashes para todos os arquivos na pasta
    private void generateFileHashes() {
        File[] files = getFiles();
        if (files != null) {
            for (File file : files) {
                try {
                    String hash = calculateSHA256(file);
                    fileHashes.put(file.getName(), hash);
                } catch (Exception e) {
                    System.err.println("Erro ao calcular hash para o arquivo " + file.getName() + ": " + e.getMessage());
                }
            }
        }
    }

    // Calcula o hash SHA-256 de um arquivo
    private String calculateSHA256(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        FileInputStream fis = new FileInputStream(file);
        byte[] byteArray = new byte[1024];
        int bytesCount;
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        fis.close();
        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // Retorna o hash de um arquivo pelo nome
    public String getFileHash(String fileName) {
        return fileHashes.getOrDefault(fileName, null);
    }

    // Retorna o mapa completo de hashes
    public Map<String, String> getFileHashes() {
        return fileHashes;
    }
}

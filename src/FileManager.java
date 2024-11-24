import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to manage local files in the P2P system.
 * It calculates file hashes, retrieves file metadata, and handles the shared folder.
 */
public class FileManager {
    private final String folderPath; // Path to the folder containing files
    private final Map<String, String> fileHashes; // Maps file names to their hashes

    /**
     * Constructor for FileManager.
     *
     * @param folderPath The path to the folder containing files to be managed.
     */
    public FileManager(String folderPath) {
        this.folderPath = folderPath;
        this.fileHashes = new HashMap<>();
        loadFiles(); // Load files and calculate their hashes
    }

    /**
     * Loads all files from the folder and calculates their hashes.
     */
    private void loadFiles() {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("Invalid folder path: " + folderPath);
        }

        File[] files = folder.listFiles();
        System.out.println("Files in folder: " + folderPath);
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    System.out.println(" - " + file.getName());
                    String hash = calculateHash(file);
                    if (hash != null) {
                        fileHashes.put(file.getName(), hash);
                    }
                }
            }
        } else {
            System.out.println("No files found in folder: " + folderPath);
        }
    }


    /**
     * Calculates the hash (SHA-256) of a file.
     *
     * @param file The file to hash.
     * @return The hash of the file as a hexadecimal string, or null if an error occurs.
     */
    public String calculateHash(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] byteArray = new byte[1024];
            int bytesRead;

            while ((bytesRead = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesRead);
            }

            byte[] hashBytes = digest.digest();
            return bytesToHex(hashBytes);
        } catch (IOException | NoSuchAlgorithmException e) {
            System.err.println("Error calculating hash for file: " + file.getName() + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Converts a byte array into a hexadecimal string.
     *
     * @param bytes The byte array to convert.
     * @return The hexadecimal string.
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Gets all the files in the folder as an array of File objects.
     *
     * @return An array of files in the folder.
     */
    public File[] getFiles() {
        File folder = new File(folderPath);
        return folder.listFiles(file -> file.isFile()); // Filter only files
    }

    /**
     * Gets the map of file names to their hashes.
     *
     * @return The map of file hashes.
     */
    public Map<String, String> getFileHashes() {
        return fileHashes;
    }

    /**
     * Gets the folder path managed by this FileManager.
     *
     * @return The folder path.
     */
    public String getFolderPath() {
        return folderPath;
    }
}

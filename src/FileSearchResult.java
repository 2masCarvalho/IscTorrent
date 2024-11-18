import java.io.Serializable;

public class FileSearchResult implements Serializable {
    private String fileName;
    private String hash;

    public FileSearchResult(String fileName, String hash) {
        this.fileName = fileName;
        this.hash = hash;
    }

    public String getFileName() {
        return fileName;
    }

    public String getHash() {
        return hash;
    }

    @Override
    public String toString() {
        return "FileSearchResult{fileName='" + fileName + "', hash='" + hash + "'}";
    }
}

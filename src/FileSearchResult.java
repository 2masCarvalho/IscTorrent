import java.io.Serializable;

public class FileSearchResult implements Serializable {
    private static final long serialVersionUID = 1L;

    //Nome do arquivo
    private String fileName;
    // Hash único do arquivo
    private String hash;
    //Tamanho do arquivo em bytes
    private long fileSize;
    //ip do nó que possui o arquivo
    private String nodeAddress;
    //Porto do nó que possui o arquivo
    private int nodePort;
    //Instancia da mensagem de busca associada
    private WordSearchMessage searchQuery;


    public FileSearchResult(String fileName, String hash, long fileSize, String nodeAddress, int nodePort, WordSearchMessage searchQuery) {
        this.fileName = fileName;
        this.hash = hash;
        this.fileSize = fileSize;
        this.nodeAddress = nodeAddress;
        this.nodePort = nodePort;
        this.searchQuery = searchQuery;
    }

    public String getFileName() {
        return fileName;
    }

    public String getHash() {
        return hash;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public int getNodePort() {
        return nodePort;
    }

    public WordSearchMessage getSearchQuery() {
        return searchQuery;
    }

    @Override
    public String toString() {
        return "FileSearchResult{" +
                "fileName='" + fileName + '\'' +
                ", hash='" + hash + '\'' +
                ", fileSize=" + fileSize +
                ", nodeAddress='" + nodeAddress + '\'' +
                ", nodePort=" + nodePort +
                ", searchQuery=" + searchQuery +
                '}';
    }
}

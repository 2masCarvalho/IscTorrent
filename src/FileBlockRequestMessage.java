public class FileBlockRequestMessage {

    private String fileName;  // Nome ou hash do ficheiro
    private long offset;      // Offset do bloco no ficheiro (posição inicial)
    private int length;       // Tamanho do bloco

    // Construtor
    public FileBlockRequestMessage(String fileName, long offset, int length) {
        this.fileName = fileName;
        this.offset = offset;
        this.length = length;
    }

    // Getters para obter as informações do bloco
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

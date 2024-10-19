public class FileBlockRequestMessage {

    private String fileName;
    private long offset;
    private int length;

    // Construtor
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

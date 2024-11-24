import java.io.Serializable;

public class WordSearchMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String keyword;
    private final String senderNodeName;

    public WordSearchMessage(String keyword, String senderNodeName) {
        this.keyword = keyword;
        this.senderNodeName = senderNodeName;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getSenderNodeName() {
        return senderNodeName;
    }

    @Override
    public String toString() {
        return "WordSearchMessage{" +
                "keyword='" + keyword + '\'' +
                ", senderNodeName='" + senderNodeName + '\'' +
                '}';
    }
}

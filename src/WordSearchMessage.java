import java.io.Serializable;

/**
 * Class representing a search request message in a P2P system.
 */
public class WordSearchMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String keyword;        // The search keyword
    private String senderNodeName; // The name of the node sending the search request

    /**
     * Constructor to initialize a search message.
     *
     * @param keyword        The search keyword.
     * @param senderNodeName The name of the node sending the search request.
     */
    public WordSearchMessage(String keyword, String senderNodeName) {
        this.keyword = keyword;
        this.senderNodeName = senderNodeName;
    }

    // Getter for the search keyword
    public String getKeyword() {
        return keyword;
    }

    // Getter for the sender node's name
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

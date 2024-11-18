import java.io.Serializable;

public class NewConnectionRequest implements Serializable {
    private String senderNodeName;

    public NewConnectionRequest(String senderNodeName) {
        this.senderNodeName = senderNodeName;
    }

    public String getSenderNodeName() {
        return senderNodeName;
    }

    @Override
    public String toString() {
        return "NewConnectionRequest from node: " + senderNodeName;
    }
}

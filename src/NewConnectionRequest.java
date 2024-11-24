import java.io.Serializable;

//Esta class representa uma mensagem de solicitção de conexão enviada por um nó que quer se conectar a outro nó na rede
//é necessário ser do tipo Serializable pois vai ser enviada através de um canal de objetos ( socket)
//


public class NewConnectionRequest implements Serializable {
    // ID único para garantir que é compatível durante a serialização / deserialização
    private static final long serialVersionUID = 1L;


    private String senderNodeName;
    private String ipAddress;
    private int port;


    //Esta class recebe como campos
    // senderNodeName -> Nome do nó que solicita a conexão
    // ipAddress -> ip do nó solicitante
    // port -> porto do nó colicitante

    public NewConnectionRequest(String senderNodeName, String ipAddress, int port) {
        this.senderNodeName = senderNodeName;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    //Retorna nome do solicitador
    public String getSenderNodeName() {
        return senderNodeName;
    }

    //Retorna ip do solicitador
    public String getIpAddress() {
        return ipAddress;
    }

    //Retorna porto do solicitador
    public int getPort() {
        return port;
    }

    //Representa de forma textual a mensagem de solicitação de conexão
    @Override
    public String toString() {
        return "NewConnectionRequest from node: " + senderNodeName + " [" + ipAddress + ":" + port + "]";
    }
}

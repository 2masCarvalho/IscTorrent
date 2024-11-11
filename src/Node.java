import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Node {
    private String ipAddress;
    private int port;
    private String nodeName;
    //O set evita duplicações se for preciso mudamos para List
    private Set<Node> connectedNodes;
    //Verificar se faz sentido este server socket no futuro
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    //armazena as msg recebidas
    //sprivate Queue<Message> incomingMessages;

    //Para criar um node é necessário fornecer o ip, porto e nome do node.
    public Node(String ipAddress, int port, String nodeName) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.nodeName = nodeName;
        this.connectedNodes = new HashSet<>();
        this.threadPool = Executors.newFixedThreadPool(5); // Ajustável conforme necessidade
        //this.incomingMessages = new LinkedList<>();
    }
    //M1.Inicia o servidor
    public void startServer() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);

        while (true) {
            Socket connectionSocket = serverSocket.accept();
            threadPool.submit(() -> handleNewConnection(connectionSocket));
        }
    }
    //M2 - Estabelece conexão com outro node
    //public void connectToNode(String ipAddress, int port) {}
    //M3 - Trata uma nova conexão adicionando o node a lista de nós conectados
    //private void handleNewConnection(Socket connectionSocket) {
    private void handleNewConnection(Socket connectionSocket) {
        try (ObjectInputStream in = new ObjectInputStream(connectionSocket.getInputStream())) {
            // Processa a mensagem de conexão
            Message message = (Message) in.readObject();
            if (message instanceof NewConnectionRequest) {
                // Aceita a conexão e envia uma resposta
                ObjectOutputStream out = new ObjectOutputStream(connectionSocket.getOutputStream());
                out.writeObject(new ConnectionAcceptedMessage(nodeName, this.ipAddress, this.port));

                // Adiciona o nó à lista de nós conectados
                connectedNodes.add(new Node(message.getIpAddress(), message.getPort(), message.getNodeName()));
                System.out.println("Accepted connection from node: " + message.getIpAddress() + ":" + message.getPort());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //public void sendMessage(Node destinationNode, Message message) {
    //public void receiveMessages() {
}

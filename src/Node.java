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
    //public void startServer()
    //M2 - Estabelece conexão com outro node
    //public void connectToNode(String ipAddress, int port) {}
    //M3 - Trata uma nova conexão adicionando o node a lista de nós conectados
    //private void handleNewConnection(Socket connectionSocket) {
    //public void sendMessage(Node destinationNode, Message message) {
    //public void receiveMessages() {
}

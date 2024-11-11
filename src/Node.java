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
//    //armazena as msg recebidas
//    private Queue<Message> incomingMessages;

    //Para criar um node é necessário fornecer o ip, porto e nome do node.
    public Node(String ipAddress, int port, String nodeName) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.nodeName = nodeName;
        this.connectedNodes = new HashSet<>();
        this.threadPool = Executors.newFixedThreadPool(5); // Ajustável conforme necessidade
//        this.incomingMessages = new LinkedList<Message>();
    }
    //M1.Inicia o servidor
    //Este metodo inicia o servidor
    public void startServer() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);
        //Faz com que o servidor continue a correr e aceita novas conexões
        while (true) {
            Socket connectionSocket = serverSocket.accept();
            System.out.println("New connection from " + connectionSocket.getInetAddress());
            threadPool.submit(() -> handleNewConnection(connectionSocket));
        }
    }
    //M2 - Estabelece conexão com outro node
//    public void connectToNode(String ipAddress, int port) {
//        try (Socket socket = new Socket(ipAddress, port);
//             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
//
//            // Envia uma solicitação de nova conexão
//            Message connectionRequest = new NewConnectionRequest(nodeName, this.ipAddress, this.port);
//            out.writeObject(connectionRequest);
//
//            // Recebe resposta (opcional)
//            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
//            Message response = (Message) in.readObject();
//
//            // Adiciona o nó à lista de conexões
//            if (response instanceof ConnectionAcceptedMessage) {
//                connectedNodes.add(new Node(ipAddress, port, response.getNodeName()));
//                System.out.println("Connected to node: " + ipAddress + ":" + port);
//            }
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
    //public void connectToNode(String ipAddress, int port) {}
    //M3 - Trata uma nova conexão adicionando o node a lista de nós conectados
    //private void handleNewConnection(Socket connectionSocket) {
    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received: " + message);
                out.println("Echo: " + message); // Echo back the message
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //public void sendMessage(Node destinationNode, Message message) {
    //public void receiveMessages() {
}

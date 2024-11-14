import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Node {
    private String ipAddress;
    private int port;
    private String nodeName;
    private Set<NodeInfo> connectedNodes; // Armazena as informações dos nós conectados
    private ServerSocket serverSocket;
    private ExecutorService threadPool;

    public Node(String ipAddress, int port, String nodeName) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.nodeName = nodeName;
        this.connectedNodes = new HashSet<>();
        this.threadPool = Executors.newFixedThreadPool(5); // Ajustável conforme necessidade
    }

    // Método para iniciar o servidor e aceitar conexões de outros nós
    public void startServer() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Servidor iniciado na porta " + port);

        // Loop para aceitar conexões continuamente
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Nova conexão de " + clientSocket.getInetAddress());
            threadPool.submit(() -> handleClientConnection(clientSocket));
        }
    }

    // Método para estabelecer conexão com outro nó
    public void connectToNode(String ipAddress, int port) {
        try {
            Socket socket = new Socket(ipAddress, port);
            connectedNodes.add(new NodeInfo(ipAddress, port, socket));
            System.out.println("Conectado ao nó " + ipAddress + ":" + port);

            // Inicia thread para escutar mensagens do nó conectado
            threadPool.submit(() -> listenForMessages(socket));
        } catch (IOException e) {
            System.err.println("Erro ao conectar ao nó: " + e.getMessage());
        }
    }

    // Método para lidar com conexões de clientes (outros nós conectando)
    private void handleClientConnection(Socket clientSocket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Leitura inicial para identificar o nó conectado
            String nodeName = in.readLine();
            System.out.println("Conectado ao nó: " + nodeName);

            // Adiciona o nó à lista de conexões
            connectedNodes.add(new NodeInfo(clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort(), clientSocket));

            // Escuta mensagens do cliente
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Mensagem recebida de " + nodeName + ": " + message);
                out.println("Eco: " + message); // Exemplo de eco de mensagem
            }
        } catch (IOException e) {
            System.err.println("Erro ao lidar com conexão de cliente: " + e.getMessage());
        }
    }

    // Método para escutar mensagens de um nó conectado
    private void listenForMessages(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Mensagem recebida: " + message);
            }
        } catch (IOException e) {
            System.err.println("Erro ao escutar mensagens: " + e.getMessage());
        }
    }

    // Método para enviar mensagem a todos os nós conectados
    public void sendMessageToAll(String message) {
        for (NodeInfo nodeInfo : connectedNodes) {
            try {
                PrintWriter out = new PrintWriter(nodeInfo.getSocket().getOutputStream(), true);
                out.println(message);
            } catch (IOException e) {
                System.err.println("Erro ao enviar mensagem: " + e.getMessage());
            }
        }
    }

    // Classe auxiliar para armazenar informações dos nós conectados
    private static class NodeInfo {
        private String ipAddress;
        private int port;
        private Socket socket;

        public NodeInfo(String ipAddress, int port, Socket socket) {
            this.ipAddress = ipAddress;
            this.port = port;
            this.socket = socket;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public int getPort() {
            return port;
        }

        public Socket getSocket() {
            return socket;
        }
    }

    public static void main(String[] args) {
        try {
            String localIPAddress = InetAddress.getLocalHost().getHostAddress();
            Node node = new Node(localIPAddress, 8081, "Node1");

            // Inicia o servidor em uma thread separada
            new Thread(() -> {
                try {
                    node.startServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Conecta a outro nó
            node.connectToNode("127.0.0.1", 8082); // Exemplo: conectar a outro nó na mesma máquina
            node.sendMessageToAll("Hello, other nodes!");

        } catch (UnknownHostException e) {
            System.err.println("Erro ao obter endereço IP local: " + e.getMessage());
        }
    }
}

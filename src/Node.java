import javax.swing.*;
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

    // Metodo para iniciar o servidor e aceitar conexões de outros nós
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

    // Metodo para estabelecer conexão com outro nó
    public boolean connectToNode(String ipAddress, int port) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ipAddress, port), 2000); // Timeout de 2 segundos para a conexão
            connectedNodes.add(new NodeInfo(ipAddress, port, socket));
            System.out.println("Conectado ao nó " + ipAddress + ":" + port);

            // Inicia thread para escutar mensagens do nó conectado
            threadPool.submit(() -> listenForMessages(socket));
            return true; // Conexão bem-sucedida
        } catch (IOException e) {
            System.err.println("Erro ao conectar ao nó: " + e.getMessage());
            return false; // Conexão falhou
        }
    }

    // Metodo para lidar com conexões de clientes (outros nós conectando)
    private void handleClientConnection(Socket clientSocket) {
        try {
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());

            out.writeObject(nodeName);
            out.flush();

            // Recebe o nome do nó remoto
            String remoteNodeName = (String) in.readObject();
            System.out.println("Conectado ao nó: " + remoteNodeName);

            // Adiciona o nó à lista de conexões
            NodeInfo newNode = new NodeInfo(clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort(), clientSocket);
            connectedNodes.add(newNode);

            // Escuta mensagens do cliente
            while (true) {
                Object message = in.readObject();
                if (message instanceof String) {
                    System.out.println("Mensagem recebida de " + remoteNodeName + ": " + message);
                    out.writeObject("Eco: " + message); // Envia resposta de eco
                    out.flush();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao lidar com conexão de cliente: " + e.getMessage());
        }
    }

    // Metodo para escutar mensagens de um nó conectado
    private void listenForMessages(Socket socket) {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            while (true) {
                Object message = in.readObject();
                if (message instanceof String) {
                    System.out.println("Mensagem recebida: " + message);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao escutar mensagens: " + e.getMessage());
        }
    }

    // Metodo para enviar mensagem a todos os nós conectados
    public void sendMessageToAll(String message) {
        for (NodeInfo nodeInfo : connectedNodes) {
            try {
                ObjectOutputStream out = new ObjectOutputStream(nodeInfo.getSocket().getOutputStream());
                out.writeObject(message);
                out.flush();
            } catch (IOException e) {
                System.err.println("Erro ao enviar mensagem: " + e.getMessage());
            }
        }
    }

    // Metodo para imprimir todos os nós conectados
    public void printConnectedNodes() {
        System.out.println("Nós conectados:");
        for (NodeInfo nodeInfo : connectedNodes) {
            System.out.println(" - " + nodeInfo.getIpAddress() + ":" + nodeInfo.getPort());
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
}
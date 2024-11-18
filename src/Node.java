import java.io.*;
import java.net.*;
import java.util.*;

public class Node {
    private String ipAddress;
    private int port;
    private String nodeName;
    private Set<NodeInfo> connectedNodes; // Armazena as informações dos nós conectados
    private ServerSocket serverSocket;
    private FileManager fileManager; // Gerenciador de arquivos

    public Node(String ipAddress, int port, String nodeName, String folderPath) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.nodeName = nodeName;
        this.connectedNodes = new HashSet<>();
        this.fileManager = new FileManager(folderPath); // Inicializa o gerenciador de arquivos
    }

    // Método para iniciar o servidor e aceitar conexões de outros nós
    public void startServer() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Servidor iniciado na porta " + port);

        // Loop para aceitar conexões continuamente
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Nova conexão de " + clientSocket.getInetAddress());

            // Cria uma thread individual para cada conexão
            new Thread(() -> handleClientConnection(clientSocket)).start();
        }
    }

    // Método para estabelecer conexão com outro nó
    public boolean connectToNode(String ipAddress, int port) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ipAddress, port), 2000); // Timeout de 2 segundos para a conexão

            // Envia a mensagem NewConnectionRequest ao conectar
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(new NewConnectionRequest(nodeName));
            out.flush();

            // Adiciona o nó conectado à lista de conexões
            connectedNodes.add(new NodeInfo(ipAddress, port, socket));
            System.out.println("Conectado ao nó " + ipAddress + ":" + port);

            // Cria uma thread individual para escutar mensagens do nó conectado
            new Thread(() -> listenForMessages(socket)).start();
            return true; // Conexão bem-sucedida
        } catch (IOException e) {
            System.err.println("Erro ao conectar ao nó: " + e.getMessage());
            return false; // Conexão falhou
        }
    }

    // Método para lidar com conexões de clientes (outros nós conectando)
    private void handleClientConnection(Socket clientSocket) {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

            // Recebe a mensagem NewConnectionRequest
            Object receivedObject = in.readObject();
            if (receivedObject instanceof NewConnectionRequest) {
                NewConnectionRequest request = (NewConnectionRequest) receivedObject;
                System.out.println("Recebido: " + request);
            }

            // Escuta mensagens do cliente
            while (true) {
                Object message = in.readObject();
                if (message instanceof WordSearchMessage) {
                    WordSearchMessage searchMessage = (WordSearchMessage) message;
                    System.out.println("Pesquisa recebida: " + searchMessage.getKeyword());

                    // Procura arquivos correspondentes
                    List<FileSearchResult> results = searchFiles(searchMessage.getKeyword());
                    out.writeObject(results);
                    out.flush();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao lidar com conexão de cliente: " + e.getMessage());
        }
    }

    // Método para escutar mensagens de um nó conectado
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

    // Método para enviar mensagem a todos os nós conectados
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

    // Método para procurar arquivos por palavra-chave localmente
    private List<FileSearchResult> searchFiles(String keyword) {
        List<FileSearchResult> results = new ArrayList<>();
        for (Map.Entry<String, String> entry : fileManager.getFileHashes().entrySet()) {
            if (entry.getKey().contains(keyword)) {
                results.add(new FileSearchResult(entry.getKey(), entry.getValue()));
            }
        }
        return results;
    }

    // Método para pesquisar arquivos em todos os nós conectados
    public List<FileSearchResult> searchFilesAcrossNodes(String keyword) {
        List<FileSearchResult> allResults = new ArrayList<>();

        // Adiciona resultados locais
        allResults.addAll(searchFiles(keyword));

        // Procura nos nós conectados
        for (NodeInfo nodeInfo : connectedNodes) {
            try {
                ObjectOutputStream out = new ObjectOutputStream(nodeInfo.getSocket().getOutputStream());
                ObjectInputStream in = new ObjectInputStream(nodeInfo.getSocket().getInputStream());

                // Envia mensagem de busca
                out.writeObject(new WordSearchMessage(keyword));
                out.flush();

                // Lê resultados
                Object response = in.readObject();
                if (response instanceof List<?>) {
                    List<?> resultList = (List<?>) response;
                    for (Object obj : resultList) {
                        if (obj instanceof FileSearchResult) {
                            allResults.add((FileSearchResult) obj);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Erro ao buscar arquivos no nó: " + e.getMessage());
            }
        }
        return allResults;
    }


    // Método para imprimir todos os nós conectados
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

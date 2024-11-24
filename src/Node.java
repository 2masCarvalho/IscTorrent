import java.io.*;
import java.net.*;
import java.util.*;

public class Node {
    private String ipAddress;
    private int port;
    private String nodeName;
    private Set<NodeInfo> connectedNodes; // Stores connected node information
    private ServerSocket serverSocket;
    private FileManager fileManager; // Manages local files
    private List<FileSearchResult> remoteFileList = new ArrayList<>();

    public Node(String ipAddress, int port, String nodeName, String folderPath) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.nodeName = nodeName;
        this.connectedNodes = new HashSet<>();
        this.fileManager = new FileManager(folderPath); // Initialize file manager
    }

    // Starts the server and listens for connections from other nodes
    public void startServer() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);

        // Continuously accept incoming connections
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New connection from " + clientSocket.getInetAddress());

            // Create a thread to handle each connection
            new Thread(() -> handleClientConnection(clientSocket)).start();
        }
    }

    // Connects to another node
    public boolean connectToNode(String ipAddress, int port) {
        // Check if already connected to the node
        for (NodeInfo node : connectedNodes) {
            if (node.getIpAddress().equals(ipAddress) && node.getPort() == port) {
                System.err.println("Error: Already connected to node " + ipAddress + ":" + port);
                return false;
            }
        }

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ipAddress, port), 2000);

            // Streams for communication
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // Send connection request
            NewConnectionRequest request = new NewConnectionRequest(nodeName, this.ipAddress, this.port);
            out.writeObject(request);
            out.flush();

            // Receive and validate the response
            Object response = in.readObject();
            if (response instanceof String && response.equals("OK")) {
                NodeInfo newNode = new NodeInfo(ipAddress, port, socket);
                connectedNodes.add(newNode);
                System.out.println("Connected to node " + ipAddress + ":" + port);
                return true;
            } else {
                System.err.println("Error: Invalid response from node " + ipAddress + ":" + port);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error connecting to node " + ipAddress + ":" + port + ": " + e.getMessage());
        }
        return false;
    }

    // Updates the remote file list with received files
    private void updateRemoteFileList(List<FileSearchResult> fileList) {
        System.out.println("Files received from remote node:");
        remoteFileList.clear(); // Clear the previous list
        for (FileSearchResult file : fileList) {
            remoteFileList.add(file);
            System.out.println(file);
        }
    }

    public List<FileSearchResult> getRemoteFileList() {
        return remoteFileList;
    }

    // Handles incoming connections from clients (other nodes)
    private void handleClientConnection(Socket clientSocket) {
        try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

            // Read the received object
            Object receivedObject = in.readObject();

            if (receivedObject instanceof NewConnectionRequest) {
                NewConnectionRequest request = (NewConnectionRequest) receivedObject;
                System.out.println("NewConnectionRequest received from: " + request);

                // Check if already connected
                for (NodeInfo node : connectedNodes) {
                    if (node.getIpAddress().equals(request.getIpAddress()) && node.getPort() == request.getPort()) {
                        System.out.println("Already connected to node: " + request);
                        out.writeObject("ALREADY_CONNECTED");
                        out.flush();
                        return;
                    }
                }

                // Add the new node and respond with OK
                NodeInfo newNode = new NodeInfo(request.getIpAddress(), request.getPort(), clientSocket);
                connectedNodes.add(newNode);
                System.out.println("Connection received from: " + request);
                out.writeObject("OK");
                out.flush();

            } else if (receivedObject instanceof WordSearchMessage) {
                WordSearchMessage searchMessage = (WordSearchMessage) receivedObject;
                System.out.println("Search request received for keyword: " + searchMessage.getKeyword());

                // Realiza a busca local
                List<FileSearchResult> results = searchFiles(searchMessage);
                System.out.println("Local search completed. Files found: " + results.size());

                // Envia os resultados de volta
                out.writeObject(results);
                out.flush();
                System.out.println("Results sent back to requester.");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error handling client connection: " + e.getMessage());
        }
    }

    // Searches for files locally based on the provided search message
    private List<FileSearchResult> searchFiles(WordSearchMessage searchMessage) {
        List<FileSearchResult> results = new ArrayList<>();

        System.out.println("Performing local search for keyword: " + searchMessage.getKeyword());

        for (Map.Entry<String, String> entry : fileManager.getFileHashes().entrySet()) {
            if (entry.getKey().contains(searchMessage.getKeyword())) {
                results.add(new FileSearchResult(
                        entry.getKey(),
                        entry.getValue(),
                        new File(fileManager.getFolderPath() + "/" + entry.getKey()).length(),
                        ipAddress,
                        port,
                        searchMessage
                ));
            }
        }

        System.out.println("Local search complete. Files found: " + results.size());
        return results;
    }

    // Searches for files across all connected nodes
    public List<FileSearchResult> searchFilesAcrossNodes(String keyword) {
        List<FileSearchResult> allResults = new ArrayList<>();

        // Adiciona resultados locais
        System.out.println("Searching locally for keyword: " + keyword);
        WordSearchMessage localSearchMessage = new WordSearchMessage(keyword, nodeName);
        allResults.addAll(searchFiles(localSearchMessage));

        // Procura nos nós conectados
        for (NodeInfo nodeInfo : connectedNodes) {
            try {
                System.out.println("Sending search request to node: " + nodeInfo.getIpAddress() + ":" + nodeInfo.getPort());
                ObjectOutputStream out = new ObjectOutputStream(nodeInfo.getSocket().getOutputStream());
                ObjectInputStream in = new ObjectInputStream(nodeInfo.getSocket().getInputStream());

                // Envia a mensagem de busca
                WordSearchMessage searchMessage = new WordSearchMessage(keyword, nodeName);
                out.writeObject(searchMessage);
                out.flush();

                // Recebe os resultados
                Object response = in.readObject();
                if (response instanceof List<?>) {
                    List<?> resultList = (List<?>) response;
                    for (Object obj : resultList) {
                        if (obj instanceof FileSearchResult) {
                            allResults.add((FileSearchResult) obj);
                        }
                    }
                    System.out.println("Results received from node: " + nodeInfo.getIpAddress() + ":" + nodeInfo.getPort());
                } else {
                    System.err.println("Invalid response received from node: " + nodeInfo.getIpAddress() + ":" + nodeInfo.getPort());
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error searching files on node " + nodeInfo.getIpAddress() + ":" + nodeInfo.getPort() + ": " + e.getMessage());
            }
        }

        return allResults;
    }


    // Prints all connected nodes
    public void printConnectedNodes() {
        System.out.println("Connected nodes:");
        for (NodeInfo nodeInfo : connectedNodes) {
            System.out.println(" - " + nodeInfo.getIpAddress() + ":" + nodeInfo.getPort());
        }
    }

    // Helper class to store connected node information
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
    // Método para imprimir os nós conectados periodicamente
    public void startPeriodicNodeLogging() {
        new Thread(() -> {
            while (true) {
                try {
                    // Espera 10 segundos antes de imprimir novamente
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    System.err.println("Periodic logging interrupted: " + e.getMessage());
                }
                printConnectedNodes(); // Chama o método já existente
            }
        }).start();
    }
}
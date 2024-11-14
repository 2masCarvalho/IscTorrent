import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.List;

public class GUI {

    private DefaultListModel<String> listModel; // Modelo para listar arquivos
    private JList<String> resultList; // Lista de resultados na GUI
    private FileManager fileManager; // Gerenciador de arquivos
    private Node node; // Representa o nó da rede

    public GUI(String folderPath, int localPort) {
        try {
            // Obtém o endereço IP local
            String localIPAddress = InetAddress.getLocalHost().getHostAddress();
            System.out.println("IP Local: " + localIPAddress);

            // Criação do node
            node = new Node(localIPAddress, localPort, "MyNode");
            fileManager = new FileManager(folderPath);

            // Cria e exibe a interface gráfica
            createAndShowGUI(localIPAddress, localPort);

            // Inicia o servidor em uma nova thread
            new Thread(() -> {
                try {
                    node.startServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (UnknownHostException e) {
            System.err.println("Erro ao obter o endereço IP local.");
            e.printStackTrace();
        }
    }

    // Configura e exibe a interface gráfica
    public void createAndShowGUI(String ipAddress, int port) {
        JFrame frame = new JFrame("IscTorrent - IP: " + ipAddress + " Porta: " + port);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null); // Centraliza na tela

        // Campo de pesquisa
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel searchLabel = new JLabel("Texto a procurar:");
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Procurar");

        topPanel.add(searchLabel, BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);
        topPanel.add(searchButton, BorderLayout.EAST);

        // Lista de resultados
        listModel = new DefaultListModel<>();
        resultList = new JList<>(listModel);
        JScrollPane scrollPanel = new JScrollPane(resultList);

        // Botões de ação
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
        JButton unloadButton = new JButton("Descarregar");
        JButton connectNodeButton = new JButton("Ligar a Nó");

        buttonPanel.add(unloadButton);
        buttonPanel.add(connectNodeButton);

        // Adiciona componentes ao frame
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.EAST);

        // Botão para atualizar lista de arquivos
        JButton updateButton = new JButton("Atualizar Ficheiros");
        frame.add(updateButton, BorderLayout.SOUTH);

        // Ações dos botões
        updateButton.addActionListener(e -> loadFilesFromFolder());
        unloadButton.addActionListener(e -> unloadSelectedFile(frame));
        connectNodeButton.addActionListener(e -> showConnectionDialog());

        frame.setVisible(true);
        loadFilesFromFolder();
    }

    // Carrega arquivos da pasta para exibir na lista
    public void loadFilesFromFolder() {
        listModel.clear();
        File[] files = fileManager.getFiles();

        if (files.length > 0) {
            for (File file : files) {
                listModel.addElement(file.getName());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Nenhum ficheiro encontrado na pasta.");
        }
    }

    // Realiza o descarregamento do arquivo selecionado
    private void unloadSelectedFile(JFrame frame) {
        String selectedFile = resultList.getSelectedValue();
        if (selectedFile != null) {
            File testFile = new File("files/" + selectedFile);
            if (testFile.exists() && testFile.isFile()) {
                List<FileBlockManager.FileBlockRequestMessage> blockList = FileBlockManager.createBlockList(testFile);
                FileBlockManager.iniciarDescarregamento(blockList);
            } else {
                JOptionPane.showMessageDialog(frame, "Ficheiro não encontrado.");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Nenhum ficheiro selecionado.");
        }
    }

    // Mostra o diálogo de conexão para se conectar a um nó remoto
    private void showConnectionDialog() {
        JDialog connectionDialog = new JDialog();
        connectionDialog.setTitle("Conectar a um Nó");
        connectionDialog.setSize(300, 150);
        connectionDialog.setLayout(new GridLayout(3, 2));

        JLabel addressLabel = new JLabel("Endereço:");
        JTextField addressField = new JTextField();
        JLabel portLabel = new JLabel("Porta:");
        JTextField portField = new JTextField();

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancelar");

        connectionDialog.add(addressLabel);
        connectionDialog.add(addressField);
        connectionDialog.add(portLabel);
        connectionDialog.add(portField);
        connectionDialog.add(cancelButton);
        connectionDialog.add(okButton);

        cancelButton.addActionListener(e -> connectionDialog.dispose());

        okButton.addActionListener(e -> {
            String address = addressField.getText();
            String port = portField.getText();
            System.out.println("Tentando conectar ao endereço: " + address + " na porta: " + port);

            try {
                boolean isConnected = node.connectToNode(address, Integer.parseInt(port));
                if (isConnected) {
                    JOptionPane.showMessageDialog(null, "Conectado ao nó " + address + ":" + port);
                    node.printConnectedNodes(); // Exibe nós conectados
                } else {
                    JOptionPane.showMessageDialog(null, "Não foi possível conectar ao nó " + address + ":" + port,
                            "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Porta inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
            }

            connectionDialog.dispose();
        });

        connectionDialog.setVisible(true);
    }

    public static void main(String[] args) {

        String folderPath1 = "files1";
        int port1 = 8081;

        String folderPath2 = "files2";
        int port2 = 8082;

        new Thread(() -> {
            GUI gui1 = new GUI(folderPath1, port1);
            gui1.node.printConnectedNodes();
        }).start();

        new Thread(() -> {
            GUI gui2 = new GUI(folderPath2, port2);
            gui2.node.printConnectedNodes();
        }).start();
    }
}
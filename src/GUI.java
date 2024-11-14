import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.List;

public class GUI {

    private DefaultListModel<String> listModel;
    private JList<String> resultList;
    private FileManager fileManager;
    private Node node;

    public GUI(String folderPath, int localPort) {
        try {
            String localIPAddress = InetAddress.getLocalHost().getHostAddress();
            System.out.println("IP Local: " + localIPAddress);

            // Criação do node
            node = new Node(localIPAddress, localPort, "MyNode");
            fileManager = new FileManager(folderPath);

            createAndShowGUI();

            // Inicia o servidor em uma nova thread
            Thread serverThread = new Thread(() -> {
                try {
                    node.startServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            serverThread.start();
        } catch (UnknownHostException e) {
            System.err.println("Erro ao obter o endereço IP local.");
            e.printStackTrace();
        }
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("IscTorrent");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(new BorderLayout());

        // Centraliza a janela na tela
        int screenWidth = 1920;
        int screenHeight = 1080;
        int frameWidth = frame.getWidth();
        int frameHeight = frame.getHeight();
        frame.setLocation((screenWidth - frameWidth) / 2, (screenHeight - frameHeight) / 2);

        // Painel superior com campo de pesquisa
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
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPanel = new JScrollPane(resultList);

        // Painel de botões
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
        JButton unloadButton = new JButton("Descarregar");
        JButton connectNodeButton = new JButton("Ligar a Nó");

        buttonPanel.add(unloadButton);
        buttonPanel.add(connectNodeButton);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.EAST);

        // Botão para atualizar lista de arquivos
        JButton updateButton = new JButton("Atualizar Ficheiros");
        frame.add(updateButton, BorderLayout.SOUTH);

        updateButton.addActionListener(e -> loadFilesFromFolder());

        unloadButton.addActionListener(e -> {
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
        });

        connectNodeButton.addActionListener(e -> showConnectionDialog());

        frame.setVisible(true);
        loadFilesFromFolder();
    }

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
            System.out.println("Conectando ao endereço: " + address + " na porta: " + port);

            // Conecta-se ao nó especificado
            try {
                node.connectToNode(address, Integer.parseInt(port));
                JOptionPane.showMessageDialog(null, "Conectado ao nó " + address + ":" + port);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Porta inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
            }

            connectionDialog.dispose();
        });

        connectionDialog.setVisible(true);
    }


    public static void main(String[] args) {
        String folderPath = "files";
        int localPort = 8081;
        new GUI(folderPath, localPort);
    }
}

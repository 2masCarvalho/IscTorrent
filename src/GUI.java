import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.List;

public class GUI {

    private DefaultListModel<String> listModel; // Modelo que lista resultados
    private JList<String> resultList; // Lista gráfica para exibir os resultados
    private FileManager fileManager; // Gerencia os arquivos locais
    private Node node; // Representa o nó associado a este GUI

    public GUI(String folderPath, int localPort) {
        try {
            // Obtém o endereço IP local
            String localIPAddress = InetAddress.getLocalHost().getHostAddress();
            System.out.println("IP Local: " + localIPAddress);

            // Criação do nó e inicialização do FileManager
            node = new Node(localIPAddress, localPort, "MyNode", folderPath);
            fileManager = new FileManager(folderPath);

            // Cria e exibe a interface gráfica
            createAndShowGUI(localIPAddress, localPort);

            // Inicia o servidor em uma thread separada
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

    public Node getNode() {
        return node;
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

        // Ação do botão "Procurar"
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Digite uma palavra-chave para buscar.");
                return;
            }

            new Thread(() -> {
                try {
                    // Envia a mensagem de pesquisa para os nós conectados e busca localmente
                    List<FileSearchResult> results = node.searchFilesAcrossNodes(keyword);

                    SwingUtilities.invokeLater(() -> {
                        listModel.clear();
                        if (results.isEmpty()) {
                            listModel.addElement("Nenhum arquivo encontrado.");
                        } else {
                            for (FileSearchResult result : results) {
                                listModel.addElement(
                                        result.getFileName() + " (hash: " + result.getHash() + ", node: " +
                                                result.getNodeAddress() + ":" + result.getNodePort() + ")"
                                );
                            }
                        }
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                            frame,
                            "Erro ao realizar a pesquisa: " + ex.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    ));
                }
            }).start();
        });
    }

    public void loadFilesFromFolder() {
        listModel.clear();
        File[] files = fileManager.getFiles();

        if (files != null && files.length > 0) {
            for (File file : files) {
                listModel.addElement(file.getName());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Nenhum ficheiro encontrado na pasta.");
        }
    }

    private void unloadSelectedFile(JFrame frame) {
        String selectedFile = resultList.getSelectedValue();
        if (selectedFile != null) {
            JOptionPane.showMessageDialog(frame, "Função de descarregamento não implementada ainda.");
        } else {
            JOptionPane.showMessageDialog(frame, "Nenhum ficheiro selecionado.");
        }
    }

    private void showConnectionDialog() {
        JDialog connectionDialog = new JDialog();
        connectionDialog.setTitle("Conectar a um Nó");
        connectionDialog.setSize(300, 150);
        connectionDialog.setLayout(new GridLayout(3, 2));
        connectionDialog.setModal(true);

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
            String portInput = portField.getText();
            try {
                if (address.isEmpty() || portInput.isEmpty()) {
                    JOptionPane.showMessageDialog(connectionDialog, "Por favor, insira o endereço e a porta.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int port = Integer.parseInt(portInput);
                boolean isConnected = node.connectToNode(address, port);
                if (isConnected) {
                    JOptionPane.showMessageDialog(connectionDialog, "Conectado ao nó " + address + ":" + port);
                    SwingUtilities.invokeLater(() -> {
                        List<FileSearchResult> remoteFiles = node.getRemoteFileList();
                        listModel.clear();
                        for (FileSearchResult file : remoteFiles) {
                            listModel.addElement(file.getFileName() + " (hash: " + file.getHash() + ")");
                        }
                    });
                } else {
                    JOptionPane.showMessageDialog(connectionDialog, "Não foi possível conectar ao nó " + address + ":" + port, "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(connectionDialog, "Porta inválida. Deve ser um número.", "Erro", JOptionPane.ERROR_MESSAGE);
            } finally {
                connectionDialog.dispose();
            }
        });

        connectionDialog.setLocationRelativeTo(null);
        connectionDialog.setVisible(true);
    }
}

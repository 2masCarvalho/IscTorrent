import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class GUI {

    private DefaultListModel<String> listModel;
    private JList<String> resultList;
    private FileManager fileManager;

    public GUI(String folderPath) {
        fileManager = new FileManager(folderPath);
        createAndShowGUI();
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("IscTorrent");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel searchLabel = new JLabel("Texto a procurar:");
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Procurar");

        topPanel.add(searchLabel, BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);
        topPanel.add(searchButton, BorderLayout.EAST);

        listModel = new DefaultListModel<>();
        resultList = new JList<>(listModel);
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPanel = new JScrollPane(resultList);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
        JButton unloadButton = new JButton("Descarregar");
        JButton connectNodeButton = new JButton("Ligar a Nó");

        buttonPanel.add(unloadButton);
        buttonPanel.add(connectNodeButton);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.EAST);

        JButton updateButton = new JButton("Atualizar Ficheiros");
        frame.add(updateButton, BorderLayout.SOUTH);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFilesFromFolder();
            }
        });

        unloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
        });

        connectNodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showConnectionDialog();
            }
        });

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
        JTextField addressField = new JTextField("");
        JLabel portLabel = new JLabel("Porta:");
        JTextField portField = new JTextField("");

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancelar");

        connectionDialog.add(addressLabel);
        connectionDialog.add(addressField);
        connectionDialog.add(portLabel);
        connectionDialog.add(portField);
        connectionDialog.add(cancelButton);
        connectionDialog.add(okButton);

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectionDialog.dispose();
            }
        });

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String address = addressField.getText();
                String port = portField.getText();
                System.out.println("Conectando ao endereço: " + address + " na porta: " + port);

                connectionDialog.dispose();
            }
        });

        connectionDialog.setVisible(true);
    }

    public static void main(String[] args) {
        String folderPath = "files";
        GUI gui = new GUI(folderPath);
    }
}

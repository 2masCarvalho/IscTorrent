import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

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
}

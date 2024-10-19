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

        // Painel de topo com o campo de pesquisa e botão de procurar
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel searchLabel = new JLabel("Texto a procurar:");
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Procurar");

        topPanel.add(searchLabel, BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);
        topPanel.add(searchButton, BorderLayout.EAST);

        // Modelo da lista para armazenar os resultados
        listModel = new DefaultListModel<>();
        resultList = new JList<>(listModel);
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPanel = new JScrollPane(resultList);

        // Painel com os botões para descarregar e conectar-se a um nó
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
        JButton unloadButton = new JButton("Descarregar");
        JButton connectNodeButton = new JButton("Ligar a Nó");

        buttonPanel.add(unloadButton);
        buttonPanel.add(connectNodeButton);

        // Adiciona os componentes ao JFrame
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.EAST);

        // Botão para atualizar a lista de ficheiros
        JButton updateButton = new JButton("Atualizar Ficheiros");
        frame.add(updateButton, BorderLayout.SOUTH);

        // Função para atualizar a lista de ficheiros
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFilesFromFolder();  // Atualiza a lista de ficheiros
            }
        });

        // Exibe a janela
        frame.setVisible(true);

        // Carrega inicialmente os ficheiros da pasta
        loadFilesFromFolder();
    }

    // Função para carregar ficheiros a partir do FileManager
    public void loadFilesFromFolder() {
        listModel.clear();  // Limpa a lista atual
        File[] files = fileManager.getFiles();  // Obtém os ficheiros do FileManager

        if (files.length > 0) {
            for (File file : files) {
                listModel.addElement(file.getName());  // Adiciona o nome do ficheiro à lista
            }
        } else {
            JOptionPane.showMessageDialog(null, "Nenhum ficheiro encontrado na pasta.");
        }
    }
}

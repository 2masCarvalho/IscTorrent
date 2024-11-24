public class main1 {

    public static void main(String[] args) {
        String folderPath = "files1";
        int port = 8081;

        new Thread(() -> {
            GUI gui1 = new GUI(folderPath, port);
            gui1.getNode().printConnectedNodes();
        }).start();
    }
}

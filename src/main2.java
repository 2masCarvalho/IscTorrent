public class main2 {

    public static void main(String[] args) {
        String folderPath = "files2";
        int port = 8082;

        new Thread(() -> {
            GUI gui2 = new GUI(folderPath, port);
            gui2.getNode().printConnectedNodes();
        }).start();
    }
}

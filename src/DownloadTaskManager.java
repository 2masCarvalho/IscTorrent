import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

class DownloadTasksManager {

    private final List<FileBlockRequestMessage> blockList;
    private final AtomicInteger completedBlocks;
    private final int totalBlocks;
    private final ExecutorService threadPool;

    public DownloadTasksManager(List<FileBlockRequestMessage> blockList, int maxThreads) {
        this.blockList = blockList;
        this.completedBlocks = new AtomicInteger(0);
        this.totalBlocks = blockList.size();
        this.threadPool = Executors.newFixedThreadPool(maxThreads); // Limita o número de threads
    }

    public void startDownload() {
        for (FileBlockRequestMessage block : blockList) {
            threadPool.submit(new DownloadTask(block)); // Submete cada tarefa para a ThreadPool
        }
        threadPool.shutdown();
    }

    private class DownloadTask implements Runnable {
        private final FileBlockRequestMessage block;

        public DownloadTask(FileBlockRequestMessage block) {
            this.block = block;
        }

        @Override
        public void run() {
            try {
                // Simula o tempo de descarregamento
                System.out.println("A descarregar o bloco: " + block);
                Thread.sleep(100); // Simula tempo de descarregamento
                // Incrementa o progresso
                int finished = completedBlocks.incrementAndGet();
                System.out.println("Progresso: " + finished + "/" + totalBlocks + " blocos descarregados.");
            } catch (InterruptedException e) {
                System.err.println("Erro ao descarregar o bloco: " + block);
                Thread.currentThread().interrupt();
            }
        }
    }

    public boolean isDownloadComplete() {
        return completedBlocks.get() == totalBlocks;
    }

}


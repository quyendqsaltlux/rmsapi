package saltlux.ctv.tranSS.concurrency;

import saltlux.ctv.tranSS.service.FileSystemStorageService;

import java.util.TimerTask;

public class RemoveFileTask extends TimerTask {
    private FileSystemStorageService storageService;
    private String path;

    public RemoveFileTask(FileSystemStorageService storageService, String path) {
        this.storageService = storageService;
        this.path = path;
    }

    @Override
    public void run() {
        this.storageService.deleteFile(path);
    }
}

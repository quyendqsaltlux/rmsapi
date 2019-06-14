package saltlux.ctv.tranSS.exception;

public class StorageFileExistedException extends StorageException {

    public StorageFileExistedException(String message) {
        super(message);
    }

    public StorageFileExistedException(String message, Throwable cause) {
        super(message, cause);
    }
}
package saltlux.ctv.tranSS.exception;

public class StorageFileTooLargeException extends StorageException {

    public StorageFileTooLargeException(String message) {
        super(message);
    }

    public StorageFileTooLargeException(String message, Throwable cause) {
        super(message, cause);
    }
}
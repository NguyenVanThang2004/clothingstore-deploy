package vn.ClothingStore.util.error;

public class StorageException extends RuntimeException {
    // Constructor that accepts a message
    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}

package yandex.practicum.exception;

public class LoadFromFileException extends RuntimeException {
    public LoadFromFileException(String message) {
        super(message);
    }

    public LoadFromFileException(String message, Throwable cause) {
        super(message, cause);
    }
}

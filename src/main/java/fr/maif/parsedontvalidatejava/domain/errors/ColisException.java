package fr.maif.parsedontvalidatejava.domain.errors;

public class ColisException extends RuntimeException {
    public ColisException() {
    }

    public ColisException(String message) {
        super(message);
    }
}

package fr.maif.parsedontvalidatejava.v2.domain.errors;

public class ColisException extends RuntimeException {
    public ColisException() {
    }

    public ColisException(String message) {
        super(message);
    }
}

package fr.maif.parsedontvalidatejava.domain.errors;

public class EtatInvalide extends ColisException {
    public EtatInvalide() {
    }

    public EtatInvalide(String message) {
        super(message);
    }
}

package fr.maif.parsedontvalidatejava.v1.domain.errors;

import fr.maif.parsedontvalidatejava.v2.domain.errors.ColisException;

public class EtatInvalide extends ColisException {
    public EtatInvalide() {
    }

    public EtatInvalide(String message) {
        super(message);
    }
}

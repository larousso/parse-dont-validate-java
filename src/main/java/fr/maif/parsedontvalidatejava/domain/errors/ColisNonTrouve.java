package fr.maif.parsedontvalidatejava.domain.errors;

import fr.maif.parsedontvalidatejava.domain.Colis;
import lombok.Value;

@Value
public class ColisNonTrouve extends ColisException {
    public final Colis.ReferenceColis reference;
}

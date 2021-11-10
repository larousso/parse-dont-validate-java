package fr.maif.parsedontvalidatejava.v2.domain.errors;

import fr.maif.parsedontvalidatejava.v2.domain.Colis;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper=false)
public class ColisNonTrouve extends ColisException {
    public final Colis.ReferenceColis reference;
}

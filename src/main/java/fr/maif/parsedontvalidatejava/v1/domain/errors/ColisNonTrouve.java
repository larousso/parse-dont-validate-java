package fr.maif.parsedontvalidatejava.v1.domain.errors;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper=false)
public class ColisNonTrouve extends ColisException {
    public final String reference;
}

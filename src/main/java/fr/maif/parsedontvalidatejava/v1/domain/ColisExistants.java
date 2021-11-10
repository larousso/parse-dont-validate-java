package fr.maif.parsedontvalidatejava.v1.domain;

import fr.maif.parsedontvalidatejava.v1.domain.model.Colis;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ColisExistants {

    Mono<Colis> chercherColisExistantParReference(String referenceColis);

    Mono<Colis> enregistrerColis(Colis colisPrisEnCharge);

    Mono<Colis> mettreAJourColis(Colis colisExistant);

    Flux<Colis> listerColis();
}

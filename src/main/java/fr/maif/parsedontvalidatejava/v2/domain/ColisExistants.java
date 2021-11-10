package fr.maif.parsedontvalidatejava.v2.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ColisExistants {

    Mono<Colis.ColisExistant> chercherColisExistantParReference(Colis.ReferenceColis referenceColis);

    Mono<Colis.ColisExistant> enregistrerColis(Colis.ColisPrisEnCharge colisPrisEnCharge);

    Mono<Colis.ColisExistant> mettreAJourColis(Colis.ColisExistant colisExistant);

    Flux<Colis.ColisExistant> listerColis();
}

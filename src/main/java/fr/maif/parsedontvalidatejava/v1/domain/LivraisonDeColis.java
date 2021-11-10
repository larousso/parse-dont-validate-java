package fr.maif.parsedontvalidatejava.v1.domain;

import fr.maif.parsedontvalidatejava.v1.domain.model.TypeColis;
import fr.maif.parsedontvalidatejava.v1.domain.model.Colis;
import fr.maif.parsedontvalidatejava.v1.domain.errors.ColisNonTrouve;
import fr.maif.parsedontvalidatejava.v1.domain.errors.EtatInvalide;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static fr.maif.parsedontvalidatejava.v1.domain.model.TypeColis.*;

@Component("LivraisonDeColisClassic")
public class LivraisonDeColis {

    public final ColisExistants colisExistants;

    public LivraisonDeColis(ColisExistants colisExistants) {
        this.colisExistants = colisExistants;
    }

    public Flux<Colis> listerColis() {
        return this.colisExistants.listerColis();
    }


    public Mono<Colis> prendreEnChargeLeColis(Colis colis) {
        if (colis.type.equals(TypeColis.NouveauColis)) {
            var reference = genererReference();
            return this.colisExistants.enregistrerColis(colis.toBuilder()
                    .reference(reference)
                    .type(ColisPrisEnCharge)
                    .build());
        } else {
            return Mono.error(new EtatInvalide("Nouveau colis attendu"));
        }
    }

    public Mono<Colis> gererColis(Colis colis) {
        if (colis.type.equals(TypeColis.NouveauColis)) {
            return Mono.error(new EtatInvalide("Le colis ne doit pas être un nouveau colis"));
        } else {
            return this.colisExistants.chercherColisExistantParReference(colis.reference)
                    .flatMap(colisExistant -> {
                        if ((colisExistant.type.equals(ColisPrisEnCharge) && colis.type.equals(ColisEnCoursDAcheminement)) ||
                                (colisExistant.type.equals(ColisEnCoursDAcheminement) && colis.type.equals(ColisEnCoursDAcheminement)) ||
                                (colisExistant.type.equals(ColisEnCoursDAcheminement) && colis.type.equals(ColisRecu))) {
                            return this.colisExistants.mettreAJourColis(colis);
                        }
                        if (colisExistant.type.equals(ColisPrisEnCharge)) {
                            return Mono.error(new EtatInvalide("On attend un colis à l'état \"ColisEnCoursDAcheminement\""));
                        }
                        if (colisExistant.type.equals(ColisEnCoursDAcheminement)) {
                            return Mono.error(new EtatInvalide("On attend un colis à l'état \"ColisEnCoursDAcheminement\" ou \"ColisPrisEnCharge\""));
                        }
                        if (colisExistant.type.equals(ColisRecu)) {
                            return Mono.error(new EtatInvalide("Le colis est déjà reçu"));
                        }
                        return Mono.error(new EtatInvalide("Cas non géré"));
                    })
                    .switchIfEmpty(Mono.error(new ColisNonTrouve(colis.reference)));
        }
    }

    private String genererReference() {
        return UUID.randomUUID().toString();
    }
}

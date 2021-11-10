package fr.maif.parsedontvalidatejava.v2.domain;

import fr.maif.parsedontvalidatejava.v2.domain.Colis.ColisEnCoursDAcheminement;
import fr.maif.parsedontvalidatejava.v2.domain.Colis.ColisPrisEnCharge;
import fr.maif.parsedontvalidatejava.v2.domain.Colis.ColisRecu;
import fr.maif.parsedontvalidatejava.v2.domain.errors.ColisNonTrouve;
import fr.maif.parsedontvalidatejava.v2.domain.errors.EtatInvalide;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class LivraisonDeColis {

    public final ColisExistants colisExistants;

    public LivraisonDeColis(ColisExistants colisExistants) {
        this.colisExistants = colisExistants;
    }

    public Flux<Colis.ColisExistant> listerColis() {
        return this.colisExistants.listerColis();
    }

    public Mono<? extends Colis> gererColis(Colis colis) {
        return switch (colis) {
            case Colis.NouveauColis nouveauColis -> prendreEnChargeLeColis(nouveauColis);
            case Colis.ColisExistant colisAGerer -> colisExistants
                    .chercherColisExistantParReference(colisAGerer.reference())
                    .flatMap(colisExistant -> gererColisExistant(colisExistant, colisAGerer))
                    .switchIfEmpty(Mono.error(new ColisNonTrouve(colisAGerer.reference())));
        };
    }

    public Mono<Colis.ColisExistant> prendreEnChargeLeColis(Colis.NouveauColis nouveauColis) {
        var reference = genererReference();
        var colisPrisEnCharge = nouveauColis.toColisPrisEnCharge(reference);
        return colisExistants.enregistrerColis(colisPrisEnCharge);
    }

    private Colis.ReferenceColis genererReference() {
        return new Colis.ReferenceColis(UUID.randomUUID().toString());
    }

    Mono<? extends Colis> gererColisExistant(Colis.ColisExistant colisExistant, Colis.ColisExistant colisAGerer) {
        return switch (colisExistant) {
            case ColisPrisEnCharge __ && colisAGerer instanceof ColisEnCoursDAcheminement colisEnCoursAGerer ->
                    mettreAJourColis(colisEnCoursAGerer);
            case ColisEnCoursDAcheminement __ && colisAGerer instanceof ColisEnCoursDAcheminement colisEnCoursAGerer ->
                    mettreAJourColis(colisEnCoursAGerer);
            case ColisEnCoursDAcheminement __  && colisAGerer instanceof ColisRecu colisEnCoursAGerer ->
                    enregistrerColisRecu(colisEnCoursAGerer);
            case ColisPrisEnCharge __ ->
                    Mono.error(new EtatInvalide("On attend un colis à l'état \"ColisEnCoursDAcheminement\""));
            case ColisEnCoursDAcheminement __ ->
                    Mono.error(new EtatInvalide("On attend un colis à l'état \"ColisEnCoursDAcheminement\" ou \"ColisPrisEnCharge\""));
            case ColisRecu __ ->
                    Mono.error(new EtatInvalide("Le colis est déja reçu"));
        };
    }

    private Mono<Colis.ColisExistant> enregistrerColisRecu(ColisRecu colisEnCoursAGerer) {
        return colisExistants.mettreAJourColis(colisEnCoursAGerer);
    }

    private Mono<Colis.ColisExistant> mettreAJourColis(ColisEnCoursDAcheminement colisEnCoursAGerer) {
        return colisExistants.mettreAJourColis(colisEnCoursAGerer);
    }
}

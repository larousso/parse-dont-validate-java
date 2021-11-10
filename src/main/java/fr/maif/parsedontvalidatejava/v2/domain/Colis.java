package fr.maif.parsedontvalidatejava.v2.domain;

import fr.maif.parsedontvalidatejava.libs.Refined;
import io.vavr.control.Option;
import lombok.Builder;

import java.time.LocalDateTime;

import static fr.maif.parsedontvalidatejava.libs.Validations.*;

public sealed interface Colis {

    sealed interface ColisExistant extends Colis {
        ReferenceColis reference();
    }

    DateDEnvoi dateDEnvoi();
    Email email();
    Adresse adresse();

    record NouveauColis(DateDEnvoi dateDEnvoi, Email email, Adresse adresse) implements Colis {
        @Builder
        public NouveauColis {
            throwInvalid(nonNull(dateDEnvoi)
                    .and(nonNull(email))
                    .and(nonNull(adresse))
            );
        }

        public ColisPrisEnCharge toColisPrisEnCharge(ReferenceColis reference) {
            return new ColisPrisEnCharge(reference, dateDEnvoi, email, adresse);
        }
    }

    record ColisPrisEnCharge(ReferenceColis reference, DateDEnvoi dateDEnvoi, Email email, Adresse adresse) implements ColisExistant {
        @Builder
        public ColisPrisEnCharge {
            throwInvalid(nonNull(reference)
                    .and(nonNull(dateDEnvoi))
                    .and(nonNull(email))
                    .and(nonNull(adresse))
            );
        }
    }
    record ColisEnCoursDAcheminement(ReferenceColis reference, DateDEnvoi dateDEnvoi, PositionGps position, Email email, Adresse adresse) implements ColisExistant {
        @Builder
        public ColisEnCoursDAcheminement {
            throwInvalid(nonNull(reference)
                    .and(nonNull(dateDEnvoi))
                    .and(nonNull(email))
                    .and(nonNull(adresse))
            );
        }
    }

    record ColisRecu(ReferenceColis reference, DateDEnvoi dateDEnvoi, DateDeReception dateDeReception, Email email,
                     Adresse adresse) implements ColisExistant {
        @Builder
        public ColisRecu {
            throwInvalid(nonNull(reference)
                    .and(nonNull(dateDEnvoi))
                    .and(nonNull(email))
                    .and(nonNull(adresse))
                    .andThen(() ->
                            doitEtreAvant(dateDEnvoi, dateDeReception, "La date d'envoi doit être avant la date de reception")
                    )
            );
        }
    }

//    sealed interface ReferenceColis { }
//    record PasDeReference() implements ReferenceColis { }
    record ReferenceColis(String value) implements Refined<String> { }

    record PositionGps(int latitude, int longitude) {
        @Builder
        public PositionGps {}
    }

    static String formatterLigneAdresse(String ligne) {
        return ligne.toUpperCase()
                .replaceAll("\\.", " ");
    }

    record CiviliteNomPrenom(String value) implements Refined<String> {
        public CiviliteNomPrenom {
            throwInvalid(nonNull(value).andThen(() ->
                    tailleMax(value, 38)
            ));
        }
    }

    record NoAppEtageCouloirEscalier(String value) implements Refined<String> {
        public NoAppEtageCouloirEscalier {
            throwInvalid(nonNull(value).andThen(() ->
                    tailleMax(value, 38)
            ));
        }
    }

    record EntreeBatimentImmeubleResidence(String value) implements Refined<String> {
        public EntreeBatimentImmeubleResidence {
            throwInvalid(nonNull(value).andThen(() ->
                    tailleMax(value, 38)
            ));
        }
    }

    record NumeroLibelleVoie(String value) implements Refined<String> {
        public NumeroLibelleVoie(String value) {
            throwInvalid(nonNull(value).andThen(() ->
                    tailleMax(value, 38)
            ));
            this.value = formatterLigneAdresse(value);
        }
    }

    record LieuDitServiceParticulierDeDistribution(String value) implements Refined<String> {
        public LieuDitServiceParticulierDeDistribution(String value) {
            throwInvalid(nonNull(value).andThen(() ->
                    tailleMax(value, 38)
            ));
            this.value = formatterLigneAdresse(value);
        }
    }

    record CodePostalEtLocaliteOuCedex(String value) implements Refined<String> {
        public CodePostalEtLocaliteOuCedex(String value) {
            throwInvalid(nonNull(value).andThen(() ->
                    tailleMax(value, 38)
            ));
            this.value = formatterLigneAdresse(value);
        }
    }

    record Pays(String value) implements Refined<String> {
        public Pays(String value) {
            throwInvalid(nonNull(value).andThen(() ->
                    tailleMax(value, 38)
            ));
            this.value = formatterLigneAdresse(value);
        }
    }

    record RaisonSocialeOuDenomination(String value) implements Refined<String> {
        public RaisonSocialeOuDenomination {
            throwInvalid(nonNull(value).andThen(() ->
                    tailleMax(value, 38)
            ));
        }
    }

    record IdentiteDestinataireOuService(String value) implements Refined<String> {
        public IdentiteDestinataireOuService {
            throwInvalid(nonNull(value).andThen(() ->
                    tailleMax(value, 38)
            ));
        }
    }

    record MentionSpecialeEtCommuneGeo(String value) implements Refined<String> {
        public MentionSpecialeEtCommuneGeo(String value) {
            throwInvalid(nonNull(value).andThen(() ->
                    tailleMax(value, 38)
            ));
            this.value = formatterLigneAdresse(value);
        }
    }

    sealed interface Adresse {
        record AdresseBtoB(
                RaisonSocialeOuDenomination ligne1,
                IdentiteDestinataireOuService ligne2,
                Option<EntreeBatimentImmeubleResidence> ligne3,
                NumeroLibelleVoie ligne4,
                Option<MentionSpecialeEtCommuneGeo> ligne5,
                CodePostalEtLocaliteOuCedex ligne6,
                Option<Pays> ligne7
        ) implements Adresse {
            @Builder(toBuilder = true)
            public AdresseBtoB {
                throwInvalid(nonNull(ligne1)
                        .and(nonNull(ligne2))
                        .and(nonNull(ligne3))
                        .and(nonNull(ligne4))
                        .and(nonNull(ligne5))
                        .and(nonNull(ligne6))
                        .and(nonNull(ligne7))
                );
            }
        }

        record AdresseBtoC(
                CiviliteNomPrenom ligne1,
                Option<NoAppEtageCouloirEscalier> ligne2,
                Option<EntreeBatimentImmeubleResidence> ligne3,
                NumeroLibelleVoie ligne4,
                Option<LieuDitServiceParticulierDeDistribution> ligne5,
                CodePostalEtLocaliteOuCedex ligne6,
                Option<Pays> ligne7
        ) implements Adresse {
            @Builder(toBuilder = true)
            public AdresseBtoC {
                throwInvalid(nonNull(ligne1)
                        .and(nonNull(ligne2))
                        .and(nonNull(ligne3))
                        .and(nonNull(ligne4))
                        .and(nonNull(ligne5))
                        .and(nonNull(ligne6))
                        .and(nonNull(ligne7))
                );
            }
        }
    }

    record Email(String value) implements Refined<String> {
        public Email {
            throwInvalid(nonNull(value).andThen(() -> emailValid(value)));
        }
    }

    record DateDEnvoi(LocalDateTime value) implements Refined<LocalDateTime> {
        public DateDEnvoi {
            throwInvalid(nonNull(value).andThen(() -> datePassee(value)));
        }
    }

    record DateDeReception(LocalDateTime value) implements Refined<LocalDateTime> {
        public DateDeReception {
            throwInvalid(nonNull(value).andThen(() -> datePassee(value)));
        }
    }

}

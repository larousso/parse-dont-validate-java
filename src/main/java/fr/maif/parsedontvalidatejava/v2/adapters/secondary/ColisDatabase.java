package fr.maif.parsedontvalidatejava.v2.adapters.secondary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.maif.parsedontvalidatejava.v2.domain.Colis;
import fr.maif.parsedontvalidatejava.libs.db.ColReader;
import io.vavr.control.Option;
import lombok.Builder;

import java.time.LocalDateTime;

import static fr.maif.parsedontvalidatejava.libs.db.ColReader.col;
import static fr.maif.parsedontvalidatejava.libs.db.NamedColReader.*;
import static fr.maif.parsedontvalidatejava.libs.db.NamedColReader.stringCol;

@JsonInclude(JsonInclude.Include.ALWAYS)
record ColisDatabase(
        String reference,
        ColisType type,
        @JsonProperty("date_envoi") LocalDateTime dateDEnvoi,
        @JsonProperty("date_reception") LocalDateTime dateReception,
        Double latitude,
        Double longitude,
        String email,
        @JsonProperty("adresse_type") AdresseType adresseType,
        String ligne1,
        String ligne2,
        String ligne3,
        String ligne4,
        String ligne5,
        String ligne6,
        String ligne7
) {

    enum ColisType {
        ColisPrisEnCharge, ColisEnCoursDAcheminement, ColisRecu;
    }

    enum AdresseType {
        BtoB, BtoC;
    }

    static ColReader<ColisDatabase> rowReader() {
        return col(stringCol("reference"), ColisDatabase.builder()::reference)
                .and(col(stringCol("type"), ColisDatabase.ColisType::valueOf), ColisDatabaseBuilder::type)
                .and(localDateTimeCol("date_envoi"), ColisDatabaseBuilder::dateDEnvoi)
                .and(localDateTimeCol("date_reception"), ColisDatabaseBuilder::dateReception)
                .and(doubleCol("latitude"), ColisDatabaseBuilder::latitude)
                .and(doubleCol("longitude"), ColisDatabaseBuilder::longitude)
                .and(stringCol("email"), ColisDatabaseBuilder::email)
                .and(col(stringCol("adresse_type"), ColisDatabase.AdresseType::valueOf), ColisDatabaseBuilder::adresseType)
                .and(stringCol("ligne1"), ColisDatabaseBuilder::ligne1)
                .and(stringCol("ligne2"), ColisDatabaseBuilder::ligne2)
                .and(stringCol("ligne3"), ColisDatabaseBuilder::ligne3)
                .and(stringCol("ligne4"), ColisDatabaseBuilder::ligne4)
                .and(stringCol("ligne5"), ColisDatabaseBuilder::ligne5)
                .and(stringCol("ligne6"), ColisDatabaseBuilder::ligne6)
                .and(stringCol("ligne7"), ColisDatabaseBuilder::ligne7)
                .map(ColisDatabaseBuilder::build);
    }

    @Builder
    public ColisDatabase {
    }

    static ColisDatabase fromColis(Colis.ColisExistant colisExistant) {
        return switch (colisExistant) {
            case Colis.ColisPrisEnCharge c -> {
                var builder = ColisDatabase.builder()
                        .reference(c.reference().value())
                        .type(ColisType.ColisPrisEnCharge)
                        .dateDEnvoi(c.dateDEnvoi().value())
                        .email(c.email().value());

                yield switch (c.adresse()) {
                    case Colis.Adresse.AdresseBtoC a -> builder.adresseType(AdresseType.BtoC)
                            .ligne1(a.ligne1().value())
                            .ligne2(a.ligne2().map(v -> v.value()).getOrNull())
                            .ligne3(a.ligne3().map(v -> v.value()).getOrNull())
                            .ligne4(a.ligne4().value())
                            .ligne5(a.ligne5().map(v -> v.value()).getOrNull())
                            .ligne6(a.ligne6().value())
                            .ligne7(a.ligne7().map(v -> v.value()).getOrNull())
                            .build();
                    case Colis.Adresse.AdresseBtoB a -> builder.adresseType(AdresseType.BtoB)
                            .ligne1(a.ligne1().value())
                            .ligne2(a.ligne2().value())
                            .ligne3(a.ligne3().map(v -> v.value()).getOrNull())
                            .ligne4(a.ligne4().value())
                            .ligne5(a.ligne5().map(v -> v.value()).getOrNull())
                            .ligne6(a.ligne6().value())
                            .ligne7(a.ligne7().map(v -> v.value()).getOrNull())
                            .build();
                };
            }
            case Colis.ColisEnCoursDAcheminement c -> {
                var builder = ColisDatabase.builder()
                        .reference(c.reference().value())
                        .type(ColisType.ColisEnCoursDAcheminement)
                        .dateDEnvoi(c.dateDEnvoi().value())
                        .latitude(c.position().latitude())
                        .longitude(c.position().longitude())
                        .email(c.email().value());

                yield switch (c.adresse()) {
                    case Colis.Adresse.AdresseBtoC a -> builder.adresseType(AdresseType.BtoC)
                            .ligne1(a.ligne1().value())
                            .ligne2(a.ligne2().map(v -> v.value()).getOrNull())
                            .ligne3(a.ligne3().map(v -> v.value()).getOrNull())
                            .ligne4(a.ligne4().value())
                            .ligne5(a.ligne5().map(v -> v.value()).getOrNull())
                            .ligne6(a.ligne6().value())
                            .ligne7(a.ligne7().map(v -> v.value()).getOrNull())
                            .build();
                    case Colis.Adresse.AdresseBtoB a -> builder.adresseType(AdresseType.BtoB)
                            .ligne1(a.ligne1().value())
                            .ligne2(a.ligne2().value())
                            .ligne3(a.ligne3().map(v -> v.value()).getOrNull())
                            .ligne4(a.ligne4().value())
                            .ligne5(a.ligne5().map(v -> v.value()).getOrNull())
                            .ligne6(a.ligne6().value())
                            .ligne7(a.ligne7().map(v -> v.value()).getOrNull())
                            .build();
                };
            }
            case Colis.ColisRecu c -> {
                var builder = ColisDatabase.builder()
                        .reference(c.reference().value())
                        .type(ColisType.ColisRecu)
                        .dateDEnvoi(c.dateDEnvoi().value())
                        .dateReception(c.dateDeReception().value())
                        .latitude(null)
                        .longitude(null)
                        .email(c.email().value());

                yield switch (c.adresse()) {
                    case Colis.Adresse.AdresseBtoC a -> builder.adresseType(AdresseType.BtoC)
                            .ligne1(a.ligne1().value())
                            .ligne2(a.ligne2().map(v -> v.value()).getOrNull())
                            .ligne3(a.ligne3().map(v -> v.value()).getOrNull())
                            .ligne4(a.ligne4().value())
                            .ligne5(a.ligne5().map(v -> v.value()).getOrNull())
                            .ligne6(a.ligne6().value())
                            .ligne7(a.ligne7().map(v -> v.value()).getOrNull())
                            .build();
                    case Colis.Adresse.AdresseBtoB a -> builder.adresseType(AdresseType.BtoB)
                            .ligne1(a.ligne1().value())
                            .ligne2(a.ligne2().value())
                            .ligne3(a.ligne3().map(v -> v.value()).getOrNull())
                            .ligne4(a.ligne4().value())
                            .ligne5(a.ligne5().map(v -> v.value()).getOrNull())
                            .ligne6(a.ligne6().value())
                            .ligne7(a.ligne7().map(v -> v.value()).getOrNull())
                            .build();
                };
            }
        };
    }

    public Colis.ColisExistant toColis() {
        var adresse = switch (adresseType) {
            case BtoB -> new Colis.Adresse.AdresseBtoB(
                    new Colis.RaisonSocialeOuDenomination(ligne1),
                    new Colis.IdentiteDestinataireOuService(ligne2),
                    Option.of(this.ligne3).map(Colis.EntreeBatimentImmeubleResidence::new),
                    new Colis.NumeroLibelleVoie(ligne4),
                    Option.of(this.ligne5).map(Colis.MentionSpecialeEtCommuneGeo::new),
                    new Colis.CodePostalEtLocaliteOuCedex(ligne6),
                    Option.of(ligne7).map(Colis.Pays::new)
            );
            case BtoC -> new Colis.Adresse.AdresseBtoC(
                    new Colis.CiviliteNomPrenom(ligne1),
                    Option.of(ligne2).map(Colis.NoAppEtageCouloirEscalier::new),
                    Option.of(this.ligne3).map(Colis.EntreeBatimentImmeubleResidence::new),
                    new Colis.NumeroLibelleVoie(ligne4),
                    Option.of(this.ligne5).map(Colis.LieuDitServiceParticulierDeDistribution::new),
                    new Colis.CodePostalEtLocaliteOuCedex(ligne6),
                    Option.of(ligne7).map(Colis.Pays::new)
            );
        };
        return switch (this.type) {
            case ColisPrisEnCharge -> new Colis.ColisPrisEnCharge(
                    new Colis.ReferenceColis(this.reference),
                    new Colis.DateDEnvoi(this.dateDEnvoi),
                    new Colis.Email(this.email),
                    adresse
            );
            case ColisEnCoursDAcheminement -> new Colis.ColisEnCoursDAcheminement(
                    new Colis.ReferenceColis(this.reference),
                    new Colis.DateDEnvoi(this.dateDEnvoi),
                    new Colis.PositionGps(this.latitude, this.longitude),
                    new Colis.Email(this.email),
                    adresse
            );
            case ColisRecu -> new Colis.ColisRecu(
                    new Colis.ReferenceColis(this.reference),
                    new Colis.DateDEnvoi(this.dateDEnvoi),
                    new Colis.DateDeReception(this.dateReception),
                    new Colis.Email(this.email),
                    adresse
            );
        };
    }
}

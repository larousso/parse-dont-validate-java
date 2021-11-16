package fr.maif.parsedontvalidatejava.v1.adapters.secondary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.maif.parsedontvalidatejava.v1.domain.model.Adresse;
import fr.maif.parsedontvalidatejava.v1.domain.model.Colis;
import fr.maif.parsedontvalidatejava.v1.domain.model.TypeAdresse;
import fr.maif.parsedontvalidatejava.v1.domain.model.TypeColis;
import fr.maif.parsedontvalidatejava.libs.db.ColReader;
import lombok.Builder;

import java.time.LocalDateTime;
import static fr.maif.parsedontvalidatejava.v1.adapters.secondary.ColisDatabase.AdresseType.BtoB;
import static fr.maif.parsedontvalidatejava.v1.adapters.secondary.ColisDatabase.AdresseType.BtoC;
import static fr.maif.parsedontvalidatejava.libs.db.ColReader.col;
import static fr.maif.parsedontvalidatejava.libs.db.NamedColReader.*;


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

    static ColisDatabase fromColis(Colis colis) {
        var typeAdresse = switch (colis.adresse.type) {
            case AdresseBtoB -> BtoB;
            case AdresseBtoC -> BtoC;
        };
        var type = switch (colis.type) {
            case ColisPrisEnCharge -> ColisType.ColisPrisEnCharge;
            case ColisEnCoursDAcheminement -> ColisType.ColisEnCoursDAcheminement;
            case ColisRecu -> ColisType.ColisRecu;
            case NouveauColis -> null;
        };
        return ColisDatabase.builder()
                .type(type)
                .reference(colis.reference)
                .dateDEnvoi(colis.dateDEnvoi)
                .adresseType(typeAdresse)
                .dateReception(colis.dateReception)
                .latitude(colis.latitude)
                .longitude(colis.longitude)
                .ligne1(colis.adresse.ligne1)
                .ligne2(colis.adresse.ligne2)
                .ligne3(colis.adresse.ligne3)
                .ligne4(colis.adresse.ligne4)
                .ligne5(colis.adresse.ligne5)
                .ligne6(colis.adresse.ligne6)
                .ligne7(colis.adresse.ligne7)
                .email(colis.email)
                .build();
    }

    public Colis toColis() {
        var typeAdresse = switch (this.adresseType()) {
            case BtoB -> TypeAdresse.AdresseBtoB;
            case BtoC -> TypeAdresse.AdresseBtoC;
        };

        var adresse = Adresse.builder()
                .type(typeAdresse)
                .ligne1(this.ligne1())
                .ligne2(this.ligne2())
                .ligne3(this.ligne3())
                .ligne4(this.ligne4())
                .ligne5(this.ligne5())
                .ligne6(this.ligne6())
                .ligne7(this.ligne7())
                .build();

        return Colis.builder()
                .type(TypeColis.valueOf(this.type().name()))
                .reference(this.reference())
                .dateDEnvoi(this.dateDEnvoi())
                .dateReception(this.dateReception())
                .latitude(this.latitude())
                .longitude(this.longitude())
                .adresse(adresse)
                .email(this.email())
                .build();
    }
}

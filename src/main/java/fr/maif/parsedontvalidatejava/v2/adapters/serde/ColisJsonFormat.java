package fr.maif.parsedontvalidatejava.v2.adapters.serde;

import fr.maif.json.Json;
import fr.maif.json.JsonFormat;
import fr.maif.json.JsonRead;
import fr.maif.parsedontvalidatejava.libs.Refined;
import fr.maif.parsedontvalidatejava.v2.domain.Colis;
import fr.maif.parsedontvalidatejava.v2.domain.Colis.*;
import fr.maif.parsedontvalidatejava.v2.domain.Colis.Adresse.AdresseBtoB;
import fr.maif.parsedontvalidatejava.v2.domain.Colis.Adresse.AdresseBtoB.AdresseBtoBBuilder;
import fr.maif.parsedontvalidatejava.v2.domain.Colis.Adresse.AdresseBtoC;
import fr.maif.parsedontvalidatejava.v2.domain.Colis.Adresse.AdresseBtoC.AdresseBtoCBuilder;
import fr.maif.parsedontvalidatejava.v2.domain.Colis.ColisEnCoursDAcheminement.ColisEnCoursDAcheminementBuilder;
import fr.maif.parsedontvalidatejava.v2.domain.Colis.ColisPrisEnCharge.ColisPrisEnChargeBuilder;
import fr.maif.parsedontvalidatejava.v2.domain.Colis.ColisRecu.ColisRecuBuilder;
import fr.maif.parsedontvalidatejava.v2.domain.Colis.NouveauColis.NouveauColisBuilder;
import fr.maif.parsedontvalidatejava.v2.domain.Colis.PositionGps.PositionGpsBuilder;

import static fr.maif.json.Json.$$;
import static fr.maif.json.JsonFormat._$double;
import static fr.maif.json.JsonRead.*;
import static fr.maif.json.JsonWrite.$localdatetime;
import static fr.maif.parsedontvalidatejava.libs.Refined.refinedString;
import static io.vavr.API.$;

public interface ColisJsonFormat {

    static JsonFormat<Colis> colisFormat() {
        return JsonFormat.of(
                JsonRead.oneOf(_string("type"),
                        caseOf($(NouveauColis.class.getSimpleName()), nouveauColisFormat()),
                        caseOf($(ColisPrisEnCharge.class.getSimpleName()), colisPrisEnChargeFormat()),
                        caseOf($(ColisEnCoursDAcheminement.class.getSimpleName()), colisEnCoursDAcheminementFormat()),
                        caseOf($(ColisRecu.class.getSimpleName()), colisRecuFormat())
                ),
                (Colis colis) -> switch (colis) {
                    case NouveauColis nouveauColis -> nouveauColisFormat().write(nouveauColis);
                    case ColisPrisEnCharge colisPrisEnCharge -> colisPrisEnChargeFormat().write(colisPrisEnCharge);
                    case ColisEnCoursDAcheminement colisEnvoye -> colisEnCoursDAcheminementFormat().write(colisEnvoye);
                    case ColisRecu colisRecu -> colisRecuFormat().write(colisRecu);
                }
        );
    }

    static JsonFormat<Colis.ColisExistant> colisExistantFormat() {
        return JsonFormat.of(
                JsonRead.oneOf(_string("type"),
                        caseOf($(ColisPrisEnCharge.class.getSimpleName()), colisPrisEnChargeFormat()),
                        caseOf($(ColisEnCoursDAcheminement.class.getSimpleName()), colisEnCoursDAcheminementFormat()),
                        caseOf($(ColisRecu.class.getSimpleName()), colisRecuFormat())
                ),
                (Colis.ColisExistant colis) -> switch (colis) {
                    case ColisPrisEnCharge colisPrisEnCharge -> colisPrisEnChargeFormat().write(colisPrisEnCharge);
                    case ColisEnCoursDAcheminement colisEnvoye -> colisEnCoursDAcheminementFormat().write(colisEnvoye);
                    case ColisRecu colisRecu -> colisRecuFormat().write(colisRecu);
                }
        );
    }

    static JsonFormat<NouveauColis> nouveauColisFormat() {
        return JsonFormat.of(
                __("email", emailFormat(), NouveauColis.builder()::email)
                .and(__("dateDEnvoi", dateDEnvoiFormat()).orDefault(DateDEnvoi.now()), NouveauColisBuilder::dateDEnvoi)
                .and(__("adresse", adresseFormat()), NouveauColisBuilder::adresse)
                .map(NouveauColisBuilder::build),
                (NouveauColis colis) -> Json.obj(
                        $$("type", colis.getClass().getSimpleName()),
                        $$("dateDEnvoi", colis.dateDEnvoi(), dateDEnvoiFormat()),
                        $$("email", colis.email(), emailFormat()),
                        $$("adresse", colis.adresse(), adresseFormat())
                )
        );
    }

    static JsonFormat<ColisPrisEnCharge> colisPrisEnChargeFormat() {
        return JsonFormat.of(
                __("dateDEnvoi", dateDEnvoiFormat(), ColisPrisEnCharge.builder()::dateDEnvoi)
                .and(__("reference", referenceFormat()), ColisPrisEnChargeBuilder::reference)
                .and(__("email", emailFormat()), ColisPrisEnChargeBuilder::email)
                .and(__("adresse", adresseFormat()), ColisPrisEnChargeBuilder::adresse)
                .map(ColisPrisEnChargeBuilder::build),
                (ColisPrisEnCharge colis) -> Json.obj(
                        $$("reference", colis.reference(), referenceFormat()),
                        $$("type", colis.getClass().getSimpleName()),
                        $$("dateDEnvoi", colis.dateDEnvoi(), dateDEnvoiFormat()),
                        $$("email", colis.email(), emailFormat()),
                        $$("adresse", colis.adresse(), adresseFormat())
                )
        );
    }

    static JsonFormat<ColisEnCoursDAcheminement> colisEnCoursDAcheminementFormat() {
        return JsonFormat.of(
                __("dateDEnvoi", dateDEnvoiFormat(), ColisEnCoursDAcheminement.builder()::dateDEnvoi)
                .and(__("reference", referenceFormat()), ColisEnCoursDAcheminementBuilder::reference)
                .and(__("email", emailFormat()), ColisEnCoursDAcheminementBuilder::email)
                .and(__("adresse", adresseFormat()), ColisEnCoursDAcheminementBuilder::adresse)
                .and(__("position", positionFormat()), ColisEnCoursDAcheminementBuilder::position)
                .map(ColisEnCoursDAcheminementBuilder::build),
                (ColisEnCoursDAcheminement colis) -> Json.obj(
                        $$("reference", colis.reference(), referenceFormat()),
                        $$("type", colis.getClass().getSimpleName()),
                        $$("dateDEnvoi", colis.dateDEnvoi(), dateDEnvoiFormat()),
                        $$("email", colis.email(), emailFormat()),
                        $$("position", colis.position(), positionFormat()),
                        $$("adresse", colis.adresse(), adresseFormat())
                )
        );
    }

    static JsonFormat<PositionGps> positionFormat() {
        return JsonFormat.of(
                __("latitude", _double(), PositionGps.builder()::latitude)
                .and(__("longitude", _double()), PositionGpsBuilder::longitude)
                .map(PositionGpsBuilder::build),
                (PositionGps gps) -> Json.obj(
                    $$("latitude", gps.latitude(), _$double()),
                    $$("longitude", gps.longitude(), _$double())
                )
        );
    }

    static JsonFormat<ColisRecu> colisRecuFormat() {
        return JsonFormat.of(
                __("dateDEnvoi", dateDEnvoiFormat(), ColisRecu.builder()::dateDEnvoi)
                .and(__("reference", referenceFormat()), ColisRecuBuilder::reference)
                .and(__("dateDeReception", dateDeReceptionFormat()), ColisRecuBuilder::dateDeReception)
                .and(__("email", emailFormat()), ColisRecuBuilder::email)
                .and(__("adresse", adresseFormat()), ColisRecuBuilder::adresse)
                .map(ColisRecuBuilder::build),
                (ColisRecu colis) -> Json.obj(
                        $$("reference", colis.reference(), referenceFormat()),
                        $$("type", colis.getClass().getSimpleName()),
                        $$("dateDEnvoi", colis.dateDEnvoi(), dateDEnvoiFormat()),
                        $$("dateDeReception", colis.dateDeReception(), dateDeReceptionFormat()),
                        $$("email", colis.email(), emailFormat()),
                        $$("adresse", colis.adresse(), adresseFormat())
                )
        );
    }

    private static JsonFormat<Colis.Email> emailFormat() {
        return refinedString(Colis.Email::new);
    }

    static JsonFormat<Adresse> adresseFormat() {
        return JsonFormat.of(
                JsonRead.oneOf(_string("type"),
                caseOf($(AdresseBtoB.class.getSimpleName()), adresseBtoBFormat()),
                caseOf($(AdresseBtoC.class.getSimpleName()), adresseBtoCFormat())
            ),
            (Adresse adresse) -> switch (adresse) {
                case AdresseBtoB adresseBtoB -> adresseBtoBFormat().write(adresseBtoB);
                case AdresseBtoC adresseBtoC -> adresseBtoCFormat().write(adresseBtoC);
            }
        );
    }

    static JsonFormat<AdresseBtoB> adresseBtoBFormat() {
        return JsonFormat.of(
                __("ligne1", raisonSocialeOuDenominationFormat(), AdresseBtoB.builder()::ligne1)
                .and(__("ligne2", identiteDestinataireOuServiceFormat()), AdresseBtoBBuilder::ligne2)
                .and(_opt("ligne3", entreeBatimentImmeubleResidenceFormat()), AdresseBtoBBuilder::ligne3)
                .and(__("ligne4", numeroLibelleVoieFormat()), AdresseBtoBBuilder::ligne4)
                .and(_opt("ligne5", mentionSpecialeEtCommuneGeoFormat()), AdresseBtoBBuilder::ligne5)
                .and(__("ligne6", codePostalEtLocaliteOuCedexFormat()), AdresseBtoBBuilder::ligne6)
                .and(_opt("ligne7", paysFormat()), AdresseBtoBBuilder::ligne7)
                .map(AdresseBtoBBuilder::build),
                adresse -> Json.obj(
                    $$("type", AdresseBtoB.class.getSimpleName()),
                    $$("ligne1", adresse.ligne1(), raisonSocialeOuDenominationFormat()),
                    $$("ligne2", adresse.ligne2(), identiteDestinataireOuServiceFormat()),
                    $$("ligne3", adresse.ligne3(), entreeBatimentImmeubleResidenceFormat()),
                    $$("ligne4", adresse.ligne4(), numeroLibelleVoieFormat()),
                    $$("ligne5", adresse.ligne5(), mentionSpecialeEtCommuneGeoFormat()),
                    $$("ligne6", adresse.ligne6(), codePostalEtLocaliteOuCedexFormat()),
                    $$("ligne7", adresse.ligne7(), paysFormat())
                )
            );
    }

    static JsonFormat<AdresseBtoC> adresseBtoCFormat() {
        return JsonFormat.of(
                __("ligne1", civiliteNomPrenomFormat(), AdresseBtoC.builder()::ligne1)
                .and(_opt("ligne2", noAppEtageCouloirEscalierFormat()), AdresseBtoCBuilder::ligne2)
                .and(_opt("ligne3", entreeBatimentImmeubleResidenceFormat()), AdresseBtoCBuilder::ligne3)
                .and(__("ligne4", numeroLibelleVoieFormat()), AdresseBtoCBuilder::ligne4)
                .and(_opt("ligne5", lieuDitServiceParticulierDeDistributionFormat()), AdresseBtoCBuilder::ligne5)
                .and(__("ligne6", codePostalEtLocaliteOuCedexFormat()), AdresseBtoCBuilder::ligne6)
                .and(_opt("ligne7", paysFormat()), AdresseBtoCBuilder::ligne7)
                .map(AdresseBtoCBuilder::build),
        adresse -> Json.obj(
                $$("type", AdresseBtoC.class.getSimpleName()),
                $$("ligne1", adresse.ligne1(), civiliteNomPrenomFormat()),
                $$("ligne2", adresse.ligne2(), noAppEtageCouloirEscalierFormat()),
                $$("ligne3", adresse.ligne3(), entreeBatimentImmeubleResidenceFormat()),
                $$("ligne4", adresse.ligne4(), numeroLibelleVoieFormat()),
                $$("ligne5", adresse.ligne5(), lieuDitServiceParticulierDeDistributionFormat()),
                $$("ligne6", adresse.ligne6(), codePostalEtLocaliteOuCedexFormat()),
                $$("ligne7", adresse.ligne7(), paysFormat())
        ));
    }

    private static JsonFormat<ReferenceColis> referenceFormat() {
        return refinedString(ReferenceColis::new);
    }

    private static JsonFormat<LieuDitServiceParticulierDeDistribution> lieuDitServiceParticulierDeDistributionFormat() {
        return refinedString(LieuDitServiceParticulierDeDistribution::new);
    }

    private static JsonFormat<NoAppEtageCouloirEscalier> noAppEtageCouloirEscalierFormat() {
        return refinedString(NoAppEtageCouloirEscalier::new);
    }

    private static JsonFormat<CiviliteNomPrenom> civiliteNomPrenomFormat() {
        return refinedString(CiviliteNomPrenom::new);
    }

    static JsonFormat<CodePostalEtLocaliteOuCedex> codePostalEtLocaliteOuCedexFormat() {
        return refinedString(CodePostalEtLocaliteOuCedex::new);
    }

    private static JsonFormat<MentionSpecialeEtCommuneGeo> mentionSpecialeEtCommuneGeoFormat() {
        return refinedString(MentionSpecialeEtCommuneGeo::new);
    }

    static JsonFormat<EntreeBatimentImmeubleResidence> entreeBatimentImmeubleResidenceFormat() {
        return refinedString(EntreeBatimentImmeubleResidence::new);
    }

    private static JsonFormat<IdentiteDestinataireOuService> identiteDestinataireOuServiceFormat() {
        return refinedString(IdentiteDestinataireOuService::new);
    }

    private static JsonFormat<RaisonSocialeOuDenomination> raisonSocialeOuDenominationFormat() {
        return refinedString(RaisonSocialeOuDenomination::new);
    }

    static JsonFormat<Pays> paysFormat() {
        return refinedString(Pays::new);
    }

    static JsonFormat<NumeroLibelleVoie> numeroLibelleVoieFormat() {
        return refinedString(NumeroLibelleVoie::new);
    }

    private static JsonFormat<Colis.DateDEnvoi> dateDEnvoiFormat() {
        return Refined.format(JsonFormat.of(_isoLocalDateTime(), $localdatetime()), Colis.DateDEnvoi::new);
    }

    private static JsonFormat<Colis.DateDeReception> dateDeReceptionFormat() {
        return Refined.format(JsonFormat.of(_isoLocalDateTime(), $localdatetime()), Colis.DateDeReception::new);
    }

}

package fr.maif.parsedontvalidatejava.adapters.serde;

import fr.maif.json.Json;
import fr.maif.json.JsonFormat;
import fr.maif.json.JsonRead;
import fr.maif.parsedontvalidatejava.domain.Colis;
import fr.maif.parsedontvalidatejava.domain.Colis.*;
import fr.maif.parsedontvalidatejava.domain.Colis.Adresse.AdresseBtoB;
import fr.maif.parsedontvalidatejava.domain.Colis.Adresse.AdresseBtoB.AdresseBtoBBuilder;
import fr.maif.parsedontvalidatejava.domain.Colis.Adresse.AdresseBtoC;
import fr.maif.parsedontvalidatejava.domain.Colis.Adresse.AdresseBtoC.AdresseBtoCBuilder;
import fr.maif.parsedontvalidatejava.domain.Colis.ColisEnvoye.ColisEnvoyeBuilder;
import fr.maif.parsedontvalidatejava.domain.Colis.ColisRecu.ColisRecuBuilder;
import fr.maif.parsedontvalidatejava.libs.Refined;

import static fr.maif.json.Json.$$;
import static fr.maif.json.JsonRead.*;
import static fr.maif.json.JsonWrite.$localdatetime;
import static fr.maif.parsedontvalidatejava.libs.Refined.refinedString;
import static io.vavr.API.$;

public interface ColisJsonFormat {

    static JsonFormat<Colis> colisFormat() {
        return JsonFormat.of(
                JsonRead.oneOf(_string("type"),
                        caseOf($(ColisEnvoye.class.getSimpleName()), colisEnvoyeFormat()),
                        caseOf($(ColisRecu.class.getSimpleName()), colisRecuFormat())
                ),
                (Colis colis) -> switch (colis) {
                    case ColisEnvoye colisEnvoye -> colisEnvoyeFormat().write(colisEnvoye);
                    case ColisRecu colisRecu -> colisRecuFormat().write(colisRecu);
                }
        );
    }
    static JsonFormat<ColisEnvoye> colisEnvoyeFormat() {
        return JsonFormat.of(
                __("dateDEnvoi", dateDEnvoiFormat(), ColisEnvoye.builder()::dateDEnvoi)
                .and(__("email", emailFormat()), ColisEnvoyeBuilder::email)
                .and(__("adresse", adresseFormat()), ColisEnvoyeBuilder::adresse)
                .map(ColisEnvoyeBuilder::build),
                (ColisEnvoye colis) -> Json.obj(
                        $$("dateDEnvoi", colis.dateDEnvoi(), dateDEnvoiFormat()),
                        $$("email", colis.email(), emailFormat()),
                        $$("adresse", colis.adresse(), adresseFormat())
                )
        );
    }
    static JsonFormat<ColisRecu> colisRecuFormat() {
        return JsonFormat.of(
                __("dateDEnvoi", dateDEnvoiFormat(), ColisRecu.builder()::dateDEnvoi)
                .and(__("dateDeReception", dateDeReceptionFormat()), ColisRecuBuilder::dateDeReception)
                .and(__("email", emailFormat()), ColisRecuBuilder::email)
                .and(__("adresse", adresseFormat()), ColisRecuBuilder::adresse)
                .map(ColisRecuBuilder::build),
                (ColisRecu colis) -> Json.obj(
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
                $$("type", AdresseBtoB.class.getSimpleName()),
                $$("ligne1", adresse.ligne1(), civiliteNomPrenomFormat()),
                $$("ligne2", adresse.ligne2(), noAppEtageCouloirEscalierFormat()),
                $$("ligne3", adresse.ligne3(), entreeBatimentImmeubleResidenceFormat()),
                $$("ligne4", adresse.ligne4(), numeroLibelleVoieFormat()),
                $$("ligne5", adresse.ligne5(), lieuDitServiceParticulierDeDistributionFormat()),
                $$("ligne6", adresse.ligne6(), codePostalEtLocaliteOuCedexFormat()),
                $$("ligne7", adresse.ligne7(), paysFormat())
        )
        );
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

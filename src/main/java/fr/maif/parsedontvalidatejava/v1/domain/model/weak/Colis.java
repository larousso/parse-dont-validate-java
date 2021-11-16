package fr.maif.parsedontvalidatejava.v1.domain.model.weak;

import fr.maif.parsedontvalidatejava.v1.domain.model.Adresse;
import fr.maif.parsedontvalidatejava.v1.domain.model.TypeAdresse;
import fr.maif.parsedontvalidatejava.v1.domain.model.TypeColis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

  @Data
  @AllArgsConstructor
  public class Colis {
      public String reference;
      boolean isPrisEnCharge;
      boolean isEnCoursDAcheminement;
      boolean isRecu;
      @NotNull
      public LocalDateTime dateDEnvoi;
      public LocalDateTime dateReception;
      public Double latitude;
      public Double longitude;
      @Email
      @NotNull
      public String email;
      public Adresse adresse;



      static void toto() {


          var colis = new Colis(
                  null,
                  false,
                  true,
                  true,
                  LocalDateTime.now(),
                  null,
                  46.3,
                  -0.46,
                  "jeanpaul",
                  new Adresse(
                  TypeAdresse.AdresseBtoC,
                  "Jean Claude Dusse",
                  null,
                  null,
                  "10 RUE DE LA RUE",
                  null,
                  "79000 NIORT",
                  null
          ));

      }
  }



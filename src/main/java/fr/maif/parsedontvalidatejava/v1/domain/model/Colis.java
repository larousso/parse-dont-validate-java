package fr.maif.parsedontvalidatejava.v1.domain.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

 @Data
 @Builder(toBuilder = true)
 public class Colis {
     public String reference;
     @NotNull
     public TypeColis type;
     @NotNull
     public LocalDateTime dateDEnvoi;
     public LocalDateTime dateReception;
     public Double latitude;
     public Double longitude;
     @Email
     @NotNull
     public String email;
     public Adresse adresse;

     public Colis(String reference, TypeColis type, LocalDateTime dateDEnvoi, LocalDateTime dateReception, Double latitude, Double longitude, String email, Adresse adresse) {
         this.reference = reference;
         this.type = type;
         this.dateDEnvoi = Objects.requireNonNullElse(dateDEnvoi, LocalDateTime.now());
         this.dateReception = dateReception;
         this.latitude = latitude;
         this.longitude = longitude;
         this.email = email;
         this.adresse = adresse;
     }
 }





package fr.maif.parsedontvalidatejava.v1.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Adresse {
        @NotNull
        public TypeAdresse type;
        @NotNull
        public String ligne1;
        public String ligne2;
        public String ligne3;
        @NotNull
        public String ligne4;
        public String ligne5;
        @NotNull
        public String ligne6;
        public String ligne7;

}

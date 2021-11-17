package fr.maif.parsedontvalidatejava;

import fr.maif.parsedontvalidatejava.v2.domain.Colis.*;
import io.vavr.API;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.With;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static io.vavr.API.Tuple;

public interface samples {

    enum Marque {
        PEUGEOT, RENAULT, CITROEN
    }
    sealed interface Vehicule permits Vehicule.Voiture, Vehicule.Scooter, Vehicule.Bus {
        record Voiture (Marque marque, String modele, Integer prix) implements Vehicule {}
        record Scooter(String couleur) implements Vehicule {}
        record Bus(String couleur, Integer nbPlaces) implements Vehicule {}
    }

    enum TypeColis {
        NouveauColis,
        ColisPrisEnCharge,
        ColisEnCoursDAcheminement,
        ColisRecu;
    }

    record Colis(ReferenceColis reference,
                 TypeColis typeColis,
                 DateDEnvoi dateDEnvoi,
                 DateDeReception dateDeReception,
                 PositionGps position,
                 Email email,
                 Adresse adresse) {}

    record Foo(String foo, String bar) {
        @Builder(toBuilder = true)
        public Foo{}
    }

    @Data
    @AllArgsConstructor
    class Bar {
        String foo; String bar;
    }

    static void addToList(List<String> list) {
        extList.add("C");
        list.add("C");
    }

    static int ext = 1;
    static List<String> extList = List.of();

    static int utiliseUneVariableHorsScope(int input) {
        return ext + input;
    }

    static io.vavr.collection.List<String> addToListPure(io.vavr.collection.List<String> list) {
        return list.append("C");
    }

    static Function<Integer, Function<Integer, Integer>> addInt() {
        return ext -> input -> ext + input;
    }

    record NonZeroInt(int value) {}

    double division(double valeur, double par) ;

    double divisionTotal(double valeur, NonZeroInt par) ;

    Either<DivisionParZeroErreur, Double> divisionTotalBis(double valeur, double par);

    record DivisionParZeroErreur() {}

    static boolean log(String texte) {
        System.out.println(texte);
        return true;
    }



    static void usage() {

        var logged = log("Je log un texte");

        Tuple(logged, log("Je log un texte"));
    }

    static void immutabilité() {

        var listDOrigine = API.List("A", "B", "C");
        System.out.println(listDOrigine);
        var cestUneNouvelleList = listDOrigine.append("D");
        System.out.println(cestUneNouvelleList);


        var foo = new Foo("Foo", "Bar");
        var updated = foo.toBuilder().foo("Foo2").build();

        var bar = new Bar("Foo", "Bar");
        bar.setFoo("Foo2");
    }

    static void mutabilité() {

        var listDOrigineMutable = new ArrayList<>();
        listDOrigineMutable.add("A");
        listDOrigineMutable.add("B");
        listDOrigineMutable.add("C");
        System.out.println(listDOrigineMutable);
        listDOrigineMutable.add("D");
        System.out.println(listDOrigineMutable);

    }

}

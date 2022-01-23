# Parse don't validate en java 

## L'abstract du talk 

Avec l'arrivée du jdk 17 et notamment des sealed class et des record, java nous propose de nouvelles fonctionnalités pour utiliser encore plus le système de type et donc le compilateur.

L'approche "parse, don't validate" propose de créer des types riches pour représenter les données plutôt que d'utiliser les types primitifs comme String, Boolean etc et ainsi rendre impossible les états incohérents. 
Dans le cas d'une API, une fois le payload d'une requête parsé, c'est le compilateur qui reprend la main et valide le code pour vous.

Dans ce talk, l'approche vous sera présentée à travers une application de démo et des exemples concrets de code.

## Les slides du talk 

[C'est ici que ça se passe](https://docs.google.com/presentation/d/1AJHnyBabLbjdTo1Ti2o_dzgj1E6AAh1_sWu3XRQJPnE/edit?usp=sharing)

## L'approche classique

Une approche traditionnelle consiste à avoir un POJO représentant les données ou chaque attribut est validé par bean validation. 

Exemple 

```java
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
}
```

Ce pojo est validé en utilisant l'annotation `@Valid` dans le contrôleur ou dans le service.   

```java
@PostMapping
public Mono<ResponseEntity<Colis>> prendreEnChargeLeColis(@RequestBody @Valid Colis colis) {
  return this.livraisonDeColis
                  .prendreEnChargeLeColis(colis)
                  .map(ResponseEntity::ok)
                  .onErrorResume(ColisNonTrouve.class, e ->
                          Mono.just(ResponseEntity.notFound().build())
                  );
}
```

Ici, on peut déjà remarquer que 
 1. Il est facile de se tromper en instanciant l'objet colis. Ex inverser `dateDEnvoi` et `dateReception`.
 2. Les validations conditionnelles ne sont pas simple à faire. Ex valider que la date de reception est non null lorsque le type de colis a la valeur `ColisRecu`.
 3. On peut avoir des états incohérents. Ex `TypeColis.EnCours` avec une `dateReception` renseignée.
 4. Niveau de confiance faible sur le fait qu'une instance de pojo soit valide ou non. Elle ne sera valide que si l'instance est passée par le controller.

## Un peu de théorie 

Une solution pour répondre aux problématiques énoncées précédemment est d'utiliser des ADT "type algébrique de données" (algebric data type) pour représenter les différents états gérés par notre application.

Un type algébrique est soit un "type produit" (product type) un "type somme" (sum type).

### Type produit 

Un produit peut être vu comme un n-uplet ou un pojo. La cardinalité d'un type produit est le produit des cardinalités de chaque type "contenus".

En java, les records permettent facilement de représenter un type produit. 

Par exemple : 

```java
record Voiture (Marque marque, String modele, Integer prix) {}
```

### Type somme 

Un type somme est un union. La cardinalité d'un type somme est la somme des cardinalités de types "contenus". 

En java, on pensera à un enum. 

```java
enum Marque {
   PEUGEOT, RENAULT, CITROEN
}
```

### Type algébrique de données

Un type algébrique combine les types somme et les types produit. 

En java 17 on peut représenter ça par une interface scellée. 

```java
sealed interface Vehicule permits Vehicule.Voiture, Vehicule.Scooter, Vehicule.Bus {
   record Voiture (Marque marque, String modele, Integer prix) implements Vehicule {}
   record Scooter(String couleur) implements Vehicule {}
   record Bus(String couleur, Integer nbPlaces) implements Vehicule {}
}
```

Le mot clé `sealed` indique que l'interface ne peut être implémentée que par une liste finie de classes ou de records. La liste est déclarée avec le mot clé `permits`. 
Dans l'illustation proposée, on pourrait enlever le mot clé `permits` puisque la liste des implémentations est dans le même fichier. 

### Avoir des types précis 

Comme exprimé plus tôt, la validation fonctionne mais n'apporte pas un fort niveau de confiance. 
Plutôt que de valider des types primitifs, pourquoi ne pas créer des types dédiés pour représenter précisement les notions manipulées dans le code. 

à la place de 

```java
class MonPojo {
    @NotNull
    @Email 
    String email; 
}
```

On pourrait avoir 
```java
record MonPojo(Email email) { }
```

avec 

```java
record Email(String value) {
    public Email {
        throwInvalid(nonNull(value).andThen(() -> emailValid(value)));
    }
}
```

Avec cette approche, il est impossible d'avoir une instance d'email qui ne soit pas valide. 
Autre avantage, il n'est plus possible d'inverser plusieurs `String` dans un constructeur car chaque donnée à son propre type. 

Le niveau de confiance devient alors plus fort.  

## Rendre les états incohérents impossibles

L'étape suivante est de rendre les états incohérents impossibles, on va bien sûr utiliser les types algébriques de données. 

Dans le cas du colis, on pourra avoir une représentation comme ceci : 

```java
public sealed interface Colis {

    sealed interface ColisExistant extends Colis {
        ReferenceColis reference();
    }

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

    record ColisPrisEnCharge(ReferenceColis reference, DateDEnvoi dateDEnvoi, Email email,
                             Adresse adresse) implements ColisExistant {
        @Builder
        public ColisPrisEnCharge {
            throwInvalid(nonNull(reference)
                    .and(nonNull(dateDEnvoi))
                    .and(nonNull(email))
                    .and(nonNull(adresse))
            );
        }
    }

    record ColisEnCoursDAcheminement(ReferenceColis reference, DateDEnvoi dateDEnvoi, PositionGps position, Email email,
                                     Adresse adresse) implements ColisExistant {
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
}
```

De cette façon, on a la liste exhaustive de chaque état. Il n'est plus possible de créer un état incohérent.

En utilisant le `switch` de java 17 il est possible de "pattern matcher" sur les types et le compilateur va nous alerter s'il y a un oubli : 

```java
var texte = switch(colis) {
   case  NouveauColis c -> "c'est un nouveau colis";
   case  ColisPrisEnCharge c -> "c'est un colis pris en charge";
   case  ColisEnCoursDAcheminement c -> "c'est un colis en cours d'acheminement";
   case  ColisRecu c -> "c'est un colis reçu";
}; 
```

Si un `case` est oublié, il y'aura une erreur de compilation. 


## Parse don't validate 

Maintenant qu'on a des classes qui représentent nos états et nos données de façon stricte, comment passe-t-on d'un monde http / json unsafe au monde ADT safe. Et bien en écrivant des parsers. 

Dans la solution proposée, la lib [`functional-json`](https://github.com/maif/functional-json) est utilisée. Dans cette approche on parse chaque attribue du json dans le bon format. La librairie permet d'empiler toutes les erreurs rencontrées lors de la lecture d'un objet.

Par exemple ici, on définit un reader et writer pour créer une instance de `ColisPrisEnCharge` :
```java
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
```

Dans notre base de code on a donc 2 zones : 
 * une zone fortement typée avec un fort degré de confiance : le code métier  
 * une zone faiblement typée sans garanties : 
   * controller http
   * accès à la base de données 

L'approche "parse don't validate" est un bon complément de l'architecture hexagonale. 


## Jouer avec le code 

### Démarrer l'app

```
docker-compose up 
```

```
./gradlew bootRun 
```

### Utiliser l'API

```bash 
curl -XGET http://localhost:8080/api/v1/colis | jq 
curl -XGET http://localhost:8080/api/v2/colis | jq 
```


```bash 
curl -XPOST http://localhost:8080/api/v1/colis -H 'Content-Type:application/json' -d '{
    "type": "NouveauColis", 
    "email": "jdusse@maif.fr",
    "adresse": {
        "type": "AdresseBtoC", 
        "ligne1": "Jean Claude Dusse", 
        "ligne4": "10 rue de la rue",
        "ligne6": "79000 Niort"
    }
}' 
```
Ou

```bash 
curl -XPOST http://localhost:8080/api/v2/colis -H 'Content-Type:application/json' -d '{
    "type": "NouveauColis", 
    "email": "jdusse@maif.fr",
    "adresse": {
        "type": "AdresseBtoC", 
        "ligne1": "Jean Claude Dusse", 
        "ligne4": "10 rue de la rue",
        "ligne6": "79000 Niort"
    }
}' 
```
Invalide: 

```bash
curl -XPOST http://localhost:8080/api/v2/colis -H 'Content-Type:application/json' -d '{
    "type": "NouveauColis",
    "email": "jdusse@maiffr",
    "adresse": {
        "type": "AdresseBtoC",
        "ligne1": "Jean Claude Dusse qui habite dans une rue qui va bien finir par dépasser les 38 caractères autorisés",
        "ligne4": "10 rue de la rue",
        "ligne6": "79000 Niort"
    }
}' | jq
```


```bash 
curl -XPUT http://localhost:8080/api/v2/colis/4bcdeac1-3aa7-4a7a-91a4-b5d3e40adefa -H 'Content-Type:application/json' -d '{
    "reference": "4bcdeac1-3aa7-4a7a-91a4-b5d3e40adefa",
    "type": "ColisEnCoursDAcheminement", 
    "email": "jdusse@maif.fr",
    "dateDEnvoi": "2021-11-08T11:59:09.933828",
    "position": {
        "latitude": 44,
        "longitude": 60
    },
    "adresse": {
        "type": "AdresseBtoC", 
        "ligne1": "Jean Claude Dusse", 
        "ligne4": "10 rue de la rue",
        "ligne6": "79000 Niort"
    }
}' --include
```


```bash 
curl -XPUT http://localhost:8080/api/colis/v2/4bcdeac1-3aa7-4a7a-91a4-b5d3e40adefa -H 'Content-Type:application/json' -d '{
  "reference": "4bcdeac1-3aa7-4a7a-91a4-b5d3e40adefa",
  "type": "ColisRecu",
  "dateDEnvoi": "2021-11-08T11:59:09.933828",
  "dateDeReception": "2021-11-08T14:40:00.000000",
  "email": "jdusse@maif.fr",
  "adresse": {
    "type": "AdresseBtoC",
    "ligne1": "Jean Claude Dusse",
    "ligne4": "10 RUE DE LA RUE",
    "ligne6": "79000 NIORT"
  }
}' --include
```
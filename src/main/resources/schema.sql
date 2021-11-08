CREATE TABLE IF NOT EXISTS colis
(
    "reference"      varchar(100) primary key,
    "type"           text,
    "date_envoi"     timestamp,
    "date_reception" timestamp,
    "latitude"       int8,
    "longitude"      int8,
    "email"          text,
    "adresse_type"   text,
    "ligne1"         text,
    "ligne2"         text,
    "ligne3"         text,
    "ligne4"         text,
    "ligne5"         text,
    "ligne6"         text,
    "ligne7"         text
);

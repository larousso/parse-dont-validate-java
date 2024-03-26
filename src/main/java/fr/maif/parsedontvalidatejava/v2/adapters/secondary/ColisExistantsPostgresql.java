package fr.maif.parsedontvalidatejava.v2.adapters.secondary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.maif.json.JsonWrite;
import fr.maif.parsedontvalidatejava.v2.domain.Colis;
import fr.maif.parsedontvalidatejava.v2.domain.ColisExistants;
import io.r2dbc.postgresql.codec.Json;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ColisExistantsPostgresql implements ColisExistants {

    private final DatabaseClient client;

    public ColisExistantsPostgresql(DatabaseClient client) {
        this.client = client;
    }


    @Override
    public Flux<Colis.ColisExistant> listerColis() {
        return client.sql("""
                        select c.*
                        from colis c 
                        """)
                .map((r, rm) -> ColisDatabase.rowReader().read(r))
                .all()
                .map(ColisDatabase::toColis);
    }

    @Override
    public Mono<Colis.ColisExistant> chercherColisExistantParReference(Colis.ReferenceColis referenceColis) {
        return client.sql("""
                    select c.*
                    from colis c 
                    where c.reference = :reference
                """)
                .bind("reference", referenceColis.value())
                .map((r, rm) -> ColisDatabase.rowReader().read(r))
                .one()
                .map(ColisDatabase::toColis);
    }

    @Override
    public Mono<Colis.ColisExistant> enregistrerColis(Colis.ColisPrisEnCharge colisPrisEnCharge) {
        var jsonString = fr.maif.json.Json.stringify(fr.maif.json.Json.toJson(ColisDatabase.fromColis(colisPrisEnCharge), JsonWrite.auto()));
        return client.sql("""
                        insert into colis select * from json_populate_record(null::colis, :colis::json)
                        returning * 
                """)
                .bind("colis", Json.of(jsonString))
                .map((r, rm) -> ColisDatabase.rowReader().read(r))
                .one()
                .map(ColisDatabase::toColis);
    }

    @Override
    public Mono<Colis.ColisExistant> mettreAJourColis(Colis.ColisExistant colisExistant) {
        var jsonString = fr.maif.json.Json.stringify(fr.maif.json.Json.toJson(ColisDatabase.fromColis(colisExistant), JsonWrite.auto()));
        return client.sql("""
                       update colis
                          set ("reference", 
                               "type",
                               "date_envoi",
                               "date_reception",
                               "latitude",
                               "longitude",
                               "email",
                               "adresse_type",
                               "ligne1",
                               "ligne2",
                               "ligne3",
                               "ligne4",
                               "ligne5",
                               "ligne6",
                               "ligne7") = (
                               select "reference",
                                       "type",
                                       "date_envoi",
                                       "date_reception",
                                       "latitude",
                                       "longitude",
                                       "email",
                                       "adresse_type",
                                       "ligne1",
                                       "ligne2",
                                       "ligne3",
                                       "ligne4",
                                       "ligne5",
                                       "ligne6",
                                       "ligne7" 
                               from json_populate_record(null::colis, :colis::json)
                           )
                           where "reference" = :reference 
                       returning * 
                        """)
                .bind("colis", Json.of(jsonString))
                .bind("reference", colisExistant.reference().value())
                .map((r, rm) -> ColisDatabase.rowReader().read(r))
                .one()
                .map(ColisDatabase::toColis);
    }
}

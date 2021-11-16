package fr.maif.parsedontvalidatejava.v2.adapters.primary;

import com.fasterxml.jackson.databind.JsonNode;
import fr.maif.json.Json;
import fr.maif.json.JsonWrite;
import fr.maif.parsedontvalidatejava.v2.adapters.serde.ColisJsonFormat;
import fr.maif.parsedontvalidatejava.v2.domain.LivraisonDeColis;
import fr.maif.parsedontvalidatejava.v2.domain.errors.ColisNonTrouve;
import fr.maif.parsedontvalidatejava.v2.domain.errors.EtatInvalide;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static fr.maif.json.Json.$$;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Component
public class ColisApi {

    private final LivraisonDeColis livraisonDeColis;

    public ColisApi(LivraisonDeColis livraisonDeColis) {
        this.livraisonDeColis = livraisonDeColis;
    }

    public Mono<ServerResponse> listerColis(ServerRequest serverRequest) {
        return ok()
                .body(this.livraisonDeColis.listerColis()
                                .map(colis -> Json.toJson(colis, ColisJsonFormat.colisFormat())),
                        JsonNode.class
                );
    }

    public Mono<ServerResponse> prendreEnChargeLeColis(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(JsonNode.class)
                .flatMap(json ->
                        Json.fromJson(json, ColisJsonFormat.nouveauColisFormat()).fold(
                                errors -> badRequest().bodyValue(Json.obj($$("errors", Json.arr(
                                        errors.map(err -> Json.toJson(err, JsonWrite.auto()))
                                )))),
                                nouveauColis -> this.livraisonDeColis.prendreEnChargeLeColis(nouveauColis)
                                        .flatMap(colis -> ok().bodyValue(Json.toJson(colis, ColisJsonFormat.colisFormat())))
                                        .onErrorResume(ColisNonTrouve.class, e -> notFound().build())
                        )
                );
    }

    public Mono<ServerResponse> gererLeColis(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(JsonNode.class)
                .flatMap(json ->
                        Json.fromJson(json, ColisJsonFormat.colisExistantFormat()).fold(
                                // Json invalide : bad request, on retourne la liste des erreurs au client
                                errors -> badRequest().bodyValue(Json.obj($$("errors", Json.arr(
                                        errors.map(err -> Json.toJson(err, JsonWrite.auto()))
                                )))),
                                // Ok : on traite le colis
                                nouveauColis -> this.livraisonDeColis.gererColis(nouveauColis)
                                        .flatMap(colis -> ok().bodyValue(Json.toJson(colis, ColisJsonFormat.colisFormat())))
                                        // Le colis n'existe pas : not found
                                        .onErrorResume(ColisNonTrouve.class, e -> notFound().build())
                                        // état incohérent : bad request
                                        .onErrorResume(EtatInvalide.class, e -> badRequest().bodyValue(
                                                Json.obj($$("errors", Json.arr(e.getMessage()))))
                                        ))
                );
    }

}

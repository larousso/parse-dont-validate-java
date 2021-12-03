package fr.maif.parsedontvalidatejava.v1.adapters.primary;

import com.fasterxml.jackson.databind.JsonNode;
import fr.maif.parsedontvalidatejava.v1.domain.LivraisonDeColis;
import fr.maif.parsedontvalidatejava.v1.domain.errors.ColisNonTrouve;
import fr.maif.parsedontvalidatejava.v1.domain.errors.EtatInvalide;
import fr.maif.parsedontvalidatejava.v1.domain.model.Colis;
import fr.maif.json.Json;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static fr.maif.json.Json.$$;

@RestController
@RequestMapping("/api/v1/colis")
public class ColisApiV1 {

    private final LivraisonDeColis livraisonDeColis;

    public ColisApiV1(LivraisonDeColis livraisonDeColis) {
        this.livraisonDeColis = livraisonDeColis;
    }

    @RequestMapping
    public Flux<Colis> listerColis() {
        return this.livraisonDeColis.listerColis();
    }

    @PostMapping
    public Mono<ResponseEntity<Colis>> prendreEnChargeLeColis(@RequestBody @Valid Colis colis) {
        return this.livraisonDeColis
                        .prendreEnChargeLeColis(colis)
                        .map(ResponseEntity::ok)
                        .onErrorResume(ColisNonTrouve.class, e ->
                                Mono.just(ResponseEntity.notFound().build())
                        );
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Object>> gererLeColis(@PathVariable("id") String id, @RequestBody @Valid Colis colis) {
        return this.livraisonDeColis.gererColis(colis)
                        .map(ResponseEntity::<Object>ok)
                        .onErrorResume(ColisNonTrouve.class, e -> Mono.just(ResponseEntity.notFound().build()))
                        .onErrorResume(EtatInvalide.class, e -> Mono.just(ResponseEntity.badRequest()
                                .body(Json.obj($$("errors", Json.arr(e.getMessage()))))
                        ));
    }
}

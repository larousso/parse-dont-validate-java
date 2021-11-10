package fr.maif.parsedontvalidatejava.v2.adapters.primary;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class ApiV2Router {

    @Bean
    public RouterFunction<ServerResponse> getStudentsRoute(ColisApi colisApi) {
        return route(GET("/api/v2/colis"), colisApi::listerColis)
                .andRoute(POST("/api/v2/colis"), colisApi::prendreEnChargeLeColis)
                .andRoute(PUT("/api/v2/colis/{id}"), colisApi::gererLeColis);
    }

}

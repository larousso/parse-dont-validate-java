package fr.maif.parsedontvalidatejava;

import fr.maif.parsedontvalidatejava.adapters.primary.ColisApi;
import io.r2dbc.spi.ConnectionFactory;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication
public class ParseDontValidateJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParseDontValidateJavaApplication.class, args);
	}

	@Bean
	public DSLContext jooqDslContext(ConnectionFactory connectionFactory) {
		return DSL.using(connectionFactory).dsl();
	}

	@Bean
	public RouterFunction<ServerResponse> getStudentsRoute(ColisApi colisApi) {
		return route(GET("/api/colis"), colisApi::listerColis)
				.andRoute(POST("/api/colis"), colisApi::prendreEnChargeLeColis)
				.andRoute(PUT("/api/colis/{id}"), colisApi::gererLeColis);
	}

	@Bean
	public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {

		ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
		initializer.setConnectionFactory(connectionFactory);

		CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
		populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
		initializer.setDatabasePopulator(populator);

		return initializer;
	}

}

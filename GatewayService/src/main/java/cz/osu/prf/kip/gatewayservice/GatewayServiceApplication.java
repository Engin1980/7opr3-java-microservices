package cz.osu.prf.kip.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayServiceApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("appuser", r -> r
						.path("/appuser/**")
						.filters(f -> f.stripPrefix(1))
						.uri("http://appuserservice:9200"))
				.route("applog", r -> r
						.path("/applog/**")
						.filters(f -> f.stripPrefix(1))
						.uri("http://applogservice:9100"))
				.route("auth", r -> r
						.path("/auth/**")
						.filters(f -> f.stripPrefix(1))
						.uri("http://authservice:9300"))
				.build();
	}

}


package site.lywq.nps;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import run.halo.app.core.extension.endpoint.CustomEndpoint;

/**
 * Photo configuration.
 *
 * @author LIlGG
 * @since 1.0.0
 */
@Configuration
public class NpsConfiguration {

    /**
     * Register custom endpoints.
     *
     * @param context application context.
     * @return router function.
     */
    @Bean
    RouterFunction<ServerResponse> customEndpoints(ApplicationContext context) {
        var builder = new CustomEndpointsBuilder();
        context.getBeansOfType(CustomEndpoint.class).values().forEach(
                builder::add);
        return builder.build();
    }
}

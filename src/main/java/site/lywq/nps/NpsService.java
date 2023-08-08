package site.lywq.nps;

import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author lywq
 */
public interface NpsService {

    Mono<ServerResponse> getNpsUser();

    Mono<ServerResponse> userRegister(NpsUser npsUser);

    Mono<ServerResponse> userLogin(NpsUser npsUser);

}

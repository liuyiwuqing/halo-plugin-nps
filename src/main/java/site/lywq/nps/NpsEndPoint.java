package site.lywq.nps;

import lombok.AllArgsConstructor;
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.endpoint.CustomEndpoint;
import run.halo.app.extension.GroupVersion;

/**
 * @author lywq
 */
@Component
@AllArgsConstructor
public class NpsEndPoint implements CustomEndpoint {

    private final NpsService npsService;

    @Override
    public RouterFunction<ServerResponse> endpoint() {
        final var tag = "nps.lywq.site/v1alpha1/Nps";
        return SpringdocRouteBuilder.route()
            .GET("plugins/PluginNps/getNpsUser", this::getNpsUser,
                builder -> builder.operationId("getNpsUser")
                    .description("Nps用户获取").tag(tag))
            .POST("plugins/PluginNps/userRegister", this::userRegister,
                builder -> builder.operationId("userRegister")
                    .description("Nps用户注册").tag(tag))
            .POST("plugins/PluginNps/userLogin", this::userLogin,
                builder -> builder.operationId("userLogin")
                    .description("Nps用户登录").tag(tag))
            // 这里可添加其他自定义 API
            .build();
    }

    @Override
    public GroupVersion groupVersion() {
        return GroupVersion.parseAPIVersion("nps.lywq.site/v1alpha1");
    }

    private NpsUser toNpsUser(ServerRequest request) {
        String userName = request.queryParam("userName").orElse("");
        String userPassword = request.queryParam("userPassword").orElse("");
        String registrationCode = request.queryParam("registrationCode").orElse("");
        return new NpsUser().setUserName(userName).setUserPassword(userPassword)
            .setRegistrationCode(registrationCode);
    }


    private Mono<ServerResponse> getNpsUser(ServerRequest serverRequest) {
        return npsService.getNpsUser();
    }

    private Mono<ServerResponse> userRegister(ServerRequest serverRequest) {
        NpsUser npsUser = toNpsUser(serverRequest);
        return npsService.userRegister(npsUser);
    }

    private Mono<ServerResponse> userLogin(ServerRequest serverRequest) {
        NpsUser npsUser = toNpsUser(serverRequest);
        return npsService.userLogin(npsUser);
    }


}

package site.lywq.nps;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Strings;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.User;
import run.halo.app.extension.Metadata;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.infra.utils.JsonUtils;

/**
 * @author lywq
 */
@Slf4j
@Component
public class NpsServiceImpl implements NpsService {

    /**
     * url类型
     */
    private enum UrlType {
        REGISTER, LOGIN
    }

    public enum StatusEnum {
        SUCCESS(0), FAILURE(1);

        private final int value;

        @JsonCreator
        StatusEnum(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return value;
        }
    }

    private final ReactiveSettingFetcher settingFetcher;
    private final ReactiveExtensionClient client;


    private static final String CHECK_REGISTRATION_CODE_URL =
        "https://lywq.muyin.site/api/content/registrationCode/check/";

    public NpsServiceImpl(ReactiveSettingFetcher settingFetcher, ReactiveExtensionClient client) {
        this.settingFetcher = settingFetcher;
        this.client = client;
    }


    /**
     * 创建nps用户
     *
     * @param npsUser
     * @return reactor.core.publisher.Mono<site.lywq.nps.NpsUser>
     * @author lywq
     * @date 2023/04/12 23:36
     **/
    private Mono<NpsUser> createNpsUser(NpsUser npsUser) {
        Metadata metadata = new Metadata();
        metadata.setName(npsUser.getUserName());
        metadata.setCreationTimestamp(Instant.now());
        npsUser.setMetadata(metadata);
        return client.create(npsUser);
    }


    /**
     * 注册用户
     *
     * @param npsUser
     * @return reactor.core.publisher.Mono<org.springframework.web.reactive.function.server.ServerResponse>
     * @author lywq
     * @date 2023/04/12 23:36
     **/
    @Override
    public Mono<ServerResponse> userRegister(NpsUser npsUser) {
        return buildUrl(UrlType.REGISTER, npsUser).flatMap(
            url -> {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();
                    HttpResponse<String> response = HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() == 200) {
                        NpsResults npsResults =
                            JsonUtils.jsonToObject(response.body(), NpsResults.class);

                        int status = npsResults.getStatus();
                        String msg = npsResults.getMsg();
                        if (1 == status && "register success".equals(msg)) {
                            Mono<NpsUser> npsUserMono = createNpsUser(npsUser);
                            return npsUserMono.flatMap(this::processSuccess);
                        } else {
                            return processFailure(null, msg);
                        }
                    } else {
                        return processFailure(null, "注册失败，请联系管理员");
                    }

                } catch (Exception e) {
                    return processFailure(null, e.getMessage());
                }
            }
        ).onErrorResume(throwable -> processFailure(null, throwable.getMessage()));
    }


    /**
     * 获取nps用户
     *
     * @return reactor.core.publisher.Mono<org.springframework.web.reactive.function.server.ServerResponse>
     * @author lywq
     * @date 2023/04/12 23:37
     **/
    @Override
    public Mono<ServerResponse> getNpsUser() {
        return getContextUser().flatMap(user -> {
            Mono<NpsUser> npsUserMono = client.get(NpsUser.class, user.getMetadata().getName());
            return npsUserMono.map(userInfo -> {
                    NpsUser npsUser = new NpsUser();
                    npsUser.setUserName(user.getMetadata().getName());
                    npsUser.setUserPassword(userInfo.getUserPassword());
                    return npsUser;
                }).onErrorReturn(new NpsUser().setUserName(user.getMetadata().getName()))
                .defaultIfEmpty(new NpsUser().setUserName(user.getMetadata().getName()));
        }).flatMap(npsUser -> {
            if (ObjectUtils.isEmpty(npsUser)) {
                return this.processFailure(npsUser, "用户不存在");
            } else if (Strings.isNullOrEmpty(npsUser.getUserPassword())) {
                return getNpsConfig().map(npsConfig -> {
                    npsUser.setRegistrationCodeUrl(npsConfig.getRegistrationCodeUrl());
                    return npsUser;
                }).flatMap(npsUserNew -> this.processFailure(npsUserNew, "暂未注册"));
            } else {
                return this.processSuccess(npsUser);
            }
        });
    }


    /**
     * 用户登录
     *
     * @param npsUser
     * @return reactor.core.publisher.Mono<org.springframework.web.reactive.function.server.ServerResponse>
     * @author lywq
     * @date 2023/04/12 23:37
     **/
    @Override
    public Mono<ServerResponse> userLogin(NpsUser npsUser) {
        return buildUrl(UrlType.LOGIN, npsUser).flatMap(this::processSuccess);
    }


    /**
     * 获取Nps配置
     *
     * @return reactor.core.publisher.Mono<site.lywq.nps.NpsConfig>
     * @author lywq
     * @date 2023/04/13 00:58
     **/
    private Mono<NpsConfig> getNpsConfig() {
        return settingFetcher.fetch(NpsConfig.CONFIG_MAP_NAME, NpsConfig.GROUP, NpsConfig.class)
            .switchIfEmpty(Mono.just(new NpsConfig()));
    }


    /**
     * 组装地址
     *
     * @param urlType
     * @param npsUser
     * @return java.net.URI
     * @author lywq
     * @date 2023/04/11 20:37
     **/
    private Mono<String> buildUrl(UrlType urlType, NpsUser npsUser) {
        if (StringUtils.isEmpty(npsUser.getUserName())) {
            return Mono.error(
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "用户名不能为空"));
        } else if (StringUtils.isEmpty(npsUser.getUserPassword())) {
            return Mono.error(
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "用户密码不能为空"));
        }
        if (UrlType.REGISTER == urlType) {
            if (StringUtils.isEmpty(npsUser.getRegistrationCode())) {
                return Mono.error(
                    new ResponseStatusException(HttpStatus.BAD_REQUEST, "注册码不能为空"));
            } else {
                boolean checked = checkRegistrationCode(npsUser.getRegistrationCode());
                if (!checked) {
                    return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "注册码不正确，请重新获取"));
                }
            }
        }
        return getNpsConfig().map(npsConfig -> {
            String url = "";
            switch (urlType) {
                case REGISTER -> url =
                    npsConfig.getNpsUserRegisterUrl() + "?username=" + npsUser.getUserName()
                        + "&password=" + npsUser.getUserPassword();
                case LOGIN -> {
                    String params = npsUser.getUserName() + "-" + npsUser.getUserPassword();
                    url = npsConfig.getNpsUserLoginUrl() + "?key=" + Base64.getEncoder()
                        .encodeToString(params.getBytes());
                }
            }
            return url;
        });
    }


    /**
     * 处理结果
     *
     * @return reactor.core.publisher.Mono<org.springframework.web.reactive.function.server.ServerResponse>
     * @author lywq
     * @date 2023/04/13 00:58
     **/
    private Mono<ServerResponse> processSuccess(Object data) {
        return process(StatusEnum.SUCCESS, data, "success");
    }

    public Mono<ServerResponse> processFailure(Object data, String msg) {
        if (Strings.isNullOrEmpty(msg)) {
            msg = "failure";
        }
        return process(StatusEnum.FAILURE, data, msg);
    }

    private Mono<ServerResponse> process(StatusEnum status, Object data, String msg) {
        NpsResults npsResults = new NpsResults();
        npsResults.setStatus(status.value).setData(data).setMsg(msg);
        return ServerResponse.ok().bodyValue(npsResults);
    }


    protected Mono<User> getContextUser() {
        return ReactiveSecurityContextHolder.getContext().flatMap(ctx -> {
            var name = ctx.getAuthentication().getName();
            return client.fetch(User.class, name);
        });
    }


    /**
     * 检查注册码是否正确
     *
     * @param registrationCode
     * @return boolean
     * @author lywq
     * @date 2023/06/12 20:58
     **/
    private boolean checkRegistrationCode(String registrationCode) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CHECK_REGISTRATION_CODE_URL + registrationCode))
                .header("API-Authorization", "lywq1225")
                .GET()
                .build();

            HttpResponse<String> response =
                HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                RegistrationCodeResults registrationCodeResults =
                    JsonUtils.jsonToObject(response.body(), RegistrationCodeResults.class);
                return (boolean) registrationCodeResults.getData();
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

}

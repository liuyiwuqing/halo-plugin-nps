package site.lywq.nps;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author lywq
 */
@Data
@Accessors(chain = true)
public class NpsConfig {

    public static final String CONFIG_MAP_NAME = "plugin-nps-config";
    public static final String GROUP = "basic";

    private boolean enable = Boolean.FALSE;

    /**
     * nps 用户注册 url
     */
    private String npsUserRegisterUrl;

    /**
     * nps 用户登录 url
     */
    private String npsUserLoginUrl;

    /**
     * 获取注册码  url
     */
    private String registrationCodeUrl;
}

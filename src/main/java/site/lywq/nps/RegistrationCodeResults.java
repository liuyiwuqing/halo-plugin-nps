package site.lywq.nps;

import lombok.Data;
import lombok.experimental.Accessors;


/**
 * 注册码请求结果
 *
 * @author lywq
 */
@Data
@Accessors(chain = true)
public class RegistrationCodeResults {
    private int status;

    private String message;

    private String devMessage;

    private Object data;
}

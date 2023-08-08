package site.lywq.nps;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Nps注册请求结果
 *
 * @author lywq
 */
@Data
@Accessors(chain = true)
public class NpsResults {

    private int status;

    private Object data;

    private String msg;
}

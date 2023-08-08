package site.lywq.template;

import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.BasePlugin;

/**
 * 插件开发模板
 *
 * @author lywq
 * @date 2023/08/08 09:49
 **/
@Component
public class TemplatePlugin extends BasePlugin {

    public TemplatePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        System.out.println("插件启动成功！");
    }

    @Override
    public void stop() {
        System.out.println("插件停止！");
    }
}
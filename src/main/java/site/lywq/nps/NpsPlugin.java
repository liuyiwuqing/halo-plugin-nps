package site.lywq.nps;

import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import run.halo.app.extension.SchemeManager;
import run.halo.app.plugin.BasePlugin;


/**
 * Nps用户注册插件
 *
 * @author lywq
 */
@Component
public class NpsPlugin extends BasePlugin {

    private final SchemeManager schemeManager;

    public NpsPlugin(PluginWrapper wrapper, SchemeManager schemeManager) {
        super(wrapper);
        this.schemeManager = schemeManager;
    }

    @Override
    public void start() {
        schemeManager.register(NpsUser.class);
    }

    @Override
    public void stop() {
        schemeManager.unregister(schemeManager.get(NpsUser.class));
    }
}

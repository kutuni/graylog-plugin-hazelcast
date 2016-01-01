package kutuni.graylog.plugin;

import org.graylog2.plugin.PluginModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HCInputModule extends PluginModule {

    private static final Logger LOG = LoggerFactory.getLogger(HCInputModule.class.getName());

    @Override
    protected void configure() {
        installTransport(transportMapBinder(),"HC-Transport",HCTransport.class);
        installInput(inputsMapBinder(), HCInput.class, HCInput.Factory.class);
        LOG.info("Configuration Initialized");
    }
}

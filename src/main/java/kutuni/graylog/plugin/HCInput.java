package kutuni.graylog.plugin;

import javax.inject.Inject;

import org.graylog2.inputs.codecs.GelfCodec;
import org.graylog2.plugin.LocalMetricRegistry;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.inputs.MessageInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class HCInput extends MessageInput {

    private static final Logger LOG = LoggerFactory.getLogger(HCInput.class.getName());

    private static final String NAME = "HC Logger";

    @AssistedInject
    public HCInput(MetricRegistry metricRegistry, @Assisted Configuration configuration,
                            HCTransport.Factory factory, LocalMetricRegistry localRegistry,
                            GelfCodec.Factory codecFactory,
                            Config config, Descriptor descriptor, ServerStatus serverStatus) {
        super(metricRegistry, configuration, factory.create(configuration),
                localRegistry, codecFactory.create(configuration), config, descriptor, serverStatus);
        LOG.debug("Message Input Initialized");
    }

    public interface Factory extends MessageInput.Factory<HCInput> {
        @Override
        HCInput create(Configuration configuration);

        @Override
        Config getConfig();

        @Override
        Descriptor getDescriptor();
    }

    public static class Descriptor extends MessageInput.Descriptor {
        @Inject
        public Descriptor() {
            super(NAME, false, "");
        }
    }

    public static class Config extends MessageInput.Config {
        @Inject
        public Config(HCTransport.Factory transport, GelfCodec.Factory codec) {
            super(transport.getConfig(), codec.getConfig());
        }
    }
}

package kutuni.graylog.plugin;

import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.Version;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

public class HCInputMetaData implements PluginMetaData {
    @Override
    public String getUniqueId() {
        return "org.kutuni.plugin.hazelcast.HCInputPlugin";
    }

    @Override
    public String getName() {
        return "Hazelcast Cluster Logger Plugin ";
    }

    @Override
    public String getAuthor() {
        return "Dervis Mahmutoglu";
    }

    @Override
    public URI getURL() {
        return URI.create("https://www.graylog.org/");
    }

    @Override
    public Version getVersion() {
        return new Version(1, 0, 2);
    }

    @Override
    public String getDescription() {
        return "HC Cluster Logger Plugin";
    }

    @Override
    public Version getRequiredVersion() {
        return new Version(1, 0, 0);
    }

    @Override
    public Set<ServerStatus.Capability> getRequiredCapabilities() {
        return Collections.emptySet();
    }
}

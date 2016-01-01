package kutuni.graylog.plugin;

import java.util.Arrays;
import java.util.Collection;

import org.graylog2.plugin.Plugin;
import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.PluginModule;

public class HCInputPlugin implements Plugin {
	@Override
	public PluginMetaData metadata() {
		return new HCInputMetaData();
	}

	@Override
	public Collection<PluginModule> modules() {
		return Arrays.<PluginModule> asList(new HCInputModule());
	}
}

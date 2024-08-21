package cost_driver;

import de.hpi.bpt.scylla.exception.ScyllaValidationException;
import de.hpi.bpt.scylla.model.global.GlobalConfiguration;
import de.hpi.bpt.scylla.plugin_type.parser.GlobalConfigurationParserPluggable;
import org.jdom2.Element;

import java.util.Map;


public class CostDriverGCParserPlugin extends GlobalConfigurationParserPluggable {
    @Override
    public String getName() {
        return CostDriverPluginUtils.PLUGIN_NAME;
    }

    @Override
    public Map<String, Object> parse(GlobalConfiguration simulationInput, Element sim)
            throws ScyllaValidationException {

        return Map.of();
    }
}

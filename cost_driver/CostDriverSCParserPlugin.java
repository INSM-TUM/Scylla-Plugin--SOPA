package cost_driver;

import de.hpi.bpt.scylla.exception.ScyllaValidationException;
import de.hpi.bpt.scylla.model.configuration.SimulationConfiguration;
import de.hpi.bpt.scylla.plugin_type.parser.SimulationConfigurationParserPluggable;
import org.jdom2.Element;

import java.util.Map;

public class CostDriverSCParserPlugin extends SimulationConfigurationParserPluggable {

    @Override
    public String getName() {
        return CostDriverPluginUtils.PLUGIN_NAME;
    }

    @Override
    public Map<String, Object> parse(SimulationConfiguration simulationInput, Element sim)
            throws ScyllaValidationException {

        return Map.of();
    }

}

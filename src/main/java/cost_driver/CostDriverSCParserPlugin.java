package cost_driver;

import java.util.*;
import de.hpi.bpt.scylla.exception.ScyllaValidationException;
import de.hpi.bpt.scylla.logger.DebugLogger;
import de.hpi.bpt.scylla.model.configuration.SimulationConfiguration;
import de.hpi.bpt.scylla.plugin_type.parser.SimulationConfigurationParserPluggable;
import org.jdom2.Element;
import org.jdom2.Namespace;

public class CostDriverSCParserPlugin extends SimulationConfigurationParserPluggable {

    @Override
    public String getName() {
        return CostDriverPluginUtils.PLUGIN_NAME;
    }

    @Override
    public Map<String, Object> parse(SimulationConfiguration simulationInput, Element sim)
            throws ScyllaValidationException {

        Map<Integer, String> costDrivers = new HashMap<>();

        Namespace bsimNamespace = sim.getNamespace();
        List<Element> elements = sim.getChildren().stream().filter(c -> c.getChild("costDrivers", bsimNamespace) != null).toList();

        for (Element el : elements) {
            String identifier = el.getAttributeValue("id");
            if (identifier == null) {
                DebugLogger.log("Warning: Simulation configuration definition element '" + identifier
                        + "' does not have an identifier, skip.");
                continue; // no matching element in process, so skip definition
            }

            Integer nodeId = simulationInput.getProcessModel().getIdentifiersToNodeIds().get(identifier);
            if (nodeId == null) {
                DebugLogger.log("Simulation configuration definition for process element '" + identifier
                        + "', but not available in process, skip.");
                continue; // no matching element in process, so skip definition
            }

            String costDriver = el.getChild("costDrivers", bsimNamespace).getChild("costDriver", bsimNamespace).getAttributeValue("id");
            costDrivers.put(nodeId, costDriver);

        }

        HashMap<String, Object> extensionAttributes = new HashMap<>();
        extensionAttributes.put("costDrivers", costDrivers);

        return extensionAttributes;
    }

}

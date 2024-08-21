package cost_driver;

import java.util.*;
import java.util.concurrent.TimeUnit;

import de.hpi.bpt.scylla.exception.ScyllaValidationException;
import de.hpi.bpt.scylla.model.configuration.distribution.*;
import de.hpi.bpt.scylla.model.global.GlobalConfiguration;
import de.hpi.bpt.scylla.plugin_type.parser.GlobalConfigurationParserPluggable;
import org.jdom2.Element;
import org.jdom2.Namespace;
import static de.hpi.bpt.scylla.parser.SimulationConfigurationParser.getTimeDistributionWrapper;

//This is an example Global Configuration Parser of "hiring_process_global"

public class CostDriverGCParserPlugin extends GlobalConfigurationParserPluggable {
    @Override
    public String getName() {
        return CostDriverPluginUtils.PLUGIN_NAME;
    }

    @Override
    public Map<String, Object> parse(GlobalConfiguration simulationInput, Element sim)
            throws ScyllaValidationException {

        Namespace bsimNamespace = sim.getNamespace();
        Element costDrivers = sim.getChildren("costDriver", bsimNamespace).get(0);

        List<CostDriver> abstractCostDrivers = new ArrayList<>();
        for (Element el: costDrivers.getChildren()) { //parse abstract cost drivers
            String id = el.getAttributeValue("id");
            TimeUnit timeUnit = TimeUnit.valueOf(el.getAttributeValue("defaultTimeUnit").toUpperCase());

            AbstractCostDriver abstractCostDriver = new AbstractCostDriver(id, new ArrayList<>(), timeUnit);

            for (Element child: el.getChildren()) { //parse concrete cost drivers
                String chileId = child.getAttributeValue("id");
                Double LCAScore = Double.valueOf(child.getAttributeValue("LCAScores"));
                Double prob = Double.valueOf(child.getChild("probability", bsimNamespace).getText());
                Element durationElem = child.getChild("duration", bsimNamespace);
                TimeDistributionWrapper duration = null;
                if (durationElem != null) {
                    duration = getTimeDistributionWrapper(durationElem,bsimNamespace);
                }
                ConcreteCostDriver costDriver = new ConcreteCostDriver(chileId, abstractCostDriver, prob, LCAScore,duration);
                abstractCostDriver.addChild(costDriver);
            }
            abstractCostDrivers.add(abstractCostDriver);
        }
        HashMap<String, Object> extensionAttributes = new HashMap<>();
        extensionAttributes.put("costDrivers", abstractCostDrivers);

        return extensionAttributes;
    }
}

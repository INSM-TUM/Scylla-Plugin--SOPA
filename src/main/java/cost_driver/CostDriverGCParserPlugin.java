package cost_driver;

import de.hpi.bpt.scylla.exception.ScyllaValidationException;
import de.hpi.bpt.scylla.model.global.GlobalConfiguration;
import de.hpi.bpt.scylla.plugin_type.parser.GlobalConfigurationParserPluggable;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.*;
import java.util.concurrent.TimeUnit;


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
    /*
    @Override
    public Map<String, Object> parse(GlobalConfiguration simulationInput, Element sim)
            throws ScyllaValidationException {

        Namespace bsimNamespace = sim.getNamespace();
        Element costDrivers = sim.getChildren("costDriver", bsimNamespace).get(0);

        List<costDriver> abstractCostDrivers = new ArrayList<>();
        for (Element el: costDrivers.getChildren()) {
            String id = el.getAttributeValue("id");
            TimeUnit timeUnit = TimeUnit.valueOf(el.getAttributeValue("defaultTimeUnit").toUpperCase());

            AbstractCostDriver abstractCostDriver = new AbstractCostDriver(id, new ArrayList<>(), timeUnit);

            for (Element child: el.getChildren()) {
                String chileId = child.getAttributeValue("id");
                Double LCAScore = Double.valueOf(child.getAttributeValue("LCAScores"));
                Element text  = child.getChild("probability", bsimNamespace);
                String text1 = child.getChildren().get(0).getText();
                Double prob = Double.valueOf(child.getChild("probability").getText());

                ConcreteCostDriver costDriver = new ConcreteCostDriver(chileId, abstractCostDriver, prob, LCAScore,null);
                abstractCostDriver.addChild(costDriver);
            }
            abstractCostDrivers.add(abstractCostDriver);
        }
        HashMap<String, Object> extensionAttributes = new HashMap<String, Object>();
        extensionAttributes.put("costDrivers", abstractCostDrivers);

        return extensionAttributes;
    }



    public static void main(String[] args) throws ScyllaValidationException {

        CostDriverGCParserPlugin costDriverGCParserPlugin = new CostDriverGCParserPlugin();

        Resource resource = new DynamicResource("exp001", "Department", 2, 10, TimeUnit.HOURS);
        GlobalConfiguration simulationInput = new GlobalConfiguration("hiring_process_global",
                ZoneId.of("UTC"), null,
                new HashMap<String, Resource>(){{put(resource.getId(), resource);}}, null);


        Element cc1 = new Element("concreteCostDriver", "http://www.hpi.de");
        cc1.setAttribute("id", "paper");
        cc1.setAttribute("LCAScores", "0.0000289");
        Element cc1prob = new Element("probability");
        cc1prob.setText("0.9");
        cc1.setContent(List.of(cc1prob));

        Element cc2 = new Element("concreteCostDriver", "http://www.hpi.de");
        cc2.setAttribute("id", "digital");
        cc2.setAttribute("LCAScores", "0.0000195");
        Element cc2prob = new Element("probability");
        cc2prob.setText("0.1");
        cc2.setContent(List.of(cc2prob));

        Element c1 = new Element("abstractCostDriver", "http://www.hpi.de");
        c1.setAttribute("id", "Request for job advertisement");
        c1.setAttribute("defaultTimeUnit", "Hours");
        c1.setContent(List.of(cc1, cc2));

        Element element = new Element("hiring_process_global", "http://www.hpi.de");
        Element child01 = new Element("zoneOffset", "http://www.hpi.de");
        Element child02 = new Element("timetables", "http://www.hpi.de");
        Element chile03 = new Element("resourceData", "http://www.hpi.de");
        Element child04 = new Element("costDriver", "http://www.hpi.de");
        child04.setContent(List.of(c1));
        element.setContent(List.of(child01, child02, chile03, child04));

        Map<String, Object> exmp = costDriverGCParserPlugin.parse(simulationInput, element);
    }
    */
}

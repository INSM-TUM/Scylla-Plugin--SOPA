package cost_driver;

import cost_driver.CostDriverGCParserPlugin;
import cost_driver.SimulationTest;
import de.hpi.bpt.scylla.exception.ScyllaValidationException;
import de.hpi.bpt.scylla.model.configuration.SimulationConfiguration;
import org.jdom2.JDOMException;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Random;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.junit.jupiter.api.Test;


import de.hpi.bpt.scylla.exception.ScyllaValidationException;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class GCParserTest extends SimulationTest {
    public static void main(String[] args) {
        try {
            new SCParserTest().testSCIsParsed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testGCIsParsed() throws ScyllaValidationException, JDOMException, IOException {
        createSimpleSimulationManager(
                "logistics_global.xml",
                "logistics_model_no_drivers.bpmn",
                "logistics_sim.xml");
        simulationManager._parseInput();
        assertNotNull(getGlobalConfiguration());
    }
    @Test
    public void testGCProcessIDParsed() throws ScyllaValidationException, JDOMException, IOException {
        createSimpleSimulationManager(
                "logistics_global.xml",
                "logistics_model_no_drivers.bpmn",
                "logistics_sim.xml");
        simulationManager._parseInput();
        assertEquals("Process_0vv8a1n", getProcessId());
    }



    private Element processRoot() {
        return processRoots.get("Process_1");
    }

    private Element properties() {
        Namespace nsp = processRoot().getNamespace();
        return processRoot()
                .getChild("subProcess", nsp)
                .getChild("extensionElements", nsp)
                .getChildren().iterator().next();
    }

    private Element getProperty(String name) {
        return properties().getChildren().stream().filter(each -> each.getAttributeValue("name").equals(name)).findAny().get();
    }

    private void setProperty(String name, Object value) {
        getProperty(name).setAttribute("value", value.toString());
    }

    private Element prepareActivationRule() {
        Element activationRuleRoot = getProperty("activationRule");
        Element ruleElement = activationRuleRoot.getChildren().iterator().next();
        ruleElement.getAttributes().clear();
        return ruleElement;
    }

    private Element prepareGroupingCharacteristic() {
        Element groupingCharacteristic = getProperty("groupingCharacteristic");
        Element child = new Element("property", groupingCharacteristic.getNamespace());
        groupingCharacteristic.addContent(child);
        return child;
    }

    private void before(Runnable r) {
        beforeParsingModels.computeIfAbsent("Process_1",s -> new ArrayList<>()).add(r);
    }

    @Override
    protected String getFolderName() {
        return null;
    }

}
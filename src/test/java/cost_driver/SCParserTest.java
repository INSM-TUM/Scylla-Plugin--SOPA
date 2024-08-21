package cost_driver;

import de.hpi.bpt.scylla.exception.ScyllaValidationException;
import org.jdom2.JDOMException;

import java.io.IOException;
import java.util.ArrayList;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SCParserTest extends SimulationTest{


    public static void main(String[] args) {
        try {
            new SCParserTest().testSCIsParsed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //Test if the SC is even parsed at all.
    @Test
    public void testSCIsParsed() throws ScyllaValidationException, JDOMException, IOException {
        createSimpleSimulationManager(
                "logistics_global.xml",
                "logistics_model_no_drivers.bpmn",
                "logistics_sim.xml");
        simulationManager._parseInput();
        assertNotNull(getSimulationConfiguration());
    }

    //Test to see if process id has parsed.
    @Test
    public void testSCProcessIDParsed() throws ScyllaValidationException, JDOMException, IOException {
        createSimpleSimulationManager(
                "logistics_global.xml",
                "logistics_model_no_drivers.bpmn",
                "logistics_sim.xml");
        simulationManager._parseInput();
        assertEquals("Process_0vv8a1n", getProcessId());
    }
    //Aggregate score test

    //matching Cost driver id to correct cost test

//
//    @ParameterizedTest
//    @EnumSource(BatchClusterExecutionType.class)
//    public void testExecutionTypeParsing(BatchClusterExecutionType executionType) throws ScyllaValidationException, JDOMException, IOException {
//        before(() -> setProperty("executionType", executionType.elementName));
//        createSimpleSimulationManager(
//                "logistics_global.xml",
//                "logistics_model_no_drivers.bpmn",
//                "logistics_sim.xml");
//        simulationManager._parseInput();
//        assertTrue(getBatchActivity().getExecutionType().equals(executionType));
//    }
//
//    @Test
//    public void testWrongExecutionTypeParsingThrowsError() {
//        before(() -> setProperty("executionType", "notAValidExecutionType"));
//        createSimpleSimulationManager(
//                "logistics_global.xml",
//                "logistics_model_no_drivers.bpmn",
//                "logistics_sim.xml");
//        assertThrows(ScyllaValidationException.class, () ->	simulationManager._parseInput());
//    }
//
//    @ParameterizedTest
//    @CsvSource({"4, PT40M"})
//    public void testParseThresholdRuleWithTimeOut(String threshold, String timeout) throws ScyllaValidationException, JDOMException, IOException {
//        before(() -> {
//            Element activationRule = prepareActivationRule();
//            activationRule.setAttribute("name", "thresholdRule");
//            activationRule.setAttribute("threshold", threshold);
//            activationRule.setAttribute("timeout", timeout.toString());
//        });
//        createSimpleSimulationManager(
//                "logistics_global.xml",
//                "logistics_model_no_drivers.bpmn",
//                "logistics_sim.xml");
//        simulationManager._parseInput();
//        assertAttribute(getBatchActivity().getActivationRule(), "threshold", Integer.parseInt(threshold));
//        assertAttribute(getBatchActivity().getActivationRule(), "timeOut", Duration.parse(timeout));
//    }
//
//    @ParameterizedTest
//    @CsvSource({"4, DataObjectX.DateY"})
//    public void testParseThresholdRuleWithDueDate(String threshold, String dueDate) throws ScyllaValidationException, JDOMException, IOException {
//        before(() -> {
//            Element activationRule = prepareActivationRule();
//            activationRule.setAttribute("name", "thresholdRule");
//            activationRule.setAttribute("threshold", threshold);
//            activationRule.setAttribute("duedate", dueDate);
//        });
//        createSimpleSimulationManager(
//                "logistics_global.xml",
//                "logistics_model_no_drivers.bpmn",
//                "logistics_sim.xml");
//        simulationManager._parseInput();
//        assertAttribute(getBatchActivity().getActivationRule(), "threshold", Integer.parseInt(threshold));
//        assertAttribute(getBatchActivity().getActivationRule(), "dueDate", dueDate);
//    }
//
//    @ParameterizedTest
//    @CsvSource({"4, DataObjectX.DateY"})
//    public void testParseThresholdRuleWithoutThrowsError() throws ScyllaValidationException, JDOMException, IOException {
//        before(() -> {
//            Element activationRule = prepareActivationRule();
//            activationRule.setAttribute("name", "thresholdRule");
//            activationRule.setAttribute("threshold", "4");
//        });
//        createSimpleSimulationManager(
//                "logistics_global.xml",
//                "logistics_model_no_drivers.bpmn",
//                "logistics_sim.xml");
//        assertThrows(ScyllaValidationException.class, () ->	simulationManager._parseInput());
//    }
//
//    @ParameterizedTest
//    @CsvSource({"1, PT10M, 2, PT30M"})
//    public void testParseMinMaxRule(Integer minInstances, String minTimeout, Integer maxInstances, String maxTimeout) throws ScyllaValidationException, JDOMException, IOException {
//        before(() -> {
//            Element activationRule = prepareActivationRule();
//            activationRule.setAttribute("name", "minMaxRule");
//            activationRule.setAttribute("minInstances", minInstances.toString());
//            activationRule.setAttribute("minTimeout", minTimeout);
//            activationRule.setAttribute("maxInstances", maxInstances.toString());
//            activationRule.setAttribute("maxTimeout", maxTimeout);
//        });
//        createSimpleSimulationManager(
//                "logistics_global.xml",
//                "logistics_model_no_drivers.bpmn",
//                "logistics_sim.xml");
//        simulationManager._parseInput();
//        assertAttribute(getBatchActivity().getActivationRule(), "minInstances", minInstances);
//        assertAttribute(getBatchActivity().getActivationRule(), "minTimeout", Duration.parse(minTimeout));
//        assertAttribute(getBatchActivity().getActivationRule(), "maxInstances", maxInstances);
//        assertAttribute(getBatchActivity().getActivationRule(), "maxTimeout", Duration.parse(maxTimeout));
//    }
//
//    @Test
//    public void testWrongActivationRuleParsingThrowsError() {
//        before(() -> getProperty("activationRule").getChildren().iterator().next().setAttribute("name", "notAValidActivationRule"));
//        createSimpleSimulationManager(
//                "logistics_global.xml",
//                "logistics_model_no_drivers.bpmn",
//                "logistics_sim.xml");
//        assertThrows(ScyllaValidationException.class, () ->	simulationManager._parseInput());
//    }
//
//    @Test
//    public void testTooManyActivationRulesParsingThrowsError() {
//        before(() -> getProperty("activationRule").addContent(new Element("property",properties().getNamespace())));
//        createSimpleSimulationManager(
//                "logistics_global.xml",
//                "logistics_model_no_drivers.bpmn",
//                "logistics_sim.xml");
//        assertThrows(ScyllaValidationException.class, () ->	simulationManager._parseInput());
//    }
//
//    @Test
//    public void testDefaultActivationRule() throws ScyllaValidationException, JDOMException, IOException {
//        before(() -> getProperty("activationRule").getContent().clear());
//        createSimpleSimulationManager(
//                "logistics_global.xml",
//                "logistics_model_no_drivers.bpmn",
//                "logistics_sim.xml");
//        simulationManager._parseInput();
//        assertNotNull(getBatchActivity().getActivationRule());
//    }
//
//    @Test
//    public void testParseGroupingCharacteristic() throws ScyllaValidationException, JDOMException, IOException {
//        BatchGroupingCharacteristic characteristic = new BatchGroupingCharacteristic("DataObjectX.ValueY");
//        before(() -> prepareGroupingCharacteristic().setAttribute("name", "processVariable").setAttribute("value", characteristic.getDataViewElement()));
//        createSimpleSimulationManager(
//                "logistics_global.xml",
//                "logistics_model_no_drivers.bpmn",
//                "logistics_sim.xml");
//        simulationManager._parseInput();
//        assertTrue(getBatchActivity().getGroupingCharacteristic().contains(characteristic));
//    }
//
//    @Test
//    public void testParseNoBatchRegion() throws ScyllaValidationException, JDOMException, IOException {
//        before(() -> properties().getChildren().clear());
//        createSimpleSimulationManager(
//                "logistics_global.xml",
//                "logistics_model_no_drivers.bpmn",
//                "logistics_sim.xml");
//        simulationManager._parseInput();
//        assertTrue(BatchPluginUtils.getBatchActivities(getProcessModel()).isEmpty());
//    }
//
//
//
//




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

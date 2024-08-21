package cost_driver;

import de.hpi.bpt.scylla.exception.ScyllaValidationException;
import de.hpi.bpt.scylla.model.global.GlobalConfiguration;
import de.hpi.bpt.scylla.model.global.resource.Resource;
import de.hpi.bpt.scylla.plugin_type.parser.EventOrderType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static cost_driver.TestUtils.GLOBAL_CONFIGURATION;
import static de.hpi.bpt.scylla.Scylla.normalizePath;
import static org.junit.jupiter.api.Assertions.fail;

class GCParserTest {

    @Test
    void testParse() throws ScyllaValidationException, IOException, JDOMException {
        CostDriverGCParserPlugin testSubject = new CostDriverGCParserPlugin();

        // Create the objects for the testGlobalConfig
        String testID = "logistics_global";
        ZoneId testZoneId = ZoneId.systemDefault();
        Long testNotRandomSeed = -5144667088361437244L;
        Map<String, Resource> testResources = new HashMap<>();
        List<EventOrderType> testEventOrderTypes = new LinkedList<>();
        // Create a testGlobalConfig
        GlobalConfiguration testGlobalConfig = new GlobalConfiguration(testID, testZoneId, testNotRandomSeed, testResources, testEventOrderTypes);

        // Preparing the parse()
        SAXBuilder builder = new SAXBuilder();
        Document gcDoc = builder.build(normalizePath("src/test/resources/Shipping/" + GLOBAL_CONFIGURATION));
        Element gcRootElement = gcDoc.getRootElement();

        // Actual GlobalConfiguration Cost Drivers
        var actualGC = testSubject.parse(testGlobalConfig, gcRootElement);

        // Expected
        var expectedGC = initialiseDriver();
        var expectedGCParents = expectedGC.stream().map(CostDriver::getId).toList();
        for (AbstractCostDriver acd : (List<AbstractCostDriver>) actualGC.get("costDrivers")) {
            // Compare the Parent
            if (!expectedGCParents.contains(acd.getId())) {
                fail("Missed parsing of ACD: " + acd.getId() + "(" + acd + ")"
                        + "\nExpected ACDs: " + expectedGCParents);
                // TODO: Compare the Children
            }
        }
    }

    private List<AbstractCostDriver> initialiseDriver() throws ScyllaValidationException {
        List<AbstractCostDriver> testAbstractCostDrivers = new LinkedList<>();
        List<ConcreteCostDriver> testConcreteCostDriver1 = new LinkedList<>();
        List<ConcreteCostDriver> testConcreteCostDriver2 = new LinkedList<>();
        List<ConcreteCostDriver> testConcreteCostDriver3 = new LinkedList<>();
        List<ConcreteCostDriver> testConcreteCostDriver4 = new LinkedList<>();
        List<ConcreteCostDriver> testConcreteCostDriver5 = new LinkedList<>();
        List<ConcreteCostDriver> testConcreteCostDriver6 = new LinkedList<>();

        AbstractCostDriver ACD_1 = new AbstractCostDriver("Delivery", testConcreteCostDriver1);
        ConcreteCostDriver CCD_1_1 = new ConcreteCostDriver("Delivery_B_Small_Lorry", ACD_1, 0.00005524);
        ConcreteCostDriver CCD_1_2 = new ConcreteCostDriver("Delivery_A_Small_Lorry", ACD_1, 0.00003683);
        ConcreteCostDriver CCD_1_3 = new ConcreteCostDriver("Delivery_A_Lorry", ACD_1, 0.00002843);
        ConcreteCostDriver CCD_1_4 = new ConcreteCostDriver("Delivery_B_Lorry", ACD_1, 0.00004265);
        ACD_1.addChild(CCD_1_1);
        ACD_1.addChild(CCD_1_2);
        ACD_1.addChild(CCD_1_3);
        ACD_1.addChild(CCD_1_4);
        AbstractCostDriver ACD_2 = new AbstractCostDriver("Filling Material", testConcreteCostDriver2);
        ConcreteCostDriver CCD_2_1 = new ConcreteCostDriver("Filling_A", ACD_2, 0.00001468);
        ACD_2.addChild(CCD_2_1);
        AbstractCostDriver ACD_3 = new AbstractCostDriver("Packaging Material", testConcreteCostDriver3);
        ConcreteCostDriver CCD_3_1 = new ConcreteCostDriver("Packaging_Material_A", ACD_3, 0.00007611);
        ConcreteCostDriver CCD_3_2 = new ConcreteCostDriver("Packaging_Material_B", ACD_3, 0.00003806);
        ACD_3.addChild(CCD_3_1);
        ACD_3.addChild(CCD_3_2);
        AbstractCostDriver ACD_4 = new AbstractCostDriver("Re-Routing", testConcreteCostDriver4);
        ConcreteCostDriver CCD_4_1 = new ConcreteCostDriver("Routing_A_Lorry", ACD_3, 0.000008529);
        ConcreteCostDriver CCD_4_2 = new ConcreteCostDriver("Routing_A_Small_Lorry", ACD_4, 0.00001105);
        ACD_4.addChild(CCD_4_1);
        ACD_4.addChild(CCD_4_2);
        AbstractCostDriver ACD_5 = new AbstractCostDriver("Receipt", testConcreteCostDriver5);
        ConcreteCostDriver CCD_5_1 = new ConcreteCostDriver("Receipt", ACD_5, 0.00001153);
        ACD_5.addChild(CCD_5_1);
        AbstractCostDriver ACD_6 = new AbstractCostDriver("Shipment", testConcreteCostDriver6);
        ConcreteCostDriver CCD_6_1 = new ConcreteCostDriver("Shipment_A_Lorry", ACD_6, 0.00007839);
        ConcreteCostDriver CCD_6_2 = new ConcreteCostDriver("Shipment_A_Rail_Electric", ACD_6, 0.0000000253);
        ConcreteCostDriver CCD_6_3 = new ConcreteCostDriver("Shipment_B_Lorry", ACD_6, 0.0001568);
        ConcreteCostDriver CCD_6_4 = new ConcreteCostDriver("Shipment_B_Rail_Electric", ACD_6, 0.0000000506);
        ACD_6.addChild(CCD_6_1);
        ACD_6.addChild(CCD_6_2);
        ACD_6.addChild(CCD_6_3);
        ACD_6.addChild(CCD_6_4);

        testAbstractCostDrivers.add(ACD_1);
        testAbstractCostDrivers.add(ACD_2);
        testAbstractCostDrivers.add(ACD_3);
        testAbstractCostDrivers.add(ACD_4);
        testAbstractCostDrivers.add(ACD_5);
        testAbstractCostDrivers.add(ACD_6);

        return testAbstractCostDrivers;
    }
}

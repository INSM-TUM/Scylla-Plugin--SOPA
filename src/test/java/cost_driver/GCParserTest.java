package cost_driver;

import de.hpi.bpt.scylla.exception.ScyllaValidationException;
import de.hpi.bpt.scylla.model.global.GlobalConfiguration;
import org.jdom2.JDOMException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GCParserTest {

    // TODO: make it work with actual .xml (any .xml)
    @Test
    @DisplayName("Integration test")
    void testParse_Integration() throws IOException, ScyllaValidationException, JDOMException {
        ScyllaScripts.runMoockModels();

        // Integrate the Global Configuration
        GlobalConfiguration actualGlobalConfig = ScyllaScripts.manager.getGlobalConfiguration();

        // Integrate the ACDs
        Object obj = actualGlobalConfig.getExtensionAttributes().get("cost_driver_costDrivers");

        if (obj instanceof ArrayList<?> list) {
            if (list.stream().allMatch(element -> element instanceof AbstractCostDriver)) {
                List<AbstractCostDriver> abstractCostDriverList = (ArrayList<AbstractCostDriver>) list;
                var expected = initialiseDriver();
                for (int i = 0; i < abstractCostDriverList.size(); i++) {
                    if (!abstractCostDriverList.get(i).equals(expected.get(i))) {
                        fail("\nWrongly parsed ACD: " +
                                "\nActual: " + abstractCostDriverList.get(i).toString() +
                                "\nExpected: " + expected.get(i).toString()
                        );
                    }
                }
            } else {
                throw new InvalidClassException("Not all elements in the list are of type AbstractCostDriver.");
            }
        } else {
            throw new ClassCastException("The object is not a ArrayList. Cannot cast to ArrayList<AbstractCostDriver>." +
                    "The Object is: " + obj.getClass());
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
        ConcreteCostDriver CCD_4_1 = new ConcreteCostDriver("Re-Routing_A_Lorry", ACD_4, 0.000008529);
        ConcreteCostDriver CCD_4_2 = new ConcreteCostDriver("Re-Routing_A_Small_Lorry", ACD_4, 0.00001105);
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

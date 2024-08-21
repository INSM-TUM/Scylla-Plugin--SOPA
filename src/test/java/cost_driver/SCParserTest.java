package cost_driver;

import de.hpi.bpt.scylla.model.configuration.SimulationConfiguration;
import de.hpi.bpt.scylla.model.global.GlobalConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

public class SCParserTest {

    @Test
    @DisplayName("Testing Parsing CostDriver List of String")
    void testParsing_CD() throws IOException {
        Scripts.runMoockModels();
        CostDriverSCParserPlugin testSubject = new CostDriverSCParserPlugin();

        // Integrate the Simulation Configuration
        SimulationConfiguration actualSimConfig = Scripts.manager.getSimulationConfigurations().get(Scripts.manager.getSimulationConfigurations().keySet().toArray()[0]);

        // Integrate the CDs
        HashMap<Integer, ArrayList<String>> actualCostDriverMap = (HashMap<Integer, ArrayList<String>>) actualSimConfig.getExtensionAttributes().get("cost_driver_costDrivers");

        List<String> actual = actualCostDriverMap.values()
                .stream()
                .flatMap(ArrayList::stream)
                .sorted()
                .toList();

        List<String> expected = initialiseDriver().stream()
                .sorted()
                .toList();

        if (!actual.equals(expected)) {
            int firstMismatchIndex = -1;
            for (int i = 0; i < Math.min(actual.size(), expected.size()); i++) {
                if (!actual.get(i).equals(expected.get(i))) {
                    firstMismatchIndex = i;
                    break;
                }
            }
            if (firstMismatchIndex != -1) {
                fail("Mismatch found at index " + firstMismatchIndex +
                        ". \n\t\tActual: " + actual.get(firstMismatchIndex) +
                        ", \n\t\tExpected: " + expected.get(firstMismatchIndex));
            } else {
                fail("Lists differ in size. Actual size: " + actual.size() +
                        ", \t\tExpected size: " + expected.size());
            }
        }
    }

    @Test
    @DisplayName("Testing Parsing CostVariant List of Strings")
    void testParsing_CV() throws IOException {
        Scripts.runMoockModels();
        CostDriverSCParserPlugin testSubject = new CostDriverSCParserPlugin();

        // Integrate the Simulation Configuration
        var l = Scripts.manager.getSimulationConfigurations();
        var k = Scripts.manager.getSimulationConfigurations().keySet().toArray()[0];
        SimulationConfiguration actualSimConfig = Scripts.manager.getSimulationConfigurations().get(k);

//        Map<String, CostVariantConfiguration> costVariantMap = (Map<String, CostVariantConfiguration>) actualSimConfig.getExtensionAttributes().get("cost_driver_CostVariant");
        CostVariantConfiguration costVariantConfiguration = (CostVariantConfiguration) actualSimConfig.getExtensionAttributes().get("cost_driver_CostVariant");

        var pause = 5;
    }

    private List<String> initialiseDriver() {
        return new ArrayList<>(Arrays.asList(
                "Packaging Material",
                "Filling Material",
                "Shipment",
                "Delivery",
                "Receipt",
                "Delivery",
                "Re-Routing"));


    }

}

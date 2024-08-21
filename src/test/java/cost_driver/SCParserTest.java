package cost_driver;

import de.hpi.bpt.scylla.SimulationManager;
import de.hpi.bpt.scylla.exception.ScyllaValidationException;
import de.hpi.bpt.scylla.model.configuration.SimulationConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

class SCParserTest {
    @BeforeEach
    void init() throws IOException {
        ScyllaScripts.runMoockModels();

    }

    // TODO: make it work with actual .xml (any .xml)
    @Test
    @DisplayName("Testing Parsing CostDriver List of String")
    void testParsing_CD() throws IOException {

        // Integrate the Simulation Configuration
        SimulationManager actualManager = ScyllaScripts.manager;
        SimulationConfiguration actualSimConfig = actualManager.getSimulationConfigurations().get(actualManager.getSimulationConfigurations().keySet().toArray()[0]);

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
                fail("Lists differ in size. " +
                        "\n\tActual size: " + actual.size() +
                        ", \t\tExpected size: " + expected.size());
            }
        }
    }

    // TODO: make it work with actual .xml (any .xml)
    @Test
    @DisplayName("Testing Parsing CostVariant List of Strings")
    void testParsing_CV() {
        // Integrate the Simulation Configuration
        SimulationManager actualManger = ScyllaScripts.manager;
        var actualSimConfigMap = actualManger.getSimulationConfigurations();
        var key = actualSimConfigMap.keySet().toArray()[0];
        SimulationConfiguration actualSimConfig = actualManger.getSimulationConfigurations().get(key);

        CostVariantConfiguration actualConfiguration = (CostVariantConfiguration) actualSimConfig.
                getExtensionAttributes().get("cost_driver_CostVariant");

        long currSeed = actualSimConfig.getRandomSeed();
        CostVariantConfiguration expectedConfiguration = new CostVariantConfiguration(10, initializeVariantList(), currSeed);

        Stack<CostVariant> i = actualConfiguration.getCostVariantListConfigured();
        var j = expectedConfiguration.getCostVariantListConfigured();
        for (int k = 0; k < i.size(); k++) {
            List<String> actualACDList = i.get(k).getConcretisedACD().keySet().stream().toList();
            List<Double> actualCostList = i.get(k).getConcretisedACD().values().stream().toList();
            List<String> expectedACDList = j.get(k).getConcretisedACD().keySet().stream().toList();
            List<Double> expectedCostList = j.get(k).getConcretisedACD().values().stream().toList();
            if(!(actualACDList.equals(expectedACDList) && actualCostList.equals(expectedCostList))){
                fail("Your configured cost variants do not match the provided simulation configuration. " +
                        "\n\tActual: " + j.get(k).getId() + "\n" + actualACDList + "\n" + actualCostList +
                        "\n\tExpected: " + j.get(k).getId() + "\n" + expectedACDList + "\n" + expectedCostList);
            }
        }
    }

    private List<CostVariant> initializeVariantList() {
        Map<String, Double> concretisedACD_1 = new HashMap<>();
        concretisedACD_1.put("Delivery", 0.00002843);
        concretisedACD_1.put("Filling Material", 0.00001468);
        concretisedACD_1.put("Packaging Material", 0.00003806);
        concretisedACD_1.put("Re-Routing", 0.000008529);
        concretisedACD_1.put("Receipt", 0.00001153);
        concretisedACD_1.put("Shipment", 0.00007839);

        Map<String, Double> concretisedACD_2 = new HashMap<>();
        concretisedACD_2.put("Delivery", 0.00002843);
        concretisedACD_2.put("Filling Material", 0.00001468);
        concretisedACD_2.put("Packaging Material", 0.00003806);
        concretisedACD_2.put("Re-Routing", 0.000008529);
        concretisedACD_2.put("Receipt", 0.00001153);
        concretisedACD_2.put("Shipment", 0.0000000253);

        Map<String, Double> concretisedACD_3 = new HashMap<>();
        concretisedACD_3.put("Delivery", 0.00004265);
        concretisedACD_3.put("Filling Material", 0.00001468);
        concretisedACD_3.put("Packaging Material", 0.00003806);
        concretisedACD_3.put("Re-Routing", 0.000008529);
        concretisedACD_3.put("Receipt", 0.00001153);
        concretisedACD_3.put("Shipment", 0.00007839);

        Map<String, Double> concretisedACD_4 = new HashMap<>();
        concretisedACD_4.put("Delivery", 0.00004265);
        concretisedACD_4.put("Filling Material", 0.00001468);
        concretisedACD_4.put("Packaging Material", 0.00003806);
        concretisedACD_4.put("Re-Routing", 0.000008529);
        concretisedACD_4.put("Receipt", 0.00001153);
        concretisedACD_4.put("Shipment", 0.0000000506);

        CostVariant costVariant1 = new CostVariant("Shipment and delivery over distance A", 0.2, concretisedACD_1);
        CostVariant costVariant2 = new CostVariant("Shipment and delivery over distance A_Electric", 0.05, concretisedACD_2);
        CostVariant costVariant3 = new CostVariant("Shipment and delivery over distance B", 0.5, concretisedACD_3);
        CostVariant costVariant4 = new CostVariant("Shipment and delivery over distance B", 0.25, concretisedACD_4);

        return new ArrayList<>(Arrays.asList(costVariant1, costVariant2, costVariant3, costVariant4));
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

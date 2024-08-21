package cost_driver;

import de.hpi.bpt.scylla.SimulationTest;
import de.hpi.bpt.scylla.exception.ScyllaRuntimeException;
import de.hpi.bpt.scylla.exception.ScyllaValidationException;
import de.hpi.bpt.scylla.model.global.GlobalConfiguration;
import org.jdom2.JDOMException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static cost_driver.Utils.*;
import static de.hpi.bpt.scylla.Scylla.normalizePath;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


class ExecutionLoggingPluginTest extends SimulationTest {

    private final CostDriverExecutionLoggingPlugin EXECUTION_LOGGING_PLUGIN = new CostDriverExecutionLoggingPlugin();
    private List<AbstractCostDriver> TEST_ABSTRACT_DRIVER_LIST = new LinkedList<>();
    private List<ConcreteCostDriver> CONCRETE_DRIVERS_1 = new LinkedList<>();
    private List<ConcreteCostDriver> CONCRETE_DRIVERS_2 = new LinkedList<>();
    private GlobalConfiguration TEST_GLOBAL_CONFIG;
    private CostVariant TEST_COST_VARIANT;

    @Test
    void testWriteToLog() throws IOException, ScyllaValidationException, JDOMException {

    }

    @Test
    @DisplayName("File extension with gzipOn = false")
    void testCorrectFileExtensionXES() {
        runSimpleSimulation(
                GLOBAL_CONFIGURATION,
                SIMULATION_MODEL,
                SIMULATION_CONFIGURATION);
        // By default is false
        EXECUTION_LOGGING_PLUGIN.gzipOn = false;

        String expectedFileName = normalizePath("./" + outputPath + SIMULATION_MODEL);
        Path filePath = Paths.get(expectedFileName.substring(0, expectedFileName.lastIndexOf('.')).concat(".xes"));
        assertTrue(Files.exists(filePath), "XES File does not exist: " + filePath);
        assertTrue(filePath.toString().endsWith(".xes"), "File extension is not .xes: " + filePath);
    }

    @Test
    @DisplayName("Integration - Find Concrete Cost Driver")
    void testFindConcreteCaseByCost_Integration() throws ScyllaValidationException, IOException {
        ScyllaScripts.runMoockModels();
        CostDriverExecutionLoggingPlugin testSubject = new CostDriverExecutionLoggingPlugin();

        // Integrate the Configurations
        TEST_GLOBAL_CONFIG = ScyllaScripts.manager.getGlobalConfiguration();

        // Integrate the ACDs
        TEST_ABSTRACT_DRIVER_LIST = (List<AbstractCostDriver>) TEST_GLOBAL_CONFIG.getExtensionAttributes().get("cost_driver_costDrivers");
        prepareCostVariant();

        String firstACD = TEST_ABSTRACT_DRIVER_LIST.get(0).getId();
        String secondACD = TEST_ABSTRACT_DRIVER_LIST.get(1).getId();

        try {
            Method findConcreteCaseByCost = testSubject.getClass().getDeclaredMethod("findConcreteCaseByCost", new Class[]{GlobalConfiguration.class, CostVariant.class, String.class});
            findConcreteCaseByCost.setAccessible(true);
            ConcreteCostDriver actualCCD = (ConcreteCostDriver) findConcreteCaseByCost.invoke(
                    testSubject, TEST_GLOBAL_CONFIG, TEST_COST_VARIANT, firstACD);

            if (!Objects.equals(actualCCD.getLCAScore(), TEST_ABSTRACT_DRIVER_LIST.get(0).getChildren().get(2).getLCAScore())) {
                fail("The returned Concrete Cost Driver is incorrect, expected the CCD object with the LCAScore - 0.00002843");
            }
        } catch (ScyllaRuntimeException | NoSuchMethodException | InvocationTargetException |
                 IllegalAccessException e) {
//            var pause = e.getCause().getClass().getSimpleName();
            fail("No exception should be thrown for the valid CCD and cost.");
        }

        try {
            Method findConcreteCaseByCost = testSubject.getClass().getDeclaredMethod("findConcreteCaseByCost", new Class[]{GlobalConfiguration.class, CostVariant.class, String.class});
            findConcreteCaseByCost.setAccessible(true);
            ConcreteCostDriver actualCCD = (ConcreteCostDriver) findConcreteCaseByCost.invoke(
                    testSubject, TEST_GLOBAL_CONFIG, TEST_COST_VARIANT, secondACD);
            fail("The ScyllaRuntimeException must be thrown for the invalid CCD.");
        } catch (ScyllaRuntimeException | NoSuchMethodException | InvocationTargetException |
                 IllegalAccessException e) {
            if (!(e.getCause() instanceof ScyllaRuntimeException)) {
                fail("ScyllaRuntimeException is thrown for a valid CCD.");
            }
        }
    }
    private void prepareCostVariant() {
        // Create CostVariant
        Map<String, Double> testConcretisedACD = new HashMap<>();

        // Valid LCAScore
        testConcretisedACD.put("Delivery", 0.00002843);

        // Invalid LCAScore
        testConcretisedACD.put(TEST_ABSTRACT_DRIVER_LIST.get(1).getId(), Math.pow(new Random().nextDouble(), Math.pow(10, -5)));

        // Instantiate new CostVariant
        TEST_COST_VARIANT = new CostVariant("testID", 0.2, testConcretisedACD);
    }

    @Override
    protected String getFolderName() {
        return "Shipping";
    }
}

package cost_driver;

import de.hpi.bpt.scylla.exception.ScyllaRuntimeException;
import de.hpi.bpt.scylla.exception.ScyllaValidationException;
import de.hpi.bpt.scylla.model.global.GlobalConfiguration;
import de.hpi.bpt.scylla.model.global.resource.DynamicResource;
import de.hpi.bpt.scylla.model.global.resource.Resource;
import de.hpi.bpt.scylla.plugin_type.parser.EventOrderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static cost_driver.TestUtils.*;
import static de.hpi.bpt.scylla.Scylla.normalizePath;
import static org.junit.jupiter.api.Assertions.*;

class ExecutionLoggingPluginTest extends SimulationTest {

    private final CostDriverExecutionLoggingPlugin executionLoggingPlugin = new CostDriverExecutionLoggingPlugin();

    @BeforeEach
    void prepare() {
        runSimpleSimulation(GLOBAL_CONFIGURATION,
                SIMULATION_MODEL,
                SIMULATION_CONFIGURATION);
    }

    @Test
    void testWriteToLog(){
        // Empty
    }

    @Test
    @DisplayName("File extension with gzipOn = false")
    void testCorrectFileExtensionXES() {
        // By default is false
        executionLoggingPlugin.gzipOn = false;

        String expectedFileName = normalizePath("./" + outputPath + SIMULATION_MODEL);
        Path filePath = Paths.get(expectedFileName.substring(0, expectedFileName.lastIndexOf('.')).concat(".xes"));
        assertTrue(Files.exists(filePath), "XES File does not exist: " + filePath);
        assertTrue(filePath.toString().endsWith(".xes"), "File extension is not .xes: " + filePath);
    }

    @Test
    void testFindConcreteCaseByCost() throws ScyllaValidationException {
        CostDriverExecutionLoggingPlugin testSubject = new CostDriverExecutionLoggingPlugin();

        // Create the objects for the testGlobalConfig
        String testID = "courier";
        ZoneId testZoneId = ZoneId.systemDefault();
        Long testNotRandomSeed = -5144667088361437244L;
        Map<String, Resource> testResources = new HashMap<>();
        List<EventOrderType> testEventOrderTypes = new LinkedList<>();
        // Create a testGlobalConfig
        GlobalConfiguration testGlobalConfig = new GlobalConfiguration(testID, testZoneId, testNotRandomSeed, testResources, testEventOrderTypes);

        // Prepare the lists for testing of ACD CCD.
        List<AbstractCostDriver> testAbstractCostDrivers = new LinkedList<>();
        List<ConcreteCostDriver> testConcreteCostDriver1 = new LinkedList<>();
        List<ConcreteCostDriver> testConcreteCostDriver2 = new LinkedList<>();

        AbstractCostDriver ACD_1 = new AbstractCostDriver("test1",testConcreteCostDriver1, TimeUnit.HOURS);
        ConcreteCostDriver CCD_1_1 = new ConcreteCostDriver("Delivery_B_Small_Lorry", ACD_1,0.00005524);
        ConcreteCostDriver CCD_1_2 = new ConcreteCostDriver("Delivery_A_Small_Lorry", ACD_1,0.00003683);
        ConcreteCostDriver CCD_1_3 = new ConcreteCostDriver("Delivery_A_Lorry", ACD_1,0.00002843);
        ConcreteCostDriver CCD_1_4 = new ConcreteCostDriver("Delivery_B_Lorry", ACD_1,0.00004265);
        ACD_1.addChild(CCD_1_1);
        ACD_1.addChild(CCD_1_2);
        ACD_1.addChild(CCD_1_3);
        ACD_1.addChild(CCD_1_4);
        AbstractCostDriver ACD_2 = new AbstractCostDriver("test2",testConcreteCostDriver2, TimeUnit.HOURS);
        ConcreteCostDriver CCD_2_1 = new ConcreteCostDriver("Delivery_B_Small_Lorry", ACD_2,0.00005524);
        ConcreteCostDriver CCD_2_2 = new ConcreteCostDriver("Delivery_A_Small_Lorry", ACD_2,0.00003683);
        ConcreteCostDriver CCD_2_3 = new ConcreteCostDriver("Delivery_A_Lorry", ACD_2,0.00007843);//
        ConcreteCostDriver CCD_2_4 = new ConcreteCostDriver("Delivery_B_Lorry", ACD_2,0.00004265);
        ACD_2.addChild(CCD_2_1);
        ACD_2.addChild(CCD_2_2);
        ACD_2.addChild(CCD_2_3);
        ACD_2.addChild(CCD_2_4);

        // Add ACDs in the list.
        testAbstractCostDrivers.add(ACD_1);
        testAbstractCostDrivers.add(ACD_2);

        // Put the ACDs in the GlobalConfiguration
        testGlobalConfig.getExtensionAttributes().put("cost_driver_costDrivers", testAbstractCostDrivers);

        // Create CostVariant
        Map<String, Double> testConcretisedACD = new HashMap<>();
        // Valid LCAScore
        testConcretisedACD.put("test1", 0.00002843);
        // Invalid LCAScore
        testConcretisedACD.put("test2", 0.00004d);
        CostVariant testCostVariant = new CostVariant("testID",0.0,testConcretisedACD);
        // Create objects for the testDynamicResource
        DynamicResource testResource = new DynamicResource("courier", null, 4,10, TimeUnit.HOURS);


        try {
            Method findConcreteCaseByCost = testSubject.getClass().getDeclaredMethod("findConcreteCaseByCost", new Class[]{GlobalConfiguration.class, CostVariant.class, String.class});
            findConcreteCaseByCost.setAccessible(true);
            ConcreteCostDriver actualCCD = (ConcreteCostDriver) findConcreteCaseByCost.invoke(testSubject, testGlobalConfig, testCostVariant, "test1");
            if(!Objects.equals(actualCCD.getLCAScore(), testConcreteCostDriver1.get(2).getLCAScore())){
                fail("The returned Concrete Cost Driver is incorrect, expected the CCD object with the LCAScore - 0.00002843");
            }
        }catch (ScyllaRuntimeException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            fail("No exception should be thrown for the valid CCD and cost.");
        }

        try {
            Method findConcreteCaseByCost = testSubject.getClass().getDeclaredMethod("findConcreteCaseByCost", new Class[]{GlobalConfiguration.class, CostVariant.class, String.class});
            findConcreteCaseByCost.setAccessible(true);
            ConcreteCostDriver actualCCD = (ConcreteCostDriver) findConcreteCaseByCost.invoke(testSubject, testGlobalConfig, testCostVariant, "test2");
            fail("The ScyllaRuntimeException must be thrown for the invalid CCD.");
        }catch (ScyllaRuntimeException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            String actualError = e.getCause().getClass().getSimpleName();
            if(!(e.getCause() instanceof ScyllaRuntimeException)){
                fail("ScyllaRuntimeException is thrown for a valid CCD.");
            }
        }
    }
    @Override
    protected String getFolderName() {
        return "Shipping";
    }
}
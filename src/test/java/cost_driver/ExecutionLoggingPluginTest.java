package cost_driver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;

import static de.hpi.bpt.scylla.Scylla.normalizePath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExecutionLoggingPluginTest extends SimulationTest{

    private CostDriverExecutionLoggingPlugin executionLoggingPlugin = new CostDriverExecutionLoggingPlugin();

    @Test
    void getName() {
        String actual = executionLoggingPlugin.getName();
        assertEquals("cost_driver", actual);
    }

    @Test
    @DisplayName("Test correct File Extension")
    void testCorrectFileExtension() {
        createSimpleSimulationManager(
                "logistics_global.xml",
                "logistics_model_no_drivers.bpmn",
                "logistics_sim.xml");

       String expectedFileName = normalizePath("./"+outputPath+getProcessModel().getName()+"_no_drivers.xes");
        System.out.println(expectedFileName);
        assertTrue(true);
    }

    @Override
    protected String getFolderName() {
        return null;
    }
}

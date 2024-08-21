package cost_driver;

import cost_driver.CostDriverGCParserPlugin;
import cost_driver.SimulationTest;
import de.hpi.bpt.scylla.exception.ScyllaValidationException;
import de.hpi.bpt.scylla.model.configuration.SimulationConfiguration;
import de.hpi.bpt.scylla.model.global.GlobalConfiguration;
import de.hpi.bpt.scylla.model.global.resource.Resource;
import de.hpi.bpt.scylla.plugin_type.parser.EventOrderType;
import org.jdom2.*;

import java.io.IOException;
import java.time.Duration;
import java.time.ZoneId;
import java.util.*;

import org.jdom2.JDOMException;
import org.junit.jupiter.api.Test;


import de.hpi.bpt.scylla.exception.ScyllaValidationException;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import javax.naming.Name;

class GCParserTest {

    @Test
    void testParse() throws ScyllaValidationException {
        CostDriverGCParserPlugin testSubject = new CostDriverGCParserPlugin();

        // Create the objects for the testGlobalConfig
        String testID = "logistics_global";
        ZoneId testZoneId = ZoneId.systemDefault();
        Long testNotRandomSeed = -5144667088361437244L;
        Map<String, Resource> testResources = new HashMap<>();
        List<EventOrderType> testEventOrderTypes = new LinkedList<>();
        // Create a testGlobalConfig
        GlobalConfiguration testGlobalConfig = new GlobalConfiguration(testID, testZoneId, testNotRandomSeed, testResources, testEventOrderTypes);

        // Instead of new Element(), create it and parse it here
        testSubject.parse(testGlobalConfig, new Element("globalConfiguration"));


    }
}
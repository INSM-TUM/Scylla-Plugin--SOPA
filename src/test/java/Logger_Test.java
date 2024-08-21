import cost_driver.CostDriverExecutionLoggingPlugin;
import org.junit.jupiter.api.Test;

public class Logger_Test {

    private CostDriverExecutionLoggingPlugin executionLoggingPlugin = new CostDriverExecutionLoggingPlugin();

    @Test
    public void print(){
        System.out.println("Test Test");
    }
}

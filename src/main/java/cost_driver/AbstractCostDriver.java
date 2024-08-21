package cost_driver;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AbstractCostDriver extends costDriver{

    protected TimeUnit defualtTimeUnit;
    protected List<costDriver> children;


    public AbstractCostDriver(String id, List<costDriver> children, TimeUnit defaultTimeUnit) {
        super(id);
        this.children = children;
        this.defualtTimeUnit = defaultTimeUnit;
    }

    public List<costDriver> getChildren() {
        return this.children;
    }

    public void addChild(costDriver concreteCostDriver) {
        children.add(concreteCostDriver);
    }

    public TimeUnit getDefualtTimeUnit() {
        return defualtTimeUnit;
    }

    public void setDefualtTimeUnit(TimeUnit defualtTimeUnit) {
        this.defualtTimeUnit = defualtTimeUnit;
    }
}

package cost_driver.model;

import de.hpi.bpt.scylla.exception.ScyllaValidationException;

public class ConcreteCostDriver extends costDriver{
    protected costDriver parent;

    public ConcreteCostDriver(String id, costDriver parent) throws ScyllaValidationException {
        super(id);
        if (parent instanceof AbstractCostDriver) {
            this.parent = parent;
        } else { throw new ScyllaValidationException("Parent cost driver is not abstract");}
    }

    public costDriver getParent() {
        return parent;
    }
}

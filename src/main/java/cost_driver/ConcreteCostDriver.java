package cost_driver;

import de.hpi.bpt.scylla.exception.ScyllaValidationException;
import de.hpi.bpt.scylla.model.configuration.distribution.TimeDistributionWrapper;

public class ConcreteCostDriver extends CostDriver {
    protected CostDriver parent;
    protected Double LCAScore;

    public ConcreteCostDriver(String id, CostDriver parent, Double LCAScore) throws ScyllaValidationException {
        super(id);
        if (parent instanceof AbstractCostDriver) {
            this.parent = parent;
        } else {
            throw new ScyllaValidationException("Parent cost driver is not abstract");
        }

        this.LCAScore = LCAScore;
    }

    public Double getLCAScore() {
        return LCAScore;
    }

    public CostDriver getParent() {
        return parent;
    }

    public void setParent(CostDriver parent) {
        this.parent = parent;
    }
}

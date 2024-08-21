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

    @Override
    public boolean equals(Object obj) {
        // If the object is compared with itself then return true
        if (obj == this) return true;

        /* Check if obj is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(obj instanceof ConcreteCostDriver concreteCostDriver)) return false;

        // TODO
        //It is a weak check <- to be Extended
        boolean isParentEqual = parent.id.compareTo(concreteCostDriver.parent.id) == 0;

        if (id.compareTo(concreteCostDriver.id) == 0 &&
                isParentEqual &&
                Double.compare(LCAScore, concreteCostDriver.LCAScore) == 0) {
            return true;
        }
        return false;
    }


    @Override
    public String toString() {
        return "\nConcreteCostDriver{" +
                "id='" + id + '\'' +
                ", parentID=" + (parent != null ? parent.id : "null") +
                ", LCAScore=" + LCAScore +
                '}';
    }
}

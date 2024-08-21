package cost_driver.model;

import de.hpi.bpt.scylla.exception.ScyllaValidationException;

import javax.xml.datatype.Duration;

public class ConcreteCostDriver extends costDriver{
    protected costDriver parent;
    protected Double probability;
    protected Double LCAScore;
    protected Duration duration;

    public ConcreteCostDriver(String id, costDriver parent, Double probability, Double LCAScore, Duration duration) throws ScyllaValidationException {
        super(id);
        if (parent instanceof AbstractCostDriver) {
            this.parent = parent;
        } else { throw new ScyllaValidationException("Parent cost driver is not abstract");}

        this.duration = duration;
        this.LCAScore = LCAScore;
        this.probability = probability;
    }

    public Double getProbability() {
        return probability;
    }

    public Double getLCAScore() {
        return LCAScore;
    }

    public Duration getDuration() {
        return duration;
    }

    public costDriver getParent() {
        return parent;
    }
}

package cost_driver;

import java.util.*;

public class CostVariantConfig {

    protected Integer count;
    protected List<CostVariant> costVariants;

    public CostVariantConfig(Integer count, List<CostVariant> costVariants) {
        this.count = count;
        this.costVariants = costVariants;
    }

    public Integer getCount() {
        return count;
    }

    public List<CostVariant> getCostVariants() {
        return costVariants;
    }
}

package cost_driver;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CostVariantConfiguration {

    protected Integer count;
    protected List<CostVariant> costVariantList;
    protected List<CostVariant> costVariantListConfigured;

    /*
    * Constructor's logic is derived from
    * https://github.com/INSM-TUM/sustainability-scylla-extension
    * Approach:
    * Use OOP concepts to create a list of configured cost variants
    * 1. For each cost variant, multiply its frequency by the number of simulation runs
    * 2. Round the result of step 1 to the nearest integer
    * 3. Add the cost variant to the list of configured cost variants as many times as the result of step 2
    * 4. Shuffle the list of configured cost variants
    *
    * */
    public CostVariantConfiguration(Integer count, List<CostVariant> costVariantList) {
        this.count = count;
        this.costVariantList = costVariantList;

        this.costVariantListConfigured = new ArrayList<>();
        this.costVariantList.forEach(costVariant ->
                IntStream.range(0, (int) Math.round(costVariant.frequency * count))
                        .forEach(i -> costVariantListConfigured.add(costVariant))
        );

        Collections.shuffle(costVariantListConfigured, new Random());
        System.out.println("Size of configured cost variant list: " + costVariantListConfigured.size());
    }

    public Integer getCount() {
        return count;
    }

    public List<CostVariant> getCostVariantList() {
        return costVariantList;
    }

    public List<CostVariant> getCostVariantListConfigured() {
        return costVariantListConfigured;
    }
}

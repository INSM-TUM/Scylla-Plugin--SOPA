package cost_driver;

import de.hpi.bpt.scylla.exception.ScyllaValidationException;

import java.util.*;
import java.util.stream.IntStream;

public class CostVariantConfiguration {

    private Integer count;
    private List<CostVariant> costVariantList;
    private final Stack<CostVariant> costVariantListConfigured;

    /**
     * Constructor's logic is derived from
     * https://github.com/INSM-TUM/sustainability-scylla-extension
     * Approach:
     * Use OOP concepts to create a list of configured cost variants
     * 1. For each cost variant, multiply its frequency by the number of simulation runs
     * 2. Round the result of step 1 to the nearest integer
     * 3. Add the cost variant to the list of configured cost variants as many times as the result of step 2
     * 4. Shuffle the list of configured cost variants
     *
     * @param count: number of simulation runs
     * @param costVariantList: list of parsed cost variants from the simulation configuration
     */
    public CostVariantConfiguration(Integer count, List<CostVariant> costVariantList, Long seed) throws ScyllaValidationException {
        this.count = count;
        this.costVariantList = costVariantList;

        if (costVariantList == null || costVariantList.isEmpty()) {
            throw new ScyllaValidationException("Cost Variant list is empty. Cannot configure without any cost variants.");
        }

        this.costVariantListConfigured = new Stack<>();
        this.costVariantList.forEach(costVariant ->
                IntStream.range(0, (int) Math.round(costVariant.frequency * count))
                        .forEach(i -> costVariantListConfigured.push(costVariant))
        );

        Collections.shuffle(costVariantListConfigured, new Random(seed));
        System.out.println("Size of configured cost variant list: " + costVariantListConfigured.size());
    }

    public Integer getCount() {
        return count;
    }

    public List<CostVariant> getCostVariantList() {
        return costVariantList;
    }

    public Stack<CostVariant> getCostVariantListConfigured() {
        return costVariantListConfigured;
    }
}

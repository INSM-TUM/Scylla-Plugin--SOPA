package cost_driver;

public abstract class CostDriver {
    protected String id;


    protected CostDriver(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        // If the object is compared with itself then return true
        if (obj == this) {
            return true;
        }

        /* Check if obj is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(obj instanceof CostDriver costDriver)) {
            return false;
        }

        return id.compareTo(costDriver.id) == 0;
    }
}

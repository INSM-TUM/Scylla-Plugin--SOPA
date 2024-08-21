package cost_driver;

public abstract class CostDriver {
    protected String id;


    public CostDriver(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

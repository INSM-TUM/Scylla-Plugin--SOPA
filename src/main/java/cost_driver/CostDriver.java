package cost_driver;

public abstract class CostDriver {
    protected String id;


    protected CostDriver(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

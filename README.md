# BearCrow
Simulate models with sustainability info, process and visualize the simulation results.

## ℹ️ About
The plugin works by extending Scylla, a business process simulator, by processing sustainability information
of a business process, passing it through the Scylla simulation and outputs data insights.
### Why sustainability information important?
Provides insights to the environmental impact of business processes/activities.
### What is Cost Driver?
Cost Driver is the term used to describe the cost of an activity.
### What is Cost Variant?
Cost Variant is a process instance with a different combination of activities, thus different cost drivers.
### What is LCA Scores?
A quantified score of environmental impacts associated with the life cycle of a commercial product.
## 🎯Objective
### 🛠️ How to run it?
***Please remember to change the path in pom.xml to your own Absolute Path of your scylla.jar file***
1. Navigate to src/main/java/cost_driver/Main and run.
2. Select the desired configuration files and "cost_driver" as plugin.
<img width="1822" alt="Screenshot 2024-01-17 at 23 10 06" src="https://github.com/mhunter02/BearCrow-private/assets/85895529/83200e2f-5fce-4098-8c8e-0b2224d9d91e">
4. The logged data files will be found in a folder with format: "output_yy_mm_dd...."


## 🧱 Components
There are three Plugins cooperating to achieve this.

### Global Configuration Parser Plugin
Parses the global config file of format as such:
```ruby
<bsim:costDriver>
    <bsim:abstractCostDriver id="Delivery" defaultTimeUnit="HOURS">
      <bsim:concreteCostDeiver id="Delivery_B_Small_Lorry" cost="0.00005524"/>
      <bsim:concreteCostDeiver id="Delivery_B_Lorry" cost="0.00004265"/>
    </bsim:abstractCostDriver>
```
### Simulation Configuration Parser Plugin
Parses the simulation config file of format as such:
```ruby
bsim:costVariantConfig>
      <bsim:variant id="Shipment and delivery over distance A" frequency="0.2">
        <bsim:driver id="Delivery" cost="0.00002843"/>
      </bsim:variant>
```
### Logger Plugin
Logs the extended simulation data in the form of an XES and XML file.  

## Results
```ruby
<CostVariantAverageTime>
    <Shipment_and_delivery_over_distance_B>1.3410227142857142E-4</Shipment_and_delivery_over_distance_B>
    <Shipment_and_delivery_over_distance_A>1.7377499999999998E-4</Shipment_and_delivery_over_distance_A>
    <Shipment_and_delivery_over_distance_A_Electric>8.11953E-5</Shipment_and_delivery_over_distance_A_Electric>
    <All_Traces_Average_Cost>1.3674612E-4</All_Traces_Average_Cost>
    <Activity_Average_Cost>
        <Print_and_post_pick-up_receipt>
            <Shipment_and_delivery_over_distance_B>0.0</Shipment_and_delivery_over_distance_B>
            <Shipment_and_delivery_over_distance_A>0.0</Shipment_and_delivery_over_distance_A>
            <Shipment_and_delivery_over_distance_A_Electric>0.0</Shipment_and_delivery_over_distance_A_Electric>
        </Print_and_post_pick-up_receipt>
        <Product_received>
            <Shipment_and_delivery_over_distance_B>0.0</Shipment_and_delivery_over_distance_B>
            <Shipment_and_delivery_over_distance_A>0.0</Shipment_and_delivery_over_distance_A>
            <Shipment_and_delivery_over_distance_A_Electric>0.0</Shipment_and_delivery_over_distance_A_Electric>
        </Product_received>
        <Deliver_to_Door>
            <Shipment_and_delivery_over_distance_B>3.0464285714285715E-5</Shipment_and_delivery_over_distance_B>
            <Shipment_and_delivery_over_distance_A>2.843E-5</Shipment_and_delivery_over_distance_A>
            <Shipment_and_delivery_over_distance_A_Electric>2.843E-5</Shipment_and_delivery_over_distance_A_Electric>
        </Deliver_to_Door>
```

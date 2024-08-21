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
Cost variants govern what specific combinations of concretizations can occur during individual process instances based on the environmental cost driver hierarchy — in other words, what sets of concrete environmental cost drivers can, during process execution, take the place of the abstract environmental cost drivers during activity execution.
### What is LCA Scores?
A quantified score of environmental impacts associated with the life cycle of a commercial product.
## 🎯Objective
### 🛠️ How to run it?
***Please remember to change the path in pom.xml to your own Absolute Path of your scylla.jar file***
1. Navigate to src/main/java/cost_driver/Main and run.
2. Select the desired configuration files and "cost_driver" as plugin.
<img width="1822" alt="Screenshot 2024-01-17 at 23 10 06" src="https://github.com/mhunter02/BearCrow-private/assets/85895529/83200e2f-5fce-4098-8c8e-0b2224d9d91e">
3. The logged data files will be found in a folder with format: "output_yy_mm_dd...."


## 🧱 Components
There are three Plugins cooperating to achieve this.

### Global Configuration Parser Plugin
Parses the global config file of which describes the abstractCDs and the concreteCDs that it consists of:
The
```ruby
<bsim:costDriver>
    <bsim:abstractCostDriver id="Delivery" defaultTimeUnit="HOURS">
      <bsim:concreteCostDeiver id="Delivery_B_Small_Lorry" cost="0.00005524"/>
      <bsim:concreteCostDeiver id="Delivery_B_Lorry" cost="0.00004265"/>
    </bsim:abstractCostDriver>
```
### Simulation Configuration Parser Plugin
Parses the simulation config file which describes the cost variant by id, frequency of occurence, and cost:
```ruby
bsim:costVariantConfig>
      <bsim:variant id="Shipment and delivery over distance A" frequency="0.2">
        <bsim:driver id="Delivery" cost="0.00002843"/>
      </bsim:variant>
```
### Logger Plugin
Logs the extended simulation data in the form of an XES and XML file.  

## Results
The outputted file shows a complete detailed breakdown of sustainability info.
Explanation of nodes moving downwards:
```ruby
/// Cost of each Cost Variant
<Sustainability_Info>
    <Shipment_and_delivery_over_distance_B>1.516574142857143E-4</Shipment_and_delivery_over_distance_B>
    <Shipment_and_delivery_over_distance_A>1.79619E-4</Shipment_and_delivery_over_distance_A>
    <Shipment_and_delivery_over_distance_A_Electric>1.0125430000000001E-4</Shipment_and_delivery_over_distance_A_Electric>
/// Average Process Instance cost
    <Average_Process_Instance_Cost>1.5220942000000002E-4</Average_Process_Instance_Cost>
/// Activity Average Cost: Shows average cost of executing an activity across all variants.
    <Activity_Average_Cost>
        <Print_and_post_pick-up_receipt>
            <Shipment_and_delivery_over_distance_B>1.153E-5</Shipment_and_delivery_over_distance_B>
            <Shipment_and_delivery_over_distance_A>1.153E-5</Shipment_and_delivery_over_distance_A>
            <Shipment_and_delivery_over_distance_A_Electric>1.153E-5</Shipment_and_delivery_over_distance_A_Electric>
            <Print_and_post_pick-up_receipt_average_activity_cost>1.1530000000000001E-5</Print_and_post_pick-up_receipt_average_activity_cost>
        </Print_and_post_pick-up_receipt>
        <Product_received>
            <Shipment_and_delivery_over_distance_B>0.0</Shipment_and_delivery_over_distance_B>
            <Shipment_and_delivery_over_distance_A>0.0</Shipment_and_delivery_over_distance_A>
            <Shipment_and_delivery_over_distance_A_Electric>0.0</Shipment_and_delivery_over_distance_A_Electric>
            <Product_received_average_activity_cost>0.0</Product_received_average_activity_cost>
        </Product_received>
        <Deliver_to_Door>
            <Shipment_and_delivery_over_distance_B>4.265E-5</Shipment_and_delivery_over_distance_B>
            <Shipment_and_delivery_over_distance_A>2.843E-5</Shipment_and_delivery_over_distance_A>
            <Shipment_and_delivery_over_distance_A_Electric>2.843E-5</Shipment_and_delivery_over_distance_A_Electric>
            <Deliver_to_Door_average_activity_cost>3.73175E-5</Deliver_to_Door_average_activity_cost>
        </Deliver_to_Door>
        <Package_product>
            <Shipment_and_delivery_over_distance_B>5.274E-5</Shipment_and_delivery_over_distance_B>
            <Shipment_and_delivery_over_distance_A>5.274E-5</Shipment_and_delivery_over_distance_A>
            <Shipment_and_delivery_over_distance_A_Electric>5.274E-5</Shipment_and_delivery_over_distance_A_Electric>
            <Package_product_average_activity_cost>5.274E-5</Package_product_average_activity_cost>
        </Package_product>
        <Product_delivered_sucessfully>
            <Shipment_and_delivery_over_distance_B>0.0</Shipment_and_delivery_over_distance_B>
            <Shipment_and_delivery_over_distance_A>0.0</Shipment_and_delivery_over_distance_A>
            <Shipment_and_delivery_over_distance_A_Electric>0.0</Shipment_and_delivery_over_distance_A_Electric>
            <Product_delivered_sucessfully_average_activity_cost>0.0</Product_delivered_sucessfully_average_activity_cost>
        </Product_delivered_sucessfully>
        <Deliver_to_Packstation>
            <Shipment_and_delivery_over_distance_B>4.265E-5</Shipment_and_delivery_over_distance_B>
            <Deliver_to_Packstation_average_activity_cost>4.265E-5</Deliver_to_Packstation_average_activity_cost>
        </Deliver_to_Packstation>
        <Re-route_to_Packstation>
            <Shipment_and_delivery_over_distance_B>8.529E-6</Shipment_and_delivery_over_distance_B>
            <Shipment_and_delivery_over_distance_A>8.529E-6</Shipment_and_delivery_over_distance_A>
            <Shipment_and_delivery_over_distance_A_Electric>8.529E-6</Shipment_and_delivery_over_distance_A_Electric>
            <Re-route_to_Packstation_average_activity_cost>8.529E-6</Re-route_to_Packstation_average_activity_cost>
        </Re-route_to_Packstation>
        <Ship_product>
            <Shipment_and_delivery_over_distance_B>4.480512857142858E-5</Shipment_and_delivery_over_distance_B>
            <Shipment_and_delivery_over_distance_A>7.839E-5</Shipment_and_delivery_over_distance_A>
            <Shipment_and_delivery_over_distance_A_Electric>2.53E-8</Shipment_and_delivery_over_distance_A_Electric>
            <Ship_product_average_activity_cost>4.704412E-5</Ship_product_average_activity_cost>
        </Ship_product>
    </Activity_Average_Cost>
/// Activity Instance Cost: Breakdown of activity instances and the ConcreteCDs of each AbstractCD.
    <Activity_Instance_Cost>
        <Print_and_post_pick-up_receipt ACD="Receipt">
            <Shipment_and_delivery_over_distance_B CCD="Receipt">
                <activity_instance_cost>1.153E-5</activity_instance_cost>
                <activity_instance_cost>1.153E-5</activity_instance_cost>
                <activity_instance_cost>1.153E-5</activity_instance_cost>
                <activity_instance_cost>1.153E-5</activity_instance_cost>
            </Shipment_and_delivery_over_distance_B>
            <Shipment_and_delivery_over_distance_A CCD="Receipt">
                <activity_instance_cost>1.153E-5</activity_instance_cost>
                <activity_instance_cost>1.153E-5</activity_instance_cost>
            </Shipment_and_delivery_over_distance_A>
            <Shipment_and_delivery_over_distance_A_Electric CCD="Receipt">
                <activity_instance_cost>1.153E-5</activity_instance_cost>
            </Shipment_and_delivery_over_distance_A_Electric>
        </Print_and_post_pick-up_receipt>
        <Product_received>
            <Shipment_and_delivery_over_distance_B>
                <activity_instance_cost>0.0</activity_instance_cost>
                <activity_instance_cost>0.0</activity_instance_cost>
                <activity_instance_cost>0.0</activity_instance_cost>
                <activity_instance_cost>0.0</activity_instance_cost>
                <activity_instance_cost>0.0</activity_instance_cost>
                <activity_instance_cost>0.0</activity_instance_cost>
                <activity_instance_cost>0.0</activity_instance_cost>
            </Shipment_and_delivery_over_distance_B>
            <Shipment_and_delivery_over_distance_A>
                <activity_instance_cost>0.0</activity_instance_cost>
                <activity_instance_cost>0.0</activity_instance_cost>
            </Shipment_and_delivery_over_distance_A>
            <Shipment_and_delivery_over_distance_A_Electric>
                <activity_instance_cost>0.0</activity_instance_cost>
            </Shipment_and_delivery_over_distance_A_Electric>
        </Product_received>
        <Deliver_to_Door ACD="Delivery">
            <Shipment_and_delivery_over_distance_B CCD="Delivery_B_Lorry">
                <activity_instance_cost>4.265E-5</activity_instance_cost>
                <activity_instance_cost>4.265E-5</activity_instance_cost>
                <activity_instance_cost>4.265E-5</activity_instance_cost>
                <activity_instance_cost>4.265E-5</activity_instance_cost>
                <activity_instance_cost>4.265E-5</activity_instance_cost>
            </Shipment_and_delivery_over_distance_B>
            <Shipment_and_delivery_over_distance_A CCD="Delivery_A_Lorry">
                <activity_instance_cost>2.843E-5</activity_instance_cost>
                <activity_instance_cost>2.843E-5</activity_instance_cost>
            </Shipment_and_delivery_over_distance_A>
            <Shipment_and_delivery_over_distance_A_Electric CCD="Delivery_A_Lorry">
                <activity_instance_cost>2.843E-5</activity_instance_cost>
            </Shipment_and_delivery_over_distance_A_Electric>
        </Deliver_to_Door>
        <Package_product ACD="Packaging Material, Filling Material">
            <Shipment_and_delivery_over_distance_B CCD="Packaging_Material_B, Filling_A">
                <activity_instance_cost>5.274E-5</activity_instance_cost>
                <activity_instance_cost>5.274E-5</activity_instance_cost>
                <activity_instance_cost>5.274E-5</activity_instance_cost>
                <activity_instance_cost>5.274E-5</activity_instance_cost>
                <activity_instance_cost>5.274E-5</activity_instance_cost>
                <activity_instance_cost>5.274E-5</activity_instance_cost>
                <activity_instance_cost>5.274E-5</activity_instance_cost>
            </Shipment_and_delivery_over_distance_B>
            <Shipment_and_delivery_over_distance_A CCD="Packaging_Material_B, Filling_A">
                <activity_instance_cost>5.274E-5</activity_instance_cost>
                <activity_instance_cost>5.274E-5</activity_instance_cost>
            </Shipment_and_delivery_over_distance_A>
            <Shipment_and_delivery_over_distance_A_Electric CCD="Packaging_Material_B, Filling_A">
                <activity_instance_cost>5.274E-5</activity_instance_cost>
            </Shipment_and_delivery_over_distance_A_Electric>
        </Package_product>
        <Product_delivered_sucessfully>
            <Shipment_and_delivery_over_distance_B>
                <activity_instance_cost>0.0</activity_instance_cost>
                <activity_instance_cost>0.0</activity_instance_cost>
                <activity_instance_cost>0.0</activity_instance_cost>
                <activity_instance_cost>0.0</activity_instance_cost>
                <activity_instance_cost>0.0</activity_instance_cost>
                <activity_instance_cost>0.0</activity_instance_cost>
                <activity_instance_cost>0.0</activity_instance_cost>
            </Shipment_and_delivery_over_distance_B>
            <Shipment_and_delivery_over_distance_A>
                <activity_instance_cost>0.0</activity_instance_cost>
                <activity_instance_cost>0.0</activity_instance_cost>
            </Shipment_and_delivery_over_distance_A>
            <Shipment_and_delivery_over_distance_A_Electric>
                <activity_instance_cost>0.0</activity_instance_cost>
            </Shipment_and_delivery_over_distance_A_Electric>
        </Product_delivered_sucessfully>
        <Deliver_to_Packstation ACD="Delivery">
            <Shipment_and_delivery_over_distance_B CCD="Delivery_B_Lorry">
                <activity_instance_cost>4.265E-5</activity_instance_cost>
                <activity_instance_cost>4.265E-5</activity_instance_cost>
            </Shipment_and_delivery_over_distance_B>
        </Deliver_to_Packstation>
        <Re-route_to_Packstation ACD="Re-Routing">
            <Shipment_and_delivery_over_distance_B CCD="Re-Routing_A_Lorry">
                <activity_instance_cost>8.529E-6</activity_instance_cost>
                <activity_instance_cost>8.529E-6</activity_instance_cost>
                <activity_instance_cost>8.529E-6</activity_instance_cost>
                <activity_instance_cost>8.529E-6</activity_instance_cost>
            </Shipment_and_delivery_over_distance_B>
            <Shipment_and_delivery_over_distance_A CCD="Re-Routing_A_Lorry">
                <activity_instance_cost>8.529E-6</activity_instance_cost>
                <activity_instance_cost>8.529E-6</activity_instance_cost>
            </Shipment_and_delivery_over_distance_A>
            <Shipment_and_delivery_over_distance_A_Electric CCD="Re-Routing_A_Lorry">
                <activity_instance_cost>8.529E-6</activity_instance_cost>
            </Shipment_and_delivery_over_distance_A_Electric>
        </Re-route_to_Packstation>
        <Ship_product ACD="Shipment">
            <Shipment_and_delivery_over_distance_B CCD="Shipment_A_Rail_Electric">
                <activity_instance_cost>2.53E-8</activity_instance_cost>
                <activity_instance_cost>7.839E-5</activity_instance_cost>
                <activity_instance_cost>7.839E-5</activity_instance_cost>
                <activity_instance_cost>7.839E-5</activity_instance_cost>
                <activity_instance_cost>2.53E-8</activity_instance_cost>
                <activity_instance_cost>2.53E-8</activity_instance_cost>
                <activity_instance_cost>7.839E-5</activity_instance_cost>
            </Shipment_and_delivery_over_distance_B>
            <Shipment_and_delivery_over_distance_A CCD="Shipment_A_Lorry">
                <activity_instance_cost>7.839E-5</activity_instance_cost>
                <activity_instance_cost>7.839E-5</activity_instance_cost>
            </Shipment_and_delivery_over_distance_A>
            <Shipment_and_delivery_over_distance_A_Electric CCD="Shipment_A_Rail_Electric">
                <activity_instance_cost>2.53E-8</activity_instance_cost>
            </Shipment_and_delivery_over_distance_A_Electric>
        </Ship_product>
    </Activity_Instance_Cost>
</Sustainability_Info>
```


References: <br>
Pufahl, L., & Weske, M. (January 2018). Design of an Extensible BPMN Process Simulator. Retrieved from https://www.researchgate.net/publication/322524759_Design_of_an_Extensible_BPMN_Process_Simulator?enrichId=rgreq-55dc4561329b473ce8f8871f05e56dba-XXX&enrichSource=Y292ZXJQYWdlOzMyMjUyNDc1OTtBUzo1OTU2ODQ2MDQ1MjY1OTJAMTUxOTAzMzY4NTAwNg%3D%3D&el=1_x_3&_esc=publicationCoverPdf  <br>
Ng, K. Y. (1996). An Algorithm for Acyclic State Machines. *Acta Informatica*, 33(4), 223–228. https://doi.org/10.1007/BF02986351


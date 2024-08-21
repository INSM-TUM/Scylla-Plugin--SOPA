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
Whilst the concern for the world’s ecosystem seems to grow, industries need to measure how much their business processes have an impact on the environment. Thus, considering the environmental impacts of business processes has become an important factor that needs to be considered.
Business process model and notation (BPMN) has been introduced to organizations to allow them to construct models of their business processes. Within this business process exist activities that contain further information about the activity itself.
Scylla is a BPMN simulator being used in the case, the plugin is an extension of Scylla by dealing with those additional sustainability infos.

More information can be found in our [report]().
### 🛠️ How to run it?
### [For Developers]
1. git clone our repository
2. [Download](https://github.com/bptlab/scylla/releases) and add Scylla.jar and scylla-tests.jar into libs
3. Navigate to src/main/java/cost_driver/Main and run.
4. Select the desired configuration files in samples UI and check "cost_driver" as plugin.
<img width="1822" alt="Screenshot 2024-01-17 at 23 10 06" src="https://github.com/mhunter02/BearCrow-private/assets/85895529/83200e2f-5fce-4098-8c8e-0b2224d9d91e">
5. The logged data files will be found in a folder with format: "output_yy_mm_dd...."
#### NOTE
*1. Please remember to put the latest scylla.jar & scylla-tests.jar files in the ./lib folder</br>
*2. Another way of managing the plugin is by replacing the current Scylla dependencies, with the following. Please ensure you are using the [latest](https://github.com/orgs/bptlab/packages?repo_name=scylla) Scylla package**
```
      <dependency>
            <groupId>de.hpi.bpt</groupId>
            <artifactId>scylla</artifactId>
            <version>0.0.1-SNAPSHOT</version>
            <type>test-jar</type>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>de.hpi.bpt</groupId>
            <artifactId>scylla</artifactId>
            <version>0.0.1-SNAPSHOT</version>
            <scope>compile</scope>
        </dependency>
```

### [For users]
1. Download the latest version of [scylla.zip](https://github.com/bptlab/scylla/releases)
2. Download our plugin package [cost_driver.zip](https://github.com/INSM-TUM-Teaching/cost_driver/releases/tag/0.0.1-SNAPSHOT)
3. Unzip them
4. Create a directory "samples" under scylla and put desire samples in
5. Double click scylla.jar and obtain the UI as step 4 above

A demo video can be found [here](https://1drv.ms/v/s!Ah3W4gQ7fV9RheVw8KaAvgLETjPC5w?e=LnzQC8).

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
The results will be shown in two different file, *.xes and *_statistic.xml. 
### Event log (enclosed with .xes)
Event log is composed by a sequence of activity instance. We put the activity cost, process cost and reference Abstract Cost Driver, Concrete Cost Driver inside so that the utilisation of resource used is clear.
```ruby
<trace>
		<string key="concept:name" value="cost[Process_Instance_ID]"/>
		<string key="cost:Process_Instance" value="[Total Cost]"/>
		<string key="cost:variant" value=[Cost Variant A]/>
		...
		<event>
			<string key="cost:driver" value=[Abstract Cost Driver(Concrete Cost Driver): [cost]]/>
			<string key="cost:driver" value=[Abstract Cost Driver(Concrete Cost Driver): [cost]]/>
			<string key="concept:name" value=[Activity]]/>
			<string key="lifecycle:transition" value="start"/>
			<date key="time:timestamp" value="2023-12-25T09:00:00+01:00"/>
			<string key="cost:activity" value=[activity cost]/>
		</event>
		...
	</trace>
```
### Aggregated sustainability information (enclosed with _statistic.xml)
The outputted file shows a complete detailed breakdown of sustainability info.
Explanation of nodes moving downwards:
```ruby
<Sustainability_Info>
    <CostVariant_1>1.4019512857142856E-4</CostVariant_1>
    ...
    <Average_Process_Instance_Cost>1.3816812000000002E-4</Average_Process_Instance_Cost>
    <Activity_Average_Cost>
        <Activity_1>
            <CostVariant_1>0.0</CostVariant_1>
            ...
            <average_activity_cost>0.0</average_activity_cost>
        </Activity_1>
        ...
    </Activity_Average_Cost>

    <Activity_Instance_Cost>
        <Activity_1>
            <CostVariant_1>
                <activity_instance_cost ProcessInstance_IDs="1, 2, 4, 6, 7, 8, 9" count="7">0.0</activity_instance_cost>
            </CostVariant_1>
            ...
        </Activity_1>
        ...
    </Activity_Instance_Cost>
</Sustainability_Info>

```

## References: <br>
Pufahl, L., & Weske, M. (January 2018). Design of an Extensible BPMN Process Simulator. Retrieved from https://www.researchgate.net/publication/322524759_Design_of_an_Extensible_BPMN_Process_Simulator?enrichId=rgreq-55dc4561329b473ce8f8871f05e56dba-XXX&enrichSource=Y292ZXJQYWdlOzMyMjUyNDc1OTtBUzo1OTU2ODQ2MDQ1MjY1OTJAMTUxOTAzMzY4NTAwNg%3D%3D&el=1_x_3&_esc=publicationCoverPdf  <br>
Ng, K. Y. (1996). An Algorithm for Acyclic State Machines. *Acta Informatica*, 33(4), 223–228. https://doi.org/10.1007/BF02986351


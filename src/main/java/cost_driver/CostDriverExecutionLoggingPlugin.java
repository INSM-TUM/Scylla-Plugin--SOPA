package cost_driver;

import de.hpi.bpt.scylla.exception.ScyllaRuntimeException;
import de.hpi.bpt.scylla.logger.ProcessNodeInfo;
import de.hpi.bpt.scylla.logger.ProcessNodeTransitionType;
import de.hpi.bpt.scylla.model.configuration.SimulationConfiguration;
import de.hpi.bpt.scylla.model.global.GlobalConfiguration;
import de.hpi.bpt.scylla.plugin_type.logger.OutputLoggerPluggable;
import de.hpi.bpt.scylla.simulation.ProcessSimulationComponents;
import de.hpi.bpt.scylla.simulation.SimulationModel;
import de.hpi.bpt.scylla.simulation.utils.DateTimeUtils;
import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.*;
import org.deckfour.xes.out.XesXmlGZIPSerializer;
import org.deckfour.xes.out.XesXmlSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class CostDriverExecutionLoggingPlugin extends OutputLoggerPluggable {

    boolean gzipOn = false;

    @Override
    public String getName() {
        return CostDriverPluginUtils.PLUGIN_NAME;
    }

    @Override
    public void writeToLog(SimulationModel model, String outputPathWithoutExtension) throws IOException {
        Map<String, ProcessSimulationComponents> desmojObjectsMap = model.getDesmojObjectsMap();
        for (String processId : desmojObjectsMap.keySet()) {
            String fileNameWithoutExtension = model.getDesmojObjectsMap().get(processId).getCommonProcessElements()
                    .getBpmnFileNameWithoutExtension();
            ZonedDateTime baseDateTime = model.getStartDateTime();
            Map<Integer, List<ProcessNodeInfo>> nodeInfos = model.getProcessNodeInfos().get(processId);

            XFactory factory = XFactoryRegistry.instance().currentDefault();
            XLog log = factory.createLog();

            List<XExtension> extensions = new ArrayList<>();
            XLifecycleExtension lifecycleExt = XLifecycleExtension.instance();
            extensions.add(lifecycleExt);
            XOrganizationalExtension organizationalExt = XOrganizationalExtension.instance();
            extensions.add(organizationalExt);
            XTimeExtension timeExt = XTimeExtension.instance();
            extensions.add(timeExt);
            XConceptExtension conceptExt = XConceptExtension.instance();
            extensions.add(conceptExt);
            log.getExtensions().addAll(extensions);

            List<XAttribute> globalTraceAttributes = new ArrayList<>();
            globalTraceAttributes.add(XConceptExtension.ATTR_NAME);
            log.getGlobalTraceAttributes().addAll(globalTraceAttributes);

            List<XAttribute> globalEventAttributes = new ArrayList<>();
            globalEventAttributes.add(XConceptExtension.ATTR_NAME);
            globalEventAttributes.add(XLifecycleExtension.ATTR_TRANSITION);
            log.getGlobalEventAttributes().addAll(globalEventAttributes);

            List<XEventClassifier> classifiers = new ArrayList<>();
            classifiers.add(new XEventAttributeClassifier("MXML Legacy Classifier", XConceptExtension.KEY_NAME,
                    XLifecycleExtension.KEY_TRANSITION));
            classifiers.add(new XEventAttributeClassifier("Event Name", XConceptExtension.KEY_NAME));
            classifiers.add(new XEventAttributeClassifier("Resource", XOrganizationalExtension.KEY_RESOURCE));
            classifiers.add(new XEventAttributeClassifier("Event Name AND Resource", XConceptExtension.KEY_NAME,
                    XOrganizationalExtension.KEY_RESOURCE));
            classifiers.add(new XEventAttributeClassifier("Cost Driver", "cost:driver"));
            classifiers.add(new XEventAttributeClassifier("Cost Variant", "cost:variant"));
            classifiers.add(new XEventAttributeClassifier("Total Cost", "total:cost"));
            log.getClassifiers().addAll(classifiers);

            log.getAttributes().put("source", factory.createAttributeLiteral("source", "Scylla", null));
            log.getAttributes().put(XConceptExtension.KEY_NAME,
                    factory.createAttributeLiteral(XConceptExtension.KEY_NAME, processId, conceptExt));
            log.getAttributes().put("description",
                    factory.createAttributeLiteral("description", "Log file created in Scylla", null));
            log.getAttributes().put(XLifecycleExtension.KEY_MODEL, XLifecycleExtension.ATTR_MODEL);

            //Preparation for adding <string key=”cost:variant” value=”standard procedure”/>
            SimulationConfiguration simulationConfiguration = desmojObjectsMap.get(processId).getSimulationConfiguration();
            CostVariantConfiguration costVariants = (CostVariantConfiguration) simulationConfiguration.getExtensionAttributes().get("cost_driver_CostVariant");
            Stack<CostVariant> costVariantStack = costVariants.getCostVariantListConfigured();

            /**
             * Preparation for Average Cost Calculation
             * Scenario -> List of total costs
             */
            Map<String, List<AtomicReference<Double>>> instancesCostVariant2TotalCostMap = new HashMap<>();


            /**
             * Preparation for average cost for each activity
             * task name -> scenario -> costs
             * */
            Map<String, Map<String, List<Double>>> averageCostEachActivityMap = new HashMap<>();

            /**
             * Preparation of writing to xml
             * */
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder;
            try {
                docBuilder = docFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                throw new RuntimeException(e);
            }
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("CostVariantAverageTime");
            doc.appendChild(rootElement);

            for (Integer processInstanceId : nodeInfos.keySet()) {
                XTrace trace = factory.createTrace();
                trace.getAttributes().put(XConceptExtension.KEY_NAME, factory
                        .createAttributeLiteral(XConceptExtension.KEY_NAME, processInstanceId.toString(), conceptExt));
                /**
                * add <string key=”cost:variant” value=”standard procedure”/>
                * */
                CostVariant costVariant = costVariantStack.pop();
                trace.getAttributes().put("cost:variant", factory
                        .createAttributeLiteral("cost:variant", costVariant.getId(), conceptExt));

                List<ProcessNodeInfo> nodeInfoList = nodeInfos.get(processInstanceId);
                Map<String, Object> nodeID2costDriversMap = (Map<String, Object>) simulationConfiguration.getExtensionAttributes().get("cost_driver_costDrivers");
                List<String> listOfCCDID = new ArrayList<>();
                AtomicReference<Double> totalCostPerInstance = new AtomicReference<>(0.0);

                for (ProcessNodeInfo info : nodeInfoList) {

                    XAttributeMap attributeMap = factory.createAttributeMap();

                    Set<String> resources = info.getResources();
                    for (String res : resources) {
                        attributeMap.put(res, factory.createAttributeLiteral(XOrganizationalExtension.KEY_RESOURCE, res,
                                organizationalExt));
                    }

                    ZonedDateTime zonedDateTime = baseDateTime.plus(info.getTimestamp(),
                            DateTimeUtils.getReferenceChronoUnit());
                    Date timestamp = new Date(zonedDateTime.toInstant().toEpochMilli());
                    attributeMap.put(XTimeExtension.KEY_TIMESTAMP,
                            factory.createAttributeTimestamp(XTimeExtension.KEY_TIMESTAMP, timestamp, timeExt));

                    String taskName = info.getTaskName();
                    attributeMap.put(XConceptExtension.KEY_NAME,
                            factory.createAttributeLiteral(XConceptExtension.KEY_NAME, taskName, conceptExt));

                    /**
                     * set processNodeInfo dataObjectField & add them into attributeMap
                     **/
                    Map<String, Object> concreteCostId2ObjectMap = new HashMap<>();

                    //Preparation for taskCost
                    Double taskCost = 0.0;

                    if (nodeID2costDriversMap.get(info.getId()) != null) {
                        listOfCCDID = (List<String>) nodeID2costDriversMap.get(info.getId());
                    } else {
                        listOfCCDID.clear();
                    }

                    if (!listOfCCDID.isEmpty()) {
                        listOfCCDID.forEach(i -> concreteCostId2ObjectMap.put(i, findConcreteCaseByCost(model.getGlobalConfiguration(), costVariant, i)));

                        info.SetDataObjectField(concreteCostId2ObjectMap); //nodeInfoList.stream().filter(i -> i.getId().equals(info.getId())).findFirst().get().SetDataObjectField(concreteCostId2ObjectMap);

                        Map<String, Object> dataObjects = info.getDataObjectField();
                        for (String d0: dataObjects.keySet()) {
                            attributeMap.put(d0, factory.createAttributeLiteral("cost:driver", d0,
                                    organizationalExt));

                            ConcreteCostDriver ccd = (ConcreteCostDriver) dataObjects.get(d0);
                            taskCost += ccd.getLCAScore();
                        }
                    }

                    ProcessNodeTransitionType transition = info.getTransition();
                    if (transition == ProcessNodeTransitionType.BEGIN
                            || transition == ProcessNodeTransitionType.EVENT_BEGIN) {
                        attributeMap.put(XLifecycleExtension.KEY_TRANSITION, factory
                                .createAttributeLiteral(XLifecycleExtension.KEY_TRANSITION, "start", lifecycleExt));
                    }
                    else if (transition == ProcessNodeTransitionType.TERMINATE
                            || transition == ProcessNodeTransitionType.EVENT_TERMINATE) {
                        attributeMap.put(XLifecycleExtension.KEY_TRANSITION, factory
                                .createAttributeLiteral(XLifecycleExtension.KEY_TRANSITION, "complete", lifecycleExt));

                        //Only add the task's cost to total cost until it completed
                        List<ConcreteCostDriver> listOfCCD = new ArrayList(concreteCostId2ObjectMap.values());
                        listOfCCD.forEach(i -> totalCostPerInstance.updateAndGet(v -> v + i.getLCAScore()));

                        //Only add the task's cost to averageCostEachActivityMap until it completed
                        if (!averageCostEachActivityMap.containsKey(taskName)) {
                            averageCostEachActivityMap.put(taskName, new HashMap<>());
                        }
                        if (!averageCostEachActivityMap.get(taskName).containsKey(costVariant.getId())) {
                            averageCostEachActivityMap.get(taskName).put(costVariant.getId(), new ArrayList<>());
                        }
                        averageCostEachActivityMap.get(taskName).get(costVariant.getId()).add(taskCost);

                    }
                    else if (transition == ProcessNodeTransitionType.CANCEL) {
                        attributeMap.put(XLifecycleExtension.KEY_TRANSITION, factory
                                .createAttributeLiteral(XLifecycleExtension.KEY_TRANSITION, "ate_abort", lifecycleExt));

                    }
                    else if (transition == ProcessNodeTransitionType.ENABLE
                            || transition == ProcessNodeTransitionType.PAUSE
                            || transition == ProcessNodeTransitionType.RESUME) {
                        continue;
                    }
                    else {
                        System.out.println("Transition type " + transition + " not supported in XESLogger.");
                    }

                    XEvent event = factory.createEvent(attributeMap);
                    trace.add(event);
                }

                /**
                 * add <string key=”total cost” value=”<LCA score>”/>
                 * */
                trace.getAttributes().put("total:cost", factory
                        .createAttributeLiteral("total:cost", String.valueOf(totalCostPerInstance), conceptExt));

                /**
                 * add total cost of each instances to instancesCostVariant2TotalCostMap
                 * */
                if (!instancesCostVariant2TotalCostMap.containsKey(costVariant.getId())) {
                    instancesCostVariant2TotalCostMap.put(costVariant.getId(), new ArrayList<>());
                }
                instancesCostVariant2TotalCostMap.get(costVariant.getId()).add(totalCostPerInstance);

                log.add(trace);
            }

            XesXmlSerializer serializer;
            FileOutputStream fos;

            /**
             * calculate average value of instances' total cost
             * */
            Map<String, Double> averageTotalCostMap = new HashMap<>();
            List<Double> instanceCosts = new ArrayList<>();
            for (String costVariant:instancesCostVariant2TotalCostMap.keySet()) {
                averageTotalCostMap.put(costVariant, instancesCostVariant2TotalCostMap.get(costVariant).stream().mapToDouble(i -> i.get()).average().orElse(0.0));
                //log.getAttributes().put(costVariant, factory.createAttributeLiteral(costVariant, String.valueOf(averageTotalCostMap.get(costVariant)), null));

                Element cv = doc.createElement(costVariant.replace(' ', '_'));
                cv.setTextContent(String.valueOf(averageTotalCostMap.get(costVariant)));
                rootElement.appendChild(cv);

                //Collect all traces average cost
                for (AtomicReference<Double> d: instancesCostVariant2TotalCostMap.get(costVariant)) instanceCosts.add(d.get());
            }

            //Calculate all traces average cost and put them into xml
            Element tcv = doc.createElement("All_Traces_Average_Cost");
            tcv.setTextContent(String.valueOf(instanceCosts.stream().mapToDouble(i -> i).average().orElse(0.0)));
            rootElement.appendChild(tcv);

            //Calculate all traces average cost per activities and put them into xml
            Element acitivityAverageCost = doc.createElement("Activity_Average_Cost");
            for (String act:averageCostEachActivityMap.keySet()) {
                Element activity = doc.createElement(act.replace(' ', '_'));
                for (String scen: averageCostEachActivityMap.get(act).keySet()) {
                    Element scenario = doc.createElement(scen.replace(' ', '_'));
                    scenario.setTextContent(String.valueOf(averageCostEachActivityMap.get(act).get(scen).stream().mapToDouble(i -> i).average().orElse(0.0)));
                    activity.appendChild(scenario);
                }
                acitivityAverageCost.appendChild(activity);
            }
            rootElement.appendChild(acitivityAverageCost);


            if (gzipOn) {
                serializer = new XesXmlGZIPSerializer();
                fos = new FileOutputStream(outputPathWithoutExtension + fileNameWithoutExtension +  ".tar");
            }
            else {
                serializer = new XesXmlSerializer();
                fos = new FileOutputStream(outputPathWithoutExtension + fileNameWithoutExtension + ".xes");
            };
            serializer.serialize(log, fos);
            fos.close();


            /***
             * Write to "XML" output file
             */
            try (FileOutputStream output =
                         new FileOutputStream(outputPathWithoutExtension + "sustainability_global_information_statistic.xml")) {
                writeXml(doc, output);
            } catch (IOException | TransformerException e) {
                e.printStackTrace();
            }
        }
    }

    private Object findConcreteCaseByCost(GlobalConfiguration globalConfiguration, CostVariant costVariant, String abstractCostDriver){

        double epsilon = 0.000001d;

        List<AbstractCostDriver> abstractCostDrivers = (List<AbstractCostDriver>) globalConfiguration.getExtensionAttributes().get("cost_driver_costDrivers");
        Double cost = costVariant.getConcretisedACD().get(abstractCostDriver);
        AbstractCostDriver costDriver = abstractCostDrivers.stream().filter(i -> i.getId().equals(abstractCostDriver)).findFirst().get();

        for (ConcreteCostDriver ccd: costDriver.getChildren()) {
            if (Math.abs(ccd.getLCAScore() - cost) < epsilon) return ccd;
        }
        throw new ScyllaRuntimeException("Can not find cost driver by its name");
    }

    private static void writeXml(Document document, OutputStream output) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(output);

        transformer.transform(source, result);
    }
}


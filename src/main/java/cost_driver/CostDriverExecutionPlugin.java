package cost_driver;


import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


import de.hpi.bpt.scylla.exception.ScyllaValidationException;
import de.hpi.bpt.scylla.model.configuration.distribution.TimeDistributionWrapper;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CostDriverExecutionPlugin {
    List<AbstractCostDriver> abstractCostDrivers;
    List<ConcreteCostDriver> concreteCostDrivers;

    public CostDriverExecutionPlugin() {
        this.abstractCostDrivers = new ArrayList<>();
        this.concreteCostDrivers = new ArrayList<>();
    }

    public static void execute() throws ParserConfigurationException, IOException, SAXException, ScyllaValidationException {

        AbstractCostDriver abstractCostDriver = new AbstractCostDriver("In-hourse mail", new ArrayList<>(), TimeUnit.HOURS);
        ConcreteCostDriver concreteCostDriver = new ConcreteCostDriver("car", abstractCostDriver, 0.3, 0.000039, new TimeDistributionWrapper(TimeUnit.HOURS));


        List<AbstractCostDriver> abstractCostDrivers = XMLParser.parseGC(XMLParser.xmlParser("hiring_process_global_1.xml"));
        List<String> concreteCostDrivers = XMLParser.parseSC(XMLParser.xmlParser("hiring_process_sim.xml")).stream().distinct().toList();
        List<AbstractCostDriver> tmp = new ArrayList<>();
        for (var i : abstractCostDrivers) {
            if (concreteCostDrivers.contains(i.getId())) {
                tmp.add(i);

//                System.out.println(i.getId());
            }
        }
        //def 6
        for (var i : tmp) {
            double activityInstanceCost = 0;
//            System.out.println(i.getId());
            for (var j : i.getChildren()) {
                activityInstanceCost += j.getLCAScore();
            }
            System.out.println(i.getId() + ": " + activityInstanceCost);
        }


        var sorted = abstractCostDrivers.stream().sorted(Comparator.comparing(
                abstractCostDriver1 -> abstractCostDriver1
                        .getChildren()
                        .stream()
                        .mapToDouble(concreteCostDriver1 -> concreteCostDriver1.LCAScore)
                        .sum()
        ));

        for (var i : sorted.toList()) {
            System.out.println(i.getId());
        }


    }

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, ScyllaValidationException {
        execute();
    }
}


class XMLParser {

    public static String xmlParser(String file) throws ParserConfigurationException, IOException, SAXException {
        Path path = Path.of("./samples/" + file);
        return Files.readString(path);
    }

    /**
     *
     */
    public static List<String> parseSC(String xmlContent) {
        List<String> abstractCostDrivers = new ArrayList<>();
        try {
            // Create a DocumentBuilder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parse the XML content
            ByteArrayInputStream input = new ByteArrayInputStream(xmlContent.trim().getBytes("UTF-8"));
            Document document = builder.parse(input);

            document.getDocumentElement().normalize();

            // Get the root element
            Element root = document.getDocumentElement();

            // Parse the costDriver section
            NodeList tasks = root.getElementsByTagName("bsim:Task");
            if (tasks.getLength() > 0) {
                for (int i = 0; i < tasks.getLength(); i++) {
                    Element currentTask = (Element) tasks.item(i);
                    String taskId = currentTask.getAttribute("id");
                    String taskName = currentTask.getAttribute("name");

                    for (int j = 0; j < currentTask.getChildNodes().getLength(); j += 1) {
                        if (currentTask.getChildNodes().item(j).getNodeName().equals("bsim:costDrivers")) {
                            Element curr = (Element) currentTask.getChildNodes().item(j).getChildNodes().item(1);
                            String costDriverId = curr.getAttribute("id");
//                            System.out.println(costDriverId);
                            abstractCostDrivers.add(costDriverId);
                        }
                    }
                }
            }
        } catch (ParserConfigurationException | IOException | NumberFormatException | SAXException e) {
            e.printStackTrace();
        }
        return abstractCostDrivers;
    }


    /**
     * @param xmlContent The global config file in ./samples/
     *                   <p>
     *                   Gives a Map of all costDrivers in the global configuration
     */

    public static List<AbstractCostDriver> parseGC(String xmlContent) {
        List<AbstractCostDriver> abstractCostDrivers = new ArrayList<>();
        try {
            // Create a DocumentBuilder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parse the XML content
            ByteArrayInputStream input = new ByteArrayInputStream(xmlContent.getBytes("UTF-8"));
            Document document = builder.parse(input);

            document.getDocumentElement().normalize();

            // Get the root element
            Element root = document.getDocumentElement();

            // Parse the costDriver section
            NodeList costDrivers = root.getElementsByTagName("bsim:costDriver");
            if (costDrivers.getLength() > 0) {
                for (int i = 1; i < costDrivers.item(0).getChildNodes().getLength(); i += 2) {
                    Element abstractCostDriver = (Element) costDrivers.item(0).getChildNodes().item(i);
                    String abstractCostDriverId = abstractCostDriver.getAttribute("id");

                    AbstractCostDriver abstractCostDriverTMP = new AbstractCostDriver(abstractCostDriverId,
                            new ArrayList<>(),
                            TimeUnit.HOURS
                    );

                    for (int j = 1; j < abstractCostDriver.getChildNodes().getLength(); j += 2) {

                        Element concreteCostDriver = (Element) abstractCostDriver.getChildNodes().item(j);
                        String concreteCostDriverID = concreteCostDriver.getAttribute("id");
                        double LCAScores = Double.parseDouble(concreteCostDriver.getAttribute("LCAScores"));

                        for (int k = 1; k < concreteCostDriver.getChildNodes().getLength(); k += 2) {

                            if (concreteCostDriver.getChildNodes().item(k).getNodeName().equals("bsim:probability")) {
                                Double probability = Double.parseDouble(concreteCostDriver.getChildNodes().item(k).getTextContent());

                                //New approach
                                ConcreteCostDriver tmp = new ConcreteCostDriver(concreteCostDriverID,
                                        abstractCostDriverTMP,
                                        probability,
                                        LCAScores,
                                        new TimeDistributionWrapper(TimeUnit.HOURS)
                                );
                                abstractCostDriverTMP.addChild(tmp);

                            }
                        }

                    }
                    //New approach
                    abstractCostDrivers.add(abstractCostDriverTMP);
                }
            }
        } catch (ParserConfigurationException | IOException | NumberFormatException | SAXException e) {
            e.printStackTrace();
        } catch (ScyllaValidationException e) {
            throw new RuntimeException(e);
        }
        return abstractCostDrivers;
    }


}




/*
       public static List<AbstractCostDriver> parseGC(String xmlContent) {
//        HashMap<String, Object> abstractCostDriversMap = new HashMap<>();
        //New approach
        List<AbstractCostDriver> abstractCostDrivers = new ArrayList<>();
        try {
            // Create a DocumentBuilder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parse the XML content
            ByteArrayInputStream input = new ByteArrayInputStream(xmlContent.getBytes("UTF-8"));
            Document document = builder.parse(input);

            document.getDocumentElement().normalize();

            // Get the root element
            Element root = document.getDocumentElement();

            // Parse the costDriver section
            NodeList costDrivers = root.getElementsByTagName("bsim:costDriver");
            if (costDrivers.getLength() > 0) {
                for (int i = 1; i < costDrivers.item(0).getChildNodes().getLength(); i += 2) {
                    Element abstractCostDriver = (Element) costDrivers.item(0).getChildNodes().item(i);
                    String abstractCostDriverId = abstractCostDriver.getAttribute("id");

                    //Trying new approach
                    AbstractCostDriver abstractCostDriverTMP = new AbstractCostDriver(abstractCostDriverId,
                            new ArrayList<>(),
                            TimeUnit.HOURS
                    );
                    //


                    List<HashMap<Object, Double>> costDriversProbList = new ArrayList<>();
                    for (int j = 1; j < abstractCostDriver.getChildNodes().getLength(); j += 2) {
//                        HashMap<String, Double> costDriverLCA = new HashMap<>();

                        Element concreteCostDriver = (Element) abstractCostDriver.getChildNodes().item(j);
                        String concreteCostDriverID = concreteCostDriver.getAttribute("id");
                        double LCAScores = Double.parseDouble(concreteCostDriver.getAttribute("LCAScores"));

//                        costDriverLCA.put(concreteCostDriverID, LCAScores);
                        //For the current abstractCostDriver
                        for (int k = 1; k < concreteCostDriver.getChildNodes().getLength(); k += 2) {

//                            HashMap<Object, Double> costDriverProb = new HashMap<>();
                            if (concreteCostDriver.getChildNodes().item(k).getNodeName().equals("bsim:probability")) {
                                Double probability = Double.parseDouble(concreteCostDriver.getChildNodes().item(k).getTextContent());

                                //New approach
                                ConcreteCostDriver tmp = new ConcreteCostDriver(concreteCostDriverID,
                                        abstractCostDriverTMP,
                                        probability,
                                        LCAScores,
                                        new TimeDistributionWrapper(TimeUnit.HOURS)
                                );
                                abstractCostDriverTMP.addChild(tmp);
                                //


//                                costDriverProb.put(costDriverLCA, probability);
//                                costDriversProbList.add(costDriverProb);
                            }
                        }

                    }
                    //New approach
                    abstractCostDrivers.add(abstractCostDriverTMP);
                    //

//                    abstractCostDriversMap.put(abstractCostDriverId, costDriversProbList);
                }
            }
        } catch (ParserConfigurationException | IOException | NumberFormatException | SAXException e) {
            e.printStackTrace();
        } catch (ScyllaValidationException e) {
            throw new RuntimeException(e);
        }
        return abstractCostDrivers;
    }
*/








/*


for (String abstractCostDriver : abstractCostDrivers) {
        List<HashMap<Object, Double>> listOfCostDrivers = (List<HashMap<Object, Double>>) globalConfiguration.get(abstractCostDriver);

        for (var costDriver : listOfCostDrivers) {
        HashMap<String, Double> tmp = (HashMap<String, Double>) costDriver.keySet().stream().findFirst().orElse(new HashMap<>());
        var children = new ArrayList<>();
        if (tmp != null) {
        String concreteDriver = tmp.keySet().toString().substring(1, tmp.keySet().toString().length()-1);

//                    children.add(new ConcreteCostDriver(concreteDriver, new AbstractCostDriver(abstractCostDriver, )))
        //                    System.out.println(tmp.keySet());
//                    var driver = new AbstractCostDriver(abstractCostDriver, )
        // Additional processing or printing can be added here if needed
        }
        }

        System.out.println();
//            var tmp =  Arrays.stream(costDriverList.substring(1, costDriverList.length()-1).split(",")).toList();

        }

*/

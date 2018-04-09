package com.opencircuit.streamline.engine;

import com.google.common.io.Resources;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

class Common {

    String getExecutionDirectory() {
        return System.getProperty("user.dir");
    }

    private NodeList retrieveXmlNodesList(String filePath, String nodeName) {

        NodeList primaryNodeList = null;

        try {

            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();

            Document document = documentBuilder.parse(filePath);
            document.getDocumentElement().normalize();
            primaryNodeList = document.getElementsByTagName(nodeName);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return primaryNodeList;
    }

    NodeList retrieveXmlChildNodesList(String filePath, String parentNodeName) {

        NodeList childNodeList = null;

        try {

            NodeList parentNodeList = retrieveXmlNodesList(filePath, parentNodeName);
            Node primaryNode = parentNodeList.item(0);
            childNodeList = primaryNode.getChildNodes();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return childNodeList;
    }

    HashMap<Integer, HashMap<String, String>> retrieveXmlNodesDictionary(String filePath, String parentNodeName) {

        HashMap<Integer, HashMap<String, String>> nodeDictionary;
        nodeDictionary = new HashMap<>();

        try {

            NodeList parentNodeList = retrieveXmlNodesList(filePath, parentNodeName);

            for (int i = 0; i < parentNodeList.getLength(); i++) {

                Node primaryNode = parentNodeList.item(i);
                Element primaryElement = (Element) primaryNode;
                String attributeValue = primaryElement.getAttribute("id");
                int stepNumber = Integer.parseInt(attributeValue);

                HashMap<String, String> childNodesDictionary;
                childNodesDictionary = generateChildNodeDictionary(primaryNode);
                nodeDictionary.put(stepNumber, childNodesDictionary);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return nodeDictionary;
    }

    private HashMap<String, String> generateChildNodeDictionary(Node primaryNode) {

        HashMap<String, String> childNodesDictionary = new HashMap<>();
        NodeList childNodeList = primaryNode.getChildNodes();

        for (int j = 0; j < childNodeList.getLength(); j++) {

            String nodeName = childNodeList.item(j).getNodeName();
            String nodeValue = childNodeList.item(j).getTextContent();

            if (!nodeName.equalsIgnoreCase("#text")) {
                childNodesDictionary.put(nodeName, nodeValue);
            }
        }

        return childNodesDictionary;
    }

    void createDirectories(String directoryPath) {

        File files = new File(directoryPath);
        if (!files.exists()) { files.mkdirs(); }
    }

    String getCurrentDateTime() {

        String format = "MMddyyyy-HHmmss";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        return simpleDateFormat.format(timestamp);
    }

    void extractResourceToDirectory(String extractionDirectory, String resourceName, String extractedResourceName) {

        try {

            File file = new File(extractionDirectory + "\\" + extractedResourceName);

            if (!file.exists()) {
                URL resource = Resources.getResource(resourceName);
                FileUtils.copyURLToFile(resource, file);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void terminateProcessesFromTaskManager(ArrayList<String> taskList) {

        try {

            for(String task : taskList) {
                Runtime.getRuntime().exec("taskkill /F /IM " + task);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean sleepThreadInSeconds(String sleepTimeInSeconds) {

        boolean stepSuccessful = false;

        try {

            int waitTime = Integer.parseInt(sleepTimeInSeconds);
            waitTime = waitTime * 1000;
            Thread.sleep(waitTime);
            stepSuccessful = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stepSuccessful;
    }

    boolean sleepThreadInMilliseconds(String sleepTimeInMilliseconds) {

        boolean stepSuccessful = false;

        try {

            int waitTime = Integer.parseInt(sleepTimeInMilliseconds);
            waitTime = waitTime * 1000;
            Thread.sleep(waitTime);
            stepSuccessful = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stepSuccessful;
    }
}
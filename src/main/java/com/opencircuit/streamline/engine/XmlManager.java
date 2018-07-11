package com.opencircuit.streamline.engine;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.HashMap;

class XmlManager {

    private NodeList retrieveXmlNodesList(String filePath, String nodeName) throws Exception {

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();

        Document document = documentBuilder.parse(filePath);
        document.getDocumentElement().normalize();
        return document.getElementsByTagName(nodeName);
    }

    HashMap<String, String> retrieveExecutionSettings(String filePath) {

        try {

            NodeList nodeList = retrieveXmlNodesList(filePath, "Settings").item(0).getChildNodes();
            return retrieveSubNodesDictionary(nodeList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    ArrayList<String> retrieveComponentList(String filePath) {

        try {

            NodeList nodeList = retrieveXmlNodesList(filePath, "Component");
            return createComponentList(nodeList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private ArrayList<String> createComponentList(NodeList nodeList) {

        ArrayList<String> componentList = new ArrayList<>();

        for (int nodeIndex = 0; nodeIndex < nodeList.getLength(); nodeIndex++) {
            addComponentToList(nodeList, componentList, nodeIndex);
        }

        return componentList;
    }

    private void addComponentToList(NodeList nodeList, ArrayList<String> componentList, int nodeIndex) {

        String nodeName = nodeList.item(nodeIndex).getNodeName();
        if (nodeName.equalsIgnoreCase("#text")) { return; }
        componentList.add(retrieveNodeValueAttribute(nodeList.item(nodeIndex)));
    }

    private String retrieveNodeValueAttribute(Node node) {

        Element element = (Element) node;
        return element.getAttribute("value");
    }

    ArrayList<HashMap<String, String>> retrieveStepDictionaryList(String filePath) {

        try {

            NodeList nodeList = retrieveXmlNodesList(filePath, "Step");
            return createStepDictionaryList(nodeList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private ArrayList<HashMap<String, String>> createStepDictionaryList(NodeList nodeList) {

        ArrayList<HashMap<String, String>> stepDictionaryList = new ArrayList<>();

        for (int nodeIndex = 0; nodeIndex < nodeList.getLength(); nodeIndex++) {
            createStepDictionary(nodeList.item(nodeIndex), stepDictionaryList);
        }

        return stepDictionaryList;
    }

    private void createStepDictionary(Node item, ArrayList<HashMap<String, String>> stepDictionaryList) {

        NodeList childNodeList = item.getChildNodes();
        stepDictionaryList.add(retrieveSubNodesDictionary(childNodeList));
    }

    private HashMap<String, String> retrieveSubNodesDictionary(NodeList nodeList) {

        HashMap<String, String> dictionary = new HashMap<>();

        for (int nodeIndex = 0; nodeIndex < nodeList.getLength(); nodeIndex++) {
            addNodeNameAndValueToDictionary(nodeList.item(nodeIndex), dictionary);
        }

        return dictionary;
    }

    private void addNodeNameAndValueToDictionary(Node item, HashMap<String, String> dictionary) {

        String nodeName = item.getNodeName();

        if (nodeName.equalsIgnoreCase("#text")) {
            return;
        }

        dictionary.put(nodeName, item.getTextContent());
    }
}
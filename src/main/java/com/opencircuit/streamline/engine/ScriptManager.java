package com.opencircuit.streamline.engine;

import java.util.ArrayList;

class ScriptManager {

    private DataManager dataManager = new DataManager();
    private StepDetailsManager stepDetailsManager = new StepDetailsManager();
    private ArrayList<String> componentList = new ArrayList<>();
    private final String testCaseID;
    private final String scriptDirectory;
    private boolean continueExecution;

    ScriptManager(String testCaseID, String scriptDirectory) {

        this.testCaseID = testCaseID;
        this.scriptDirectory = scriptDirectory;
        continueExecution = true;
    }

    void loadComponentList() {

        XmlManager xmlManager = new XmlManager();
        componentList = xmlManager.retrieveComponentList(scriptDirectory + "Script.xml");
    }

    void loadTestCaseData() {

        dataManager.loadTestCaseData(scriptDirectory, testCaseID);
    }

    void executeComponents() {

        for (String componentName : componentList) {

            if (!continueExecution) { break; }
            executeComponent(componentName);
            stepDetailsManager.resetStepNumber();
        }
    }

    private void executeComponent(String componentName) {

        ComponentManager componentManager = getComponentHandlerInstance(componentName);
        componentManager.synchronizeInstances(dataManager, stepDetailsManager);
        componentManager.loadComponentSteps();
        componentManager.executeComponentSteps();
        getContinueExecutionStatus(componentManager);
    }

    private ComponentManager getComponentHandlerInstance(String componentName) {

        String componentsDirectory = scriptDirectory + "Components\\";
        return new ComponentManager(componentsDirectory, componentName);
    }

    private void getContinueExecutionStatus(ComponentManager componentManager) {

        continueExecution = componentManager.getContinueExecutionStatus();
    }
}
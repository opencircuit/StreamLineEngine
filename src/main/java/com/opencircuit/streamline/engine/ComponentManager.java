package com.opencircuit.streamline.engine;

import java.util.ArrayList;
import java.util.HashMap;

class ComponentManager {

    private final ThreadManager threadManager = new ThreadManager();
    private final BrowserManager browserManager = new BrowserManager();
    private final ElementHandler elementHandler = new ElementHandler();
    private StepDetailsManager stepDetailsManager;
    private DataManager dataManager;

    private final String componentName;
    private final String componentPath;
    private ArrayList<HashMap<String, String>> stepDictionaryList;
    private boolean continueExecution;

    ComponentManager(String componentsDirectory, String componentName) {

        this.componentName = componentName;
        this.componentPath = componentsDirectory + componentName + ".xml";
        continueExecution = true;
    }

    void synchronizeInstances(DataManager dataManager, StepDetailsManager stepDetailsManager) {

        this.stepDetailsManager = stepDetailsManager;
        this.dataManager = dataManager;
    }

    void loadComponentSteps() {

        XmlManager xmlManager = new XmlManager();
        stepDictionaryList = xmlManager.retrieveStepDictionaryList(componentPath);
    }

    void executeComponentSteps() {

        for (HashMap<String, String> step : stepDictionaryList) {

            if (!continueExecution) { break; }
            if (!getStepConditionStatus(step)) { continue; }
            executeStep(step);
        }
    }

    private boolean getStepConditionStatus(HashMap<String, String> step) {

        return dataManager.compareTableDataToProvidedDataValue(
                step.get("ConditionDataTable"),
                step.get("ConditionDataColumn"),
                step.get("ConditionDataValue"));
    }

    private void executeStep(HashMap<String, String> step) {

        setElementProperties(step);
        String dataValue = getDataValue(step);
        boolean stepPassed = runStepAction(step, dataValue);
        finalizeStepExecutionDetails(step, stepPassed);
    }

    private void setElementProperties(HashMap<String, String> step) {

        elementHandler.setIdentifierType(step.get("IdentifierType"));
        elementHandler.setIdentifierValue(step.get("IdentifierValue"));
    }

    private String getDataValue(HashMap<String, String> step) {

        String tableName = step.get("TestDataTable");
        String columnName = step.get("TestDataColumn");
        String dataValue = step.get("StaticDataValue");
        String dictionaryKey = dataManager.getDictionaryKey(tableName, columnName);
        return dataManager.getDictionaryKeyValue(dictionaryKey, dataValue);
    }

    private boolean runStepAction(HashMap<String, String> step, String dataValue) {

        String action = step.get("Action");
        String screenshotName = null;
        boolean stepPassed;

        switch (action) {

            case "Navigate To URL":
                stepPassed = browserManager.navigateToUrl(dataValue);
                break;

            case "Switch To Window":
                stepPassed = browserManager.switchToWindow(dataValue);
                break;

            case "Close Window":
                stepPassed = browserManager.closeWindow();
                break;

            case "Switch To iFrame By Name":
                stepPassed = browserManager.switchToFrameByName(dataValue);
                break;

            case "Switch To iFrame By Index":
                stepPassed = browserManager.switchToFrameByIndex(dataValue);
                break;

            case "Switch To Default Content":
                stepPassed = browserManager.switchToDefaultContent();
                break;

            case "Type Text":
                stepPassed = elementHandler.typeText(dataValue);
                break;

            case "Clear Text":
                stepPassed = elementHandler.clearText();
                break;

            case "Match Text In Textbox":
                stepPassed = elementHandler.matchTextboxText(dataValue);
                break;

            case "Select Dropdown Value By Visible Text":
                stepPassed = elementHandler.selectDropdownValueByVisibleText(dataValue);
                break;

            case "Select Dropdown Value":
                stepPassed = elementHandler.selectDropdownValue(dataValue);
                break;

            case "Select Dropdown Value By Index":
                stepPassed = elementHandler.selectDropdownValueByIndex(dataValue);
                break;

            case "Select Radio Button By Value":
                stepPassed = elementHandler.selectRadioButtonByValue(dataValue);
                break;

            case "Select Radio Button By Index":
                stepPassed = elementHandler.selectRadioButtonByIndex(dataValue);
                break;

            case "Click Element":
                stepPassed = elementHandler.clickElement();
                break;

            case "Match Text In Element":
                stepPassed = elementHandler.matchTextInElement(dataValue);
                break;

            case "Verify Element Is Displayed":
                stepPassed = elementHandler.verifyElementState("Displayed", step.get("WaitTime"));
                break;

            case "Verify Element Is Enabled":
                stepPassed = elementHandler.verifyElementState("Enabled", step.get("WaitTime"));
                break;

            case "Verify Element Is Selected":
                stepPassed = elementHandler.verifyElementState("Selected", step.get("WaitTime"));
                break;

            case "Verify Element Is NOT Displayed":
                stepPassed = elementHandler.verifyElementIsNotDisplayed();
                break;

            case "Verify Element Is NOT Enabled":
                stepPassed = elementHandler.verifyElementIsNotEnabled();
                break;

            case "Verify Element Is NOT Selected":
                stepPassed = elementHandler.verifyElementIsNotSelected();
                break;

            case "Verify Page Loading Is Complete":
                stepPassed = elementHandler.verifyPageLoadIsComplete(step.get("WaitTime"));
                break;

            case "Find Text Displayed On Page":
                stepPassed = elementHandler.findText(dataValue, step.get("WaitTime"));
                break;

            case "Take Screenshot":
                screenshotName = browserManager.takeScreenshot(dataValue);
                stepPassed = true;
                break;

            case "Accept Alert":
                stepPassed = elementHandler.acceptAlert();
                break;

            case "Dismiss Alert":
                stepPassed = elementHandler.dismissAlert();
                break;

            case "Match Alert Text":
                stepPassed = elementHandler.matchAlertText(dataValue);
                break;

            case "Press Enter":
                stepPassed = elementHandler.pressKey(action);
                break;

            case "Press Tab":
                stepPassed = elementHandler.pressKey(action);
                break;

            case "Press Shift":
                stepPassed = elementHandler.pressKey(action);
                break;

            case "Press Space":
                stepPassed = elementHandler.pressKey(action);
                break;

            case "Press Backspace":
                stepPassed = elementHandler.pressKey(action);
                break;

            case "Press Pause":
                stepPassed = elementHandler.pressKey(action);
                break;

            case "Press Insert":
                stepPassed = elementHandler.pressKey(action);
                break;

            case "Press Home":
                stepPassed = elementHandler.pressKey(action);
                break;

            case "Press Delete":
                stepPassed = elementHandler.pressKey(action);
                break;

            case "Press End":
                stepPassed = elementHandler.pressKey(action);
                break;

            case "Press Escape":
                stepPassed = elementHandler.pressKey(action);
                break;

            case "Wait For X Seconds":
                stepPassed = threadManager.sleepThreadForSpecifiedSeconds(step.get("WaitTime"));
                break;

            case "Wait For X Milliseconds":
                stepPassed = threadManager.sleepThreadForSpecifiedMilliseconds(step.get("WaitTime"));
                break;

            case "Update Table Column":
                stepPassed = dataManager.setDictionaryKeyValue
                        (step.get("TestDataTable"), step.get("TestDataColumn"), step.get("StaticDataValue"));
                break;

            case "Compare Data Values":
                stepPassed = dataManager.compareTableDataToProvidedDataValue
                        (step.get("TestDataTable"), step.get("TestDataColumn"), step.get("StaticDataValue"));
                break;

            default:
                stepPassed = false;
                break;
        }

        setStepScreenshot(step, screenshotName);
        return stepPassed;
    }

    private void setStepScreenshot(HashMap<String, String> step, String screenshotName) {

        if (screenshotName == null) {
            step.put("ScreenshotName", "");
        } else {
            step.put("ScreenshotName", screenshotName);
        }
    }

    private void finalizeStepExecutionDetails(HashMap<String, String> step, boolean stepPassed) {

        String status = getStepStatus(step.get("OptionalStep"), stepPassed);
        setAddtionalStepValues(step, status);
        setContinueExecutionStatus(status);
        stepDetailsManager.logStepDetailsForReport(step, status);
    }

    private String getStepStatus(String stepOptionalFlag, boolean stepPassed) {

        boolean stepOptional = getStepOptionalStatus(stepOptionalFlag);
        String status;

        if (!stepPassed && stepOptional) {
            status = "Skipped";
        } else if (!stepPassed) {
            status = "Failed";
        } else {
            status = "Passed";
        }

        return status;
    }

    private void setAddtionalStepValues(HashMap<String, String> step, String status) {

        step.put("Status", status);
        step.put("ComponentName", componentName);
    }

    private boolean getStepOptionalStatus(String stepOptionalFlag) {

        return stepOptionalFlag.equalsIgnoreCase("Yes");
    }

    private void setContinueExecutionStatus(String status) {

        continueExecution = status.equalsIgnoreCase("Passed")
                || status.equalsIgnoreCase("Skipped");
    }

    boolean getContinueExecutionStatus() {
        return continueExecution;
    }
}
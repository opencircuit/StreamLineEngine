package com.opencircuit.streamline.engine;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.NodeList;

class ScriptHandler {

    //******************************************************************************************************************
    //*     Setup - Do Not Alter
    //******************************************************************************************************************

    private Common common = new Common();
    private BrowserDriver browserDriver;

    static HashMap<String, String> executionDetailsDictionary;
    static HashMap<Integer, HashMap<String, String>> stepsDictionary;
    static HashMap<Integer, String> statusDictionary;
    static HashMap<Integer, String> screenshotDictionary;
    static String finalStatus;
    static String failedStep;
    static long startTime;

    private String testCaseID;
    private String resourcesDirectory;
    private String executionDetailsXmlPath;
    private HashMap<String, String> dataDictionary;


    ScriptHandler(String testCaseID) {
        this.testCaseID = testCaseID;
    }

    //******************************************************************************************************************
    //*     Protected Methods
    //******************************************************************************************************************

    void initializeSetup() {

        String currentDirectory = common.getExecutionDirectory();
        resourcesDirectory = currentDirectory + "\\Resources";
        executionDetailsXmlPath = resourcesDirectory + "\\Setup\\ExecutionInformation.xml";
        executionDetailsDictionary = new HashMap<>();
        statusDictionary = new HashMap<>();
        screenshotDictionary = new HashMap<>();
        finalStatus = "Pass";
        failedStep = "";
        startTime = System.currentTimeMillis();
    }

    void initializeExecutionDetailsDictionary() {

        String parentNodeName = "Execution";
        NodeList nodesList;
        nodesList = common.retrieveXmlChildNodesList(executionDetailsXmlPath, parentNodeName);

        for (int nodeIndex = 0; nodeIndex < nodesList.getLength(); nodeIndex++) {

            String nodeName = nodesList.item(nodeIndex).getNodeName();
            String nodeValue = nodesList.item(nodeIndex).getTextContent();

            if (!nodeName.equalsIgnoreCase("#text")) {
                executionDetailsDictionary.put(nodeName, nodeValue);
            }
        }
    }

    void initializeDriversFoldesCreations() {

        common.createDirectories(resourcesDirectory + "\\Drivers");
        common.createDirectories(resourcesDirectory + "\\Drivers\\32-bit");
        common.createDirectories(resourcesDirectory + "\\Drivers\\64-bit");
    }

    void initializeExtractDrivers() {

        String driversPath = resourcesDirectory + "\\Drivers";
        String driversPath32Bit = resourcesDirectory + "\\Drivers\\32-bit";
        String driversPath64Bit = resourcesDirectory + "\\Drivers\\64-bit";

        String geckoDriver = "geckodriver.exe";
        String ieDriver = "IEDriverServer.exe";

        String chromeDriver = "chromedriver.exe";
        common.extractResourceToDirectory(driversPath, chromeDriver, chromeDriver);

        String geckoDriver32Bit = "geckodriver32.exe";
        common.extractResourceToDirectory(driversPath32Bit, geckoDriver32Bit, geckoDriver);

        String geckoDriver64Bit = "geckodriver64.exe";
        common.extractResourceToDirectory(driversPath64Bit, geckoDriver64Bit, geckoDriver);

        String ieDriver32Bit = "IEDriverServer32.exe";
        common.extractResourceToDirectory(driversPath32Bit, ieDriver32Bit, ieDriver);

        String ieDriver64Bit = "IEDriverServer64.exe";
        common.extractResourceToDirectory(driversPath64Bit, ieDriver64Bit, ieDriver);
    }

    void initializeDataLoading() {

        String databaseDirectory = executionDetailsDictionary.get("TestDatabaseDirectory");
        String databasePath = databaseDirectory + "\\TestCaseData.db";
        DatabaseConnector databaseConnector = new DatabaseConnector();
        databaseConnector.establishConnection(databasePath);
        dataDictionary = databaseConnector.executeTestCaseDataSelectQuery(testCaseID);
        databaseConnector.closeConnection();
    }

    void initializeScriptLoader() {

        String scriptPath = executionDetailsDictionary.get("ScriptsDirectory");
        scriptPath = scriptPath + "\\" + dataDictionary.get("MAIN.SCRIPT_NAME");
        scriptPath = scriptPath.toUpperCase() + ".xml";
        String parentNodeName = "Step";

        stepsDictionary = common.retrieveXmlNodesDictionary(scriptPath, parentNodeName);
    }

    void initializeProcessCleanup() {

        ArrayList<String> taskList = new ArrayList<>();
        taskList.add("chromedriver.exe");
        taskList.add("geckodriver.exe");
        taskList.add("IEDriverServer.exe");

        common.terminateProcessesFromTaskManager(taskList);
    }

    void initializeBrowserInvocation() {

        String resultsDirectory = executionDetailsDictionary.get("ResultsFileDirectory");
        String executionID = executionDetailsDictionary.get("ExecutionID");
        String screenshotDirectory = resultsDirectory + "\\" + executionID + "\\Screenshots\\";
        browserDriver = new BrowserDriver(screenshotDirectory);

        String browserName = executionDetailsDictionary.get("BrowserName");
        String driversDirectory = resourcesDirectory + "\\Drivers\\";
        browserDriver.invokeBrowser(browserName, driversDirectory);
    }

    void executeScriptSteps() {

        boolean continueExecution;

        for (int i = 0; i < stepsDictionary.size(); i++) {

            int stepNumber = i + 1;
            HashMap<String, String> stepNodes;
            stepNodes = stepsDictionary.get(stepNumber);

            String optionalStep;
            boolean conditionMatches;
            boolean stepSuccessful;
            String status = "Skip";

            optionalStep = stepNodes.get("OptionalStep");
            conditionMatches = verifyConditionsMatch(stepNodes);

            if (conditionMatches) {
                stepSuccessful = executeStepAction(stepNodes, stepNumber);
                status = validationExecutionStatus(optionalStep, stepSuccessful);
            }

            statusDictionary.put(stepNumber, status);
            continueExecution = !status.equalsIgnoreCase("Fail");

            if (status.equalsIgnoreCase("Fail")) {
                finalStatus = "Fail";
                failedStep = "Failed On Step Number: " + stepNumber;
            }

            if (!continueExecution) { break; }
        }
    }

    void closeBrowser() {

        String debugModeOn = executionDetailsDictionary.get("DebugModeOn");

        if (debugModeOn.equalsIgnoreCase("No")) {
            browserDriver.quitBrowser();
        }
    }

    //******************************************************************************************************************
    //*     Private Methods
    //******************************************************************************************************************

    private boolean verifyConditionsMatch(HashMap<String, String> stepNodes) {

        boolean conditionMatches;
        String conditionDataTable = stepNodes.get("ConditionDataTable");
        String conditionDataColumn = stepNodes.get("ConditionDataColumn");
        String conditionDataValue = stepNodes.get("ConditionDataValue");

        String dictionaryKey = conditionDataTable + "." + conditionDataColumn;
        String retrievedConditionalValue = dataDictionary.get(dictionaryKey);
        if (retrievedConditionalValue == null) { retrievedConditionalValue = ""; }

        conditionMatches = retrievedConditionalValue.equalsIgnoreCase(conditionDataValue);

        return conditionMatches;
    }

    private boolean executeStepAction(HashMap<String, String> stepNodes, int stepNumber) {

        String action = stepNodes.get("Action");
        String identifierType = stepNodes.get("IdentifierType");
        String identifierValue = stepNodes.get("IdentifierValue");
        String testDataTable = stepNodes.get("TestDataTable");
        String testDataColumn = stepNodes.get("TestDataColumn");
        String dataValue;
        boolean stepSuccessful;

        if (testDataTable.length() > 0 && testDataColumn.length() > 0) {
            String dictionaryKey = testDataTable + "." + testDataColumn;
            dataValue = dataDictionary.get(dictionaryKey);
        } else {
            dataValue = stepNodes.get("StaticDataValue");
        }

        browserDriver.setIdentifierType(identifierType);
        browserDriver.setIdentifierValue(identifierValue);
        browserDriver.setDataValue(dataValue);

        switch (action) {

            case "Navigate To URL":
                stepSuccessful = browserDriver.navigateToUrl();
                break;

            case "Switch To Window":
                stepSuccessful = browserDriver.switchToWindow();
                break;

            case "Close Window":
                stepSuccessful = browserDriver.closeWindow();
                break;

            case "Switch To iFrame":
                stepSuccessful = browserDriver.switchToFrame();
                break;

            case "Type Text":
                stepSuccessful = browserDriver.typeText();
                break;

            case "Clear Text":
                stepSuccessful = browserDriver.clearText();
                break;

            case "Match Text In Textbox":
                stepSuccessful = browserDriver.matchTextboxText();
                break;

            case "Select By Visible Text":
                stepSuccessful = browserDriver.selectByVisibleText();
                break;

            case "Select By Value":
                stepSuccessful = browserDriver.selectByValue();
                break;

            case "Select By Index":
                stepSuccessful = browserDriver.selectByIndex();
                break;

            case "Select Radio Button By Value":
                stepSuccessful = browserDriver.selectRadioButtonByValue();
                break;

            case "Click Element":
                stepSuccessful = browserDriver.clickElement();
                break;

            case "Match Text In Element":
                stepSuccessful = browserDriver.matchTextInElement();
                break;

            case "Verify Element Is Displayed":
                stepSuccessful = browserDriver.verifyElementIsDisplayed();
                break;

            case "Verify Element Is Enabled":
                stepSuccessful = browserDriver.verifyElementIsEnabled();
                break;

            case "Verify Element Is Selected":
                stepSuccessful = browserDriver.verifyElementIsSelected();
                break;

            case "Verify Element Is NOT Displayed":
                stepSuccessful = browserDriver.verifyElementIsNotDisplayed();
                break;

            case "Verify Element Is NOT Enabled":
                stepSuccessful = browserDriver.verifyElementIsNotEnabled();
                break;

            case "Verify Element Is NOT Selected":
                stepSuccessful = browserDriver.verifyElementIsNotSelected();
                break;

            case "Verify Page Loading Is Complete":
                stepSuccessful = browserDriver.verifyPageLoadIsComplete(dataValue);
                break;

            case "Find Text Displayed On Page":
                stepSuccessful = browserDriver.findTextDisplayedOnPage();
                break;

            case "Take Screenshot":
                stepSuccessful = browserDriver.takeScreenShot();
                String screenshotName = browserDriver.getScreenshotName();
                screenshotDictionary.put(stepNumber, screenshotName);
                break;

            case "Accept Alert":
                stepSuccessful = browserDriver.acceptAlert();
                break;

            case "Dismiss Alert":
                stepSuccessful = browserDriver.dismissAlert();
                break;

            case "Match Alert Text":
                stepSuccessful = browserDriver.matchAlertText();
                break;

            case "Press Enter":
                stepSuccessful = browserDriver.pressKey(action);
                break;

            case "Wait For X Seconds":
                stepSuccessful = common.sleepThreadInSeconds(dataValue);
                break;

            case "Wait For X Milliseconds":
                stepSuccessful = common.sleepThreadInMilliseconds(dataValue);
                break;

            default:
                System.err.println("<<<===--- Action '" + action + "' does not exist. ---===>>>");
                stepSuccessful = false;
                break;
        }

        return stepSuccessful;
    }

    private String validationExecutionStatus(String stepOptional, boolean stepSuccessful) {

        String status;

        if (stepOptional.equalsIgnoreCase("Yes") && stepSuccessful) {
            status = "Pass";
        } else if (stepOptional.equalsIgnoreCase("Yes") && !stepSuccessful) {
            status = "Skip";
        } else if (!stepOptional.equalsIgnoreCase("Yes") && stepSuccessful) {
            status = "Pass";
        } else if (!stepOptional.equalsIgnoreCase("Yes") && !stepSuccessful) {
            status = "Fail";
        } else {
            status = "Unknown";
        }

        return status;
    }
}
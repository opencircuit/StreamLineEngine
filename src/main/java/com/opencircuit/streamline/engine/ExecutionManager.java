package com.opencircuit.streamline.engine;

import java.util.ArrayList;
import java.util.HashMap;

class ExecutionManager {

    private ScriptManager script;
    private BrowserManager browserManager;
    private DateTimeManager dateTimeManager;

    private HashMap<String, String> executionSettings = new HashMap<>();
    private final String scriptDirectory;
    private final String testCaseID;
    private boolean debugMode;
    private String startTime;

    ExecutionManager(String testCaseID, String scriptName) {

        this.testCaseID = testCaseID;
        scriptDirectory = System.getProperty("user.dir") + "\\Resources\\Scripts\\" + scriptName + "\\";
    }

    void initializeExecutionSetup() {

        initializeStartTime();
        cleanupResidualDriversProcesses();
        loadExecutionSettings();
        loadScriptAndTestCaseData();
        setDebutMode();
    }

    private void initializeStartTime() {

        dateTimeManager = new DateTimeManager();
        startTime = dateTimeManager.getCurrentTime();
    }

    private void cleanupResidualDriversProcesses() {

        ArrayList<String> taskList = new ArrayList<>();
        taskList.add("chromedriver.exe");
        taskList.add("geckodriver.exe");
        taskList.add("IEDriverServer.exe");
        terminateProcessesFromTaskManager(taskList);
    }

    private void terminateProcessesFromTaskManager(ArrayList<String> taskList) {

        try {

            for(String task : taskList) {
                Runtime.getRuntime().exec("taskkill /F /IM " + task);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadExecutionSettings() {

        XmlManager xmlManager = new XmlManager();
        executionSettings = xmlManager.retrieveExecutionSettings(scriptDirectory + "ExecutionSettings.xml");
    }

    private void loadScriptAndTestCaseData() {

        script = new ScriptManager(testCaseID, scriptDirectory);
        script.loadComponentList();
        script.loadTestCaseData();
    }

    private void setDebutMode() {

        debugMode = executionSettings.get("DebugMode").equalsIgnoreCase("True");
    }

    void startExecution() {

        startBrowser();
        setScreenshotDirectory();
        script.executeComponents();
    }

    private void startBrowser() {

        browserManager = new BrowserManager();
        browserManager.invokeBrowser(executionSettings.get("Browser"));
    }

    private void setScreenshotDirectory() {

        BrowserManager.screenshotDirectory = executionSettings.get("ResultsDirectory")
                + "\\" + executionSettings.get("ExecutionID") + "\\Screenshots\\";
    }

    void cleanupExecutionSetup() {

        exitBrowser();
        generateHtmlReport();
        updateExecutionDetailsInDatabase();
    }

    private void exitBrowser() {

        if (!debugMode) { return; }
        if (executionSettings.get("DebugMode").equalsIgnoreCase("true")) { return; }
        browserManager.quitBrowser();
    }


    private void generateHtmlReport() {

        ReportGenerator reportGenerator = getReportGenerator();
        reportGenerator.initializeReport();
        reportGenerator.generateReportContents();
        reportGenerator.finalizeReport();
    }

    private ReportGenerator getReportGenerator() {

        return new ReportGenerator(
                executionSettings.get("ResultsDirectory"),
                executionSettings.get("ExecutionID"),
                testCaseID);
    }

    private void updateExecutionDetailsInDatabase() {

        if (!debugMode) { return; }
        DatabaseConnector database = new DatabaseConnector(scriptDirectory);
        database.establishResultsDatabaseConnection();
        database.setExecutionID(executionSettings.get("ExecutionID"));
        database.setTestCaseID(testCaseID);
        updateSpecificTestCaseExecutionDetails(database);
    }

    private void updateSpecificTestCaseExecutionDetails(DatabaseConnector database) {

        database.updateMainExecutionTable("Status", getFinalStatus(StepDetailsManager.failedStep));
        database.updateMainExecutionTable("Failed_Step", StepDetailsManager.failedStep);
        database.updateMainExecutionTable("Execution_Time", getFinalExecutionTime());
        database.updateMainExecutionTable("Results_Location", ReportGenerator.filePath);
    }

    private String getFinalStatus(String failedStep) {

        if (failedStep.length() == 0) {
            return "Passed";
        } else {
            return "Failed";
        }
    }

    private String getFinalExecutionTime() {

        String endTime = dateTimeManager.getCurrentTime();
        return dateTimeManager.getDifferenceBetweenTwoTimes(startTime, endTime);
    }
}
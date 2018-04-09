package com.opencircuit.streamline.engine;

public class ExecuteTest {

    public static void main(String[] args) {

        String testCaseID = args[0];

        ScriptHandler scriptHandler = new ScriptHandler(testCaseID);
        scriptHandler.initializeSetup();
        scriptHandler.initializeExecutionDetailsDictionary();
        scriptHandler.initializeDriversFoldesCreations();
        scriptHandler.initializeExtractDrivers();
        scriptHandler.initializeDataLoading();
        scriptHandler.initializeScriptLoader();
        scriptHandler.initializeProcessCleanup();
        scriptHandler.initializeBrowserInvocation();
        scriptHandler.executeScriptSteps();
        scriptHandler.closeBrowser();

        ReportGenerator reportGenerator = new ReportGenerator(testCaseID);
        reportGenerator.initializeReport();
        reportGenerator.generateReportContents();
        reportGenerator.updateExecutionDetails();
    }
}

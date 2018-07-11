package com.opencircuit.streamline.engine;

public class RunTestCase {

    public static void main(String[] args) {

        String testCaseID = args[0];
        String scriptName = args[1];

        ExecutionManager executionManager = new ExecutionManager(testCaseID, scriptName);
        executionManager.initializeExecutionSetup();
        executionManager.startExecution();
        executionManager.cleanupExecutionSetup();
    }
}
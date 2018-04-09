package com.opencircuit.streamline.engine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

class ReportGenerator {

    //******************************************************************************************************************
    //*     Setup - Do Not Alter
    //******************************************************************************************************************

    private Common common = new Common();

    private String testCaseID;
    private String debugMode;
    private String reportPath;

    ReportGenerator (String testCaseID) {
        this.testCaseID = testCaseID;
    }

    //******************************************************************************************************************
    //*     Protected Methods
    //******************************************************************************************************************

    void initializeReport() {

        debugMode = ScriptHandler.executionDetailsDictionary.get("DebugModeOn");
        if (debugMode.equalsIgnoreCase("Yes")) { return; }
        String resultsDirectory = ScriptHandler.executionDetailsDictionary.get("ResultsFileDirectory");
        String executionID = ScriptHandler.executionDetailsDictionary.get("ExecutionID");
        String reportDirectory = resultsDirectory + "\\" + executionID;

        String resourceName = "ReportTemplate.html";
        String reportName = testCaseID + ".html";
        reportPath = reportDirectory + "\\" + reportName;
        File file = new File(reportPath);
        if (file.exists()) { file.delete(); }

        common.extractResourceToDirectory(reportDirectory, resourceName, reportName);
    }

    void generateReportContents() {

        if (debugMode.equalsIgnoreCase("Yes")) { return; }

        try{

            FileWriter fileWriter;
            BufferedWriter bufferedWriter;
            PrintWriter printWriter;

            fileWriter = new FileWriter(reportPath, true);
            bufferedWriter = new BufferedWriter(fileWriter);
            printWriter = new PrintWriter(bufferedWriter);

            HashMap<Integer, HashMap<String, String>> stepsDictionary;
            stepsDictionary = ScriptHandler.stepsDictionary;

            HashMap<Integer, String> statusDictionary;
            statusDictionary = ScriptHandler.statusDictionary;

            HashMap<Integer, String> screenshotDictionary;
            screenshotDictionary = ScriptHandler.screenshotDictionary;

            for (int i = 0; i < statusDictionary.size(); i++) {

                printWriter.println("\n<tr>");

                int stepNumber = i + 1;
                writeTableRowData(printWriter, Integer.toString(stepNumber));

                String status = statusDictionary.get(stepNumber);
                writeTableRowStatusData(printWriter, status);

                HashMap<String, String> stepNodes;
                stepNodes = stepsDictionary.get(stepNumber);

                String description = stepNodes.get("Description");
                writeTableRowData(printWriter, description);

                String operation = stepNodes.get("Operation");
                writeTableRowData(printWriter, operation);

                String action = stepNodes.get("Action");
                writeTableRowData(printWriter, action);

                String optionalStep = stepNodes.get("OptionalStep");
                writeTableRowData(printWriter, optionalStep);

                String elementDescription = stepNodes.get("ElementDescription");
                writeTableRowData(printWriter, elementDescription);

                String identifierType = stepNodes.get("IdentifierType");
                writeTableRowData(printWriter, identifierType);

                String identifierValue = stepNodes.get("IdentifierValue");
                writeTableRowData(printWriter, identifierValue);

                String testDataTable = stepNodes.get("TestDataTable");
                writeTableRowData(printWriter, testDataTable);

                String testDataColumn = stepNodes.get("TestDataColumn");
                writeTableRowData(printWriter, testDataColumn);

                String staticDataValue = stepNodes.get("StaticDataValue");
                writeTableRowData(printWriter, staticDataValue);

                String conditionDataTable = stepNodes.get("ConditionDataTable");
                writeTableRowData(printWriter, conditionDataTable);

                String conditionDataColumn = stepNodes.get("ConditionDataColumn");
                writeTableRowData(printWriter, conditionDataColumn);

                String conditionDataValue = stepNodes.get("ConditionDataValue");
                writeTableRowData(printWriter, conditionDataValue);

                String screenshotName = screenshotDictionary.get(stepNumber);

                if (screenshotName == null) {
                    screenshotName = "";
                    writeTableRowData(printWriter, screenshotName);
                } else {
                    writeTableRowLinkData(printWriter, screenshotName);
                }

                printWriter.println("</tr>");
            }

            printWriter.println("</tbody>");
            printWriter.println("</table>");
            printWriter.println("</body>");
            printWriter.println("</html>");

            printWriter.close();
            bufferedWriter.close();
            fileWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void updateExecutionDetails() {

        if (debugMode.equalsIgnoreCase("Yes")) { return; }
        String executionID = ScriptHandler.executionDetailsDictionary.get("ExecutionID");
        String status = ScriptHandler.finalStatus;
        String failedStep = ScriptHandler.failedStep;

        long startTime = ScriptHandler.startTime;
        long endTime = System.currentTimeMillis();
        long time = TimeUnit.MILLISECONDS.toSeconds(endTime - startTime);

        String query = "UPDATE [MAIN] SET" +
                " [STATUS] = '" + status + "'," +
                " [FAILED_STEP] = '" + failedStep + "'," +
                " [EXECUTION_TIME] = '" + time + "'," +
                " [RESULTS_LOCATION] = '" + reportPath + "'" +
                " WHERE [EXECUTION_ID] = '" + executionID + "'" +
                " AND [TEST_CASE_ID] = '" + testCaseID + "'";

        String databaseDirectory = ScriptHandler.executionDetailsDictionary.get("ResultsDatabaseDirectory");
        String databasePath = databaseDirectory + "\\TestCaseResults.db";
        DatabaseConnector databaseConnector = new DatabaseConnector();
        databaseConnector.establishConnection(databasePath);
        databaseConnector.executeUpdateQuery(query);
        databaseConnector.closeConnection();
    }

    //******************************************************************************************************************
    //*     Private Methods
    //******************************************************************************************************************

    private void writeTableRowData(PrintWriter printWriter, String rowData) {

        printWriter.print("<td>");
        printWriter.print(rowData);
        printWriter.print("</td>\n");
    }

    private void writeTableRowStatusData(PrintWriter printWriter, String status) {

        printWriter.print("<td class=\"");
        printWriter.print(status);
        printWriter.print("\">");
        printWriter.print(status);
        printWriter.print("</td>\n");
    }

    private void writeTableRowLinkData(PrintWriter printWriter, String screenshotName) {

        String screenshotPath = "Screenshots\\" + screenshotName + ".png";
        printWriter.print("<td><a href=\"");
        printWriter.print(screenshotPath);
        printWriter.print("\">");
        printWriter.print(screenshotName);
        printWriter.print("</a></td>\n");
    }
}
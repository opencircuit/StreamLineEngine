package com.opencircuit.streamline.engine;

import com.google.common.io.Resources;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.util.HashMap;

class ReportGenerator {

    private FileWriter fileWriter;
    private BufferedWriter bufferedWriter;
    private PrintWriter printWriter;

    static String filePath;
    private String fileDirectory;
    private final String resultsDirectory;
    private final String executionID;
    private final String testCaseID;

    ReportGenerator(String resultsDirectory, String executionID, String testCaseID) {

        this.resultsDirectory = resultsDirectory;
        this.executionID = executionID;
        this.testCaseID = testCaseID;
    }

    void initializeReport() {

        setFileDirectory();
        setHtmlFilePath();
        extractHtmlReport();
    }

    private void setFileDirectory() {

        if (executionID.equalsIgnoreCase("Default")) {
            fileDirectory = resultsDirectory + "\\Default\\";
        } else {
            fileDirectory = resultsDirectory + "\\" + executionID + "\\";
        }
    }

    private void setHtmlFilePath() {

        filePath = fileDirectory + testCaseID + ".html";
    }

    private void extractHtmlReport() {

        try {
            extractHtmlReportToDirectory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void extractHtmlReportToDirectory() throws IOException {

        File file = new File(filePath);
        URL resource = Resources.getResource("ReportTemplate.html");
        FileUtils.copyURLToFile(resource, file);
    }

    void generateReportContents() {

        openWriteAccessToReportFile();
        writeStepsDetailsToFile();
    }

    private void openWriteAccessToReportFile() {

        try {

            fileWriter = new FileWriter(filePath, true);
            bufferedWriter = new BufferedWriter(fileWriter);
            printWriter = new PrintWriter(bufferedWriter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeStepsDetailsToFile() {

        for (HashMap<String, String> step : StepDetailsManager.stepsDictionaryList) {
            writeStepDetailsToFile(step);
        }
    }

    private void writeStepDetailsToFile(HashMap<String, String> step) {

        openTableRowTag();
        writeCellDataValue(step.get("StepNumber"));
        writeStatusDataValue(step.get("Status"));
        writeCellDataValue(step.get("ComponentName"));
        writeCellDataValue(step.get("Operation"));
        writeCellDataValue(step.get("Action"));
        writeCellDataValue(step.get("OptionalStep"));
        writeCellDataValue(step.get("ElementName"));
        writeCellDataValue(step.get("IdentifierType"));
        writeCellDataValue(step.get("IdentifierValue"));
        writeCellDataValue(step.get("WaitTime"));
        writeCellDataValue(step.get("TestDataTable"));
        writeCellDataValue(step.get("TestDataColumn"));
        writeCellDataValue(step.get("StaticDataValue"));
        writeCellDataValue(step.get("ConditionDataTable"));
        writeCellDataValue(step.get("ConditionDataColumn"));
        writeCellDataValue(step.get("ConditionDataValue"));
        writeScreenshotDataValue(step.get("ScreenshotName"));
        closeTableRowTag();
    }

    private void openTableRowTag() {

        printWriter.println("\n<tr>");
    }

    private void writeCellDataValue(String dataValue) {

        printWriter.print("<td>");
        printWriter.print(dataValue);
        printWriter.print("</td>\n");
    }

    private void writeStatusDataValue(String status) {

        printWriter.print("<td class=\"");
        printWriter.print(status);
        printWriter.print("\">");
        printWriter.print(status);
        printWriter.print("</td>\n");
    }

    private void writeScreenshotDataValue(String screenshotName) {

        String screenshotLink = getScreenshotLink(screenshotName);
        writeCellDataValue(screenshotLink);
    }

    private String getScreenshotLink(String screenshotName) {

        if (screenshotName.length() == 0) { return ""; }
        else { return "<a href=\"" + fileDirectory + "Screenshots\\"
                + screenshotName + ".png\">" + screenshotName + "</a>"; }
    }

    private void closeTableRowTag() {

        printWriter.println("</tr>");
    }

    void finalizeReport() {

        closeReportHtmlTags();
        closeWriteAccessToReportFile();
    }

    private void closeReportHtmlTags() {

        printWriter.println("</tbody>");
        printWriter.println("</table>");
        printWriter.println("</body>");
        printWriter.println("</html>");
    }

    private void closeWriteAccessToReportFile() {

        try {

            printWriter.close();
            bufferedWriter.close();
            fileWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
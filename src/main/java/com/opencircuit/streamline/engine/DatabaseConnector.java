package com.opencircuit.streamline.engine;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

class DatabaseConnector {

    private Connection connection;
    private final String testDatabasePath;
    private final String resultsDatabasePath;
    private final HashMap<String, String> dataDictionary;
    private String executionID;
    private String testCaseID;

    DatabaseConnector(String scriptDirectory) {

        testDatabasePath = scriptDirectory + "TestCaseData.db";
        resultsDatabasePath = scriptDirectory + "TestCaseResults.db";
        dataDictionary = new HashMap<>();
    }

    void setExecutionID(String executionID) {

        this.executionID = executionID;
    }

    void setTestCaseID(String testCaseID) {

        this.testCaseID = testCaseID;
    }

    void establishTestDatabaseConnection() {

        try {

            String url = "jdbc:sqlite:" + testDatabasePath;
            connection = DriverManager.getConnection(url);
            dataDictionary.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void establishResultsDatabaseConnection() {

        try {

            String url = "jdbc:sqlite:" + resultsDatabasePath;
            connection = DriverManager.getConnection(url);
            dataDictionary.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    HashMap<String, String> retrieveTestCaseData() {

        ArrayList<String> tableList = getTableList();

        for (String tableName : tableList) {
            executeSelectQueryForTable(tableName);
        }

        return dataDictionary;
    }

    private ArrayList<String> getTableList() {

        ArrayList<String> tableList = new ArrayList<>();

        try {

            ResultSet resultSet = connection.getMetaData().getTables(
                    null,null,null,null);

            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                tableList.add(tableName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tableList;
    }

    private void executeSelectQueryForTable(String tableName) {

        try {

            String query = "SELECT * FROM [" + tableName + "] WHERE [TEST_CASE_ID] = '" + testCaseID + "'";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ArrayList<String> columnList = getColumnListList(resultSet);
            loopThroughResultSet(tableName, resultSet, columnList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> getColumnListList(ResultSet resultSet) {

        ArrayList<String> columnList = new ArrayList<>();

        try {

            ResultSetMetaData queryMetaData = resultSet.getMetaData();

            for (int i = 1; i <= queryMetaData.getColumnCount(); i++) {
                columnList.add(queryMetaData.getColumnName(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return columnList;
    }

    private void loopThroughResultSet(String tableName, ResultSet resultSet, ArrayList<String> columnList) {

        try {

            while (resultSet.next()) {
                loopThroughRecordDataByColumn(resultSet, tableName, columnList);
            }

            resultSet.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loopThroughRecordDataByColumn(ResultSet resultSet, String tableName, ArrayList<String> columnList) {

        try {

            for (String columnName : columnList) {

                String dataValue = resultSet.getString(columnName);
                addDataValueToDictionary(tableName, columnName, dataValue);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addDataValueToDictionary(String tableName, String columnName, String dataValue) {

        try {

            if (dataValue == null) { dataValue = ""; }
            String dictionaryKey = tableName + "." + columnName;
            dataDictionary.put(dictionaryKey.toLowerCase(), dataValue);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void updateMainExecutionTable(String columnName, String dataValue) {

        try {

            String query = "UPDATE [Main] SET [" + columnName + "] = '" + dataValue + "' " +
                    "WHERE [Execution_ID] = '" + executionID + "' AND [Test_Case_ID] = '" + testCaseID + "'";

            executeUpdateQuery(query);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeUpdateQuery(String query) {

        try {

            Statement updateStatement = connection.createStatement();
            updateStatement.executeUpdate(query);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void closeConnection() {

        try { connection.close(); }
        catch (Exception e) { e.printStackTrace(); }
    }
}
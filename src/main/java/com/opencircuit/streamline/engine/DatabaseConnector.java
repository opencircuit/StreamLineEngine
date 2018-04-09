package com.opencircuit.streamline.engine;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

class DatabaseConnector {

    private Connection connection;
    private HashMap<String, String> dataDictionary;

    void establishConnection(String databasePath) {

        try {

            String url = "jdbc:sqlite:" + databasePath;
            connection = DriverManager.getConnection(url);
            dataDictionary = new HashMap<>();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    HashMap<String, String> executeTestCaseDataSelectQuery(String testCaseID) {

        ArrayList<String> tableList = getTableList();
        dataDictionary.clear();

        for (String tableName : tableList) {
            executeSelectQueryForTable(tableName, testCaseID);
        }

        return dataDictionary;
    }

    void executeUpdateQuery(String query) {

        try {

            Statement updateStatement = connection.createStatement();
            updateStatement.executeUpdate(query);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void closeConnection() {

        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeSelectQueryForTable(String tableName, String testCaseID) {

        try {

            String query = "SELECT * FROM [" + tableName + "] WHERE [TEST_CASE_ID] = '" + testCaseID + "'";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ArrayList<String> columnList = getColumnListList(resultSet);

            while (resultSet.next()) {
                collectQueriedData(resultSet, tableName, columnList);
            }

            resultSet.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> getTableList() {

        ArrayList<String> tableList = new ArrayList<>();

        try {

            ResultSet resultSet;
            resultSet = connection.getMetaData().getTables(
                    null,
                    null,
                    null,
                    null);

            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                tableList.add(tableName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tableList;
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

    private void collectQueriedData(ResultSet resultSet, String tableName, ArrayList<String> columnList) {

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
            dataDictionary.put(dictionaryKey, dataValue);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
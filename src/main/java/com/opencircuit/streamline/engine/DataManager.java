package com.opencircuit.streamline.engine;

import java.util.HashMap;

class DataManager {

    private static HashMap<String, String> dataDictionary;

    void loadTestCaseData(String scriptDirectory, String testCaseID) {

        DatabaseConnector database = new DatabaseConnector(scriptDirectory);
        database.establishTestDatabaseConnection();
        database.setTestCaseID(testCaseID);
        dataDictionary = database.retrieveTestCaseData();
        database.closeConnection();
    }

    boolean setDictionaryKeyValue(String tableName, String columnName, String dataValue) {

        try {

            String dictionaryKey = getDictionaryKey(tableName, columnName);
            dataDictionary.put(dictionaryKey, dataValue);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    boolean compareTableDataToProvidedDataValue(String tableName, String columnName, String dataValue) {

        String retrievedValue = retrieveDictionaryKeyValue(tableName, columnName);
        return retrievedValue.equalsIgnoreCase(dataValue);
    }

    private String retrieveDictionaryKeyValue(String tableName, String columnName) {

        String dictionaryKey = getDictionaryKey(tableName, columnName);
        return getDictionaryKeyValue(dictionaryKey, "");
    }

    String getDictionaryKey(String tableName, String columnName) {

        return tableName.toLowerCase() + "." + columnName.toLowerCase();
    }

    String getDictionaryKeyValue(String dictionaryKey, String defaultValue) {

        return dataDictionary.getOrDefault(dictionaryKey, defaultValue);
    }
}
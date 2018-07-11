package com.opencircuit.streamline.engine;

import java.util.ArrayList;
import java.util.HashMap;

class StepDetailsManager {

    static ArrayList<HashMap<String, String>> stepsDictionaryList;
    static String failedStep;
    private int stepNumber;

    StepDetailsManager() {

        stepsDictionaryList = new ArrayList<>();
        failedStep = "";
        stepNumber = 1;
    }

    void logStepDetailsForReport(HashMap<String, String> step, String status) {

        setFailedStep(step, status);
        addStepNumberToDictionary(step);
        addStepDictionaryToList(step);
    }

    private void setFailedStep(HashMap<String, String> step, String status) {

        if (status.equalsIgnoreCase("Failed")) {
            failedStep = stepNumber + " - " + step.get("ComponentName") + " - " + step.get("Action");
        }
    }

    private void addStepNumberToDictionary(HashMap<String, String> step) {

        step.put("StepNumber", Integer.toString(stepNumber));
    }

    private void addStepDictionaryToList(HashMap<String, String> step) {

        stepsDictionaryList.add(step);
        stepNumber++;
    }

    void resetStepNumber() {

        stepNumber = 1;
    }
}
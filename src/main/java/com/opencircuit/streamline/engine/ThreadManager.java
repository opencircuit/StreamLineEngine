package com.opencircuit.streamline.engine;

class ThreadManager {

    boolean sleepThreadForSpecifiedSeconds(String seconds) {

        int time = convertStringToInteger(seconds) * 1000;
        return sleepThreadForSpecifiedSeconds(time);
    }

    boolean sleepThreadForSpecifiedMilliseconds(String milliseconds) {

        int time = convertStringToInteger(milliseconds);
        return sleepThreadForSpecifiedSeconds(time);
    }

    int convertStringToInteger(String number) {

        try {
            return Integer.parseInt(number);
        } catch (Exception e) {
            return 1;
        }
    }

    boolean sleepThreadForSpecifiedSeconds(int seconds) {

        try {

            Thread.sleep(seconds * 1000);
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
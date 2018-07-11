package com.opencircuit.streamline.engine;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

class DateTimeManager {

    String getCurrentDateTime() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd-HHmmss");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return simpleDateFormat.format(timestamp);
    }

    String getCurrentTime() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return simpleDateFormat.format(timestamp);
    }

    String getDifferenceBetweenTwoTimes(String startTime, String endTime) {

        try {

            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            long timeDifference = (format.parse(endTime).getTime() - format.parse(startTime).getTime()) / 1000;
            return Long.toString(timeDifference);

        } catch (Exception e) {
            return "0";
        }
    }
}

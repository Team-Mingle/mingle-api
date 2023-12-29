package community.mingle.api.global.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeConverter {

    public static String convertToDateAndTime(LocalDateTime localDateTime) {
        String dateFormat = localDateTime.format(DateTimeFormatter.ofPattern("yy/MM/dd HH:mm"));
        return dateFormat;
    }
}
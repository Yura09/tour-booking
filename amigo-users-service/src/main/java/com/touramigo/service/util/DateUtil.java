package com.touramigo.service.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateUtil {

    /**
     * Prints "2021-01-15 07:55:42 +06:00"
     */
    public static final String STANDARD_ZONED_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss xxx";
    public static final String STANDARD_DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm:ss";
    public static final DateTimeFormatter STANDARD_ZONED_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(STANDARD_ZONED_DATE_TIME_FORMAT);
    public static final DateTimeFormatter STANDARD_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(STANDARD_DATE_TIME_FORMAT);

    public String format(ZonedDateTime zonedDateTime) {
        return zonedDateTime == null
            ? null
            : zonedDateTime.format(STANDARD_DATE_TIME_FORMATTER);
    }

    public String render(ZonedDateTime zonedDateTime) {
        return zonedDateTime == null
            ? null
            : zonedDateTime.format(STANDARD_ZONED_DATE_TIME_FORMATTER);
    }


}

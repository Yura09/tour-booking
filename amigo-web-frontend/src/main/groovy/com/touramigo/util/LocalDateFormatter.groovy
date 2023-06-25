package com.touramigo.util

import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

class LocalDateFormatter {

    static final LONG_DATE_FORMAT_STRING = "dd MMMM yyyy"

    static final DateTimeFormatter longDateFormatter = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
            .appendPattern(LONG_DATE_FORMAT_STRING).toFormatter(Locale.US)

    static final QUERY_DATE_FORMAT_STRING = "yyyy-MM-dd"

    static final DateTimeFormatter queryDateFormatter = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
            .appendPattern(QUERY_DATE_FORMAT_STRING).toFormatter(Locale.US)

    static final INPUT_DATE_FORMAT_STRING = "dd-MM-yyyy"

    static final DateTimeFormatter inputDateFormatter = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
            .appendPattern(INPUT_DATE_FORMAT_STRING).toFormatter(Locale.US)

    static final DEPARTURE_DATE_FORMAT_STRING = "MM-yyyy"

    static final DateTimeFormatter departureDateFormatter = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
            .appendPattern(DEPARTURE_DATE_FORMAT_STRING).toFormatter(Locale.US)
}
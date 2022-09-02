package com.risky.gambling.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Helpers {
    public static final int INCOMPLETE = 0;
    public static final int COMPLETED = 1;

    public static String formatDateTime(LocalDateTime dateTime)
    {
        return DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss").format(dateTime);
    }
    public static String formatDate(LocalDate date)
    {
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

}
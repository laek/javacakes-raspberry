package com.makers.javacakesraspberry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DoorbellTime {
    public static LocalDateTime doorbellAlert = LocalDateTime.now();
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy HH:mm:ss a");
    public static String newDateTime () {
        return formatter.format(doorbellAlert);
    }
}

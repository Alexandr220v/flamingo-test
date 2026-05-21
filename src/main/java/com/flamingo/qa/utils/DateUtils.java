package com.flamingo.qa.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtils {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static LocalDate parseIsoDate(String isoDate) {
        try {
            return LocalDate.parse(isoDate, ISO_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected format: YYYY-MM-DD", e);
        }
    }

    /**
     * Parses a date string in the format YYYY-MM-DD into an array of integers [year, month, day].
     *
     * @param date the date string in the format YYYY-MM-DD
     * @return an array of integers [year, month, day]
     * @throws IllegalArgumentException if the date format is invalid
     */
    public static int[] parseDate(String date) {
        if (date == null || !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Invalid date format. Expected format: YYYY-MM-DD");
        }

        String[] parts = date.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        return new int[]{year, month, day};
    }
}

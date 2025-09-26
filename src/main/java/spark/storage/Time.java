package spark.storage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class Time {
    private LocalDateTime dateTime;
    private LocalDate dateOnly;
    private boolean hasTime;
    private boolean isValid;
    private String errorMessage;

    private static final String INVALID_TIME = "Invalid date/time format: ";
    private static final String EXPECTED_FORMAT = "Expected formats:\n" +
            "    yyyy-MM-dd HHmm (e.g., 2025-01-01 0100)\n" +
            "    yyyy-MM-dd (e.g., 2025-01-01)";

    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");


    private static final DateTimeFormatter DATE_DISPLAY =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DATETIME_DISPLAY =
            DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm", Locale.ENGLISH);

    public Time(String timeString) {
        parseTimeString(timeString);
    }

    private void parseTimeString(String timeString) {
        timeString = timeString.trim();
        this.isValid = false;
        this.hasTime = false;
        this.dateTime = null;
        this.dateOnly = null;
        this.errorMessage = null;

        try {
            this.dateTime = LocalDateTime.parse(timeString, DATETIME_FORMATTER);
            this.hasTime = true;
            this.isValid = true;
        } catch (DateTimeParseException e1) {
            try {
                this.dateOnly = LocalDate.parse(timeString, DATE_FORMATTER);
                this.hasTime = false;
                this.isValid = true;
            } catch (DateTimeParseException e2) {
                this.isValid = false;
                this.errorMessage = INVALID_TIME + timeString;
            }
        }
    }

    public boolean isValid() {
        return isValid;
    }

    public String getErrorMessage() {
        return errorMessage + "\n" + EXPECTED_FORMAT;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public LocalDate getDate() {
        return hasTime ? dateTime.toLocalDate() : dateOnly;
    }

    public boolean hasTime() {
        return hasTime;
    }

    public boolean isSameDate(LocalDate otherDate) {
        return getDate().equals(otherDate);
    }

    @Override
    public String toString() {
        if (hasTime) {
            return dateTime.format(DATETIME_DISPLAY);
        } else {
            return dateOnly.format(DATE_DISPLAY);
        }
    }

    public String toStorageString() {
        if (hasTime) {
            return dateTime.format(DATETIME_FORMATTER);
        } else {
            return dateOnly.format(DATE_FORMATTER);
        }
    }
}
package spark.storage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Represents a time value with date and optional time components,
 * handles parsing validation, and formatting of the time strings.
 */
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

    /**
     * Constructs a Time object by parsing the given time string.
     *
     * @param timeString The time string to parse.
     */
    public Time(String timeString) {
        parseTimeString(timeString);
    }

    /**
     * Parses the time string and initializes the time object.
     *
     * @param timeString The time string to parse.
     */
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

    /**
     * Checks if the time value is valid.
     *
     * @return True if the time is valid, false otherwise.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Gets the error message if the time is invalid.
     *
     * @return The error message and the correct format.
     */
    public String getErrorMessage() {
        return errorMessage + "\n" + EXPECTED_FORMAT;
    }

    /**
     * Get the LocalDateTime object.
     *
     * @return The LocalDateTime object.
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * Get the LocalDate object.
     *
     * @return The LocalDate object representing the date.
     */
    public LocalDate getDate() {
        return hasTime ? dateTime.toLocalDate() : dateOnly;
    }

    /**
     * Checks if this time object has a time component.
     *
     * @return True if time component is present, false if only date.
     */
    public boolean hasTime() {
        return hasTime;
    }

    /**
     * Checks if this time object represents the same date as another date.
     *
     * @param otherDate The date to compare with.
     * @return True if the dates are the same, false otherwise.
     */
    public boolean isSameDate(LocalDate otherDate) {
        return getDate().equals(otherDate);
    }

    /**
     * Returns a string representation of the time.
     *
     * @return The string representation of the time.
     */
    @Override
    public String toString() {
        if (hasTime) {
            return dateTime.format(DATETIME_DISPLAY);
        } else {
            return dateOnly.format(DATE_DISPLAY);
        }
    }

    /**
     * Returns the storage format string representation of the time.
     *
     * @return The time string in storage format.
     */
    public String toStorageString() {
        if (hasTime) {
            return dateTime.format(DATETIME_FORMATTER);
        } else {
            return dateOnly.format(DATE_FORMATTER);
        }
    }
}
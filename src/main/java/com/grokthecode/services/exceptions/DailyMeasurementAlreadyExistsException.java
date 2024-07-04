package com.grokthecode.services.exceptions;

public class DailyMeasurementAlreadyExistsException extends Exception {
    public DailyMeasurementAlreadyExistsException(final long damId, final String date) {
        super("Daily measurement witd damId: " + damId + ",and measurement date: " + date + " already exists");
    }
}

package com.grokthecode.data.requests;

import java.io.Serializable;

public record DailyMeasurementDatesRequest(String startDate, String endDate) implements Serializable {
}

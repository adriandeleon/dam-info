package com.grokthecode.data.requests;

import java.io.Serializable;

public record DailyMeasurementRequest(String sihKey, String startDate, String endDate) implements Serializable {
}

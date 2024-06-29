package com.grokthecode.data.requests;

import java.io.Serializable;

public record DamInfoDatesRequest(String startDate, String endDate) implements Serializable {
}

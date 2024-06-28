package com.grokthecode.data.requests;

import java.time.LocalDate;

public record DamInfoStateRequest(String state, String startDate, String endDate) {
}

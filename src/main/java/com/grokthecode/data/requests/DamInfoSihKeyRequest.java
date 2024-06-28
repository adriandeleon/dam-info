package com.grokthecode.data.requests;

import java.time.LocalDate;

public record DamInfoSihKeyRequest(String sihKey, String startDate, String endDate) {
}

package com.grokthecode.data.requests;

import java.io.Serializable;

public record DamInfoSihKeyRequest(String sihKey, String startDate, String endDate) implements Serializable {
}

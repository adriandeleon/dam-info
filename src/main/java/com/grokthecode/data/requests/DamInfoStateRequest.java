package com.grokthecode.data.requests;

import java.io.Serializable;

public record DamInfoStateRequest(String state, String startDate, String endDate) implements Serializable {
}

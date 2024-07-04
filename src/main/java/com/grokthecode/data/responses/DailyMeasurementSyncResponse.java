package com.grokthecode.data.responses;

import com.grokthecode.data.entities.DailyMeasurementEntity;

import java.util.List;

public record DailyMeasurementSyncResponse(String formatedDate, Integer syncCount, List<DailyMeasurementEntity> dailyMeasurementList, List<String> syncErrorMessageList) {
}

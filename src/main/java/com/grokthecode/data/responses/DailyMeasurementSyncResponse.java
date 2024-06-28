package com.grokthecode.data.responses;

import com.grokthecode.data.entities.DailyMeasurementEntity;

import java.util.List;

public record DailyMeasurementSyncResponse(Integer syncCount, List<DailyMeasurementEntity> dailyMeasurementEntityList, List<String> syncErrorMessageList) {
}

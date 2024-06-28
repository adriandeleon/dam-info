package com.grokthecode.data.responses;

import com.grokthecode.data.entities.DailyMeasurementEntity;
import com.grokthecode.data.entities.DamCatalogEntity;

import java.util.List;

public record DamInfoResponse(DamCatalogEntity dam, List<DailyMeasurementEntity> dailyMeasurementList) {
}

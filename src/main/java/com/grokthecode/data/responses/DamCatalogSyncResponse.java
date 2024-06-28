package com.grokthecode.data.responses;

import com.grokthecode.data.entities.DamCatalogEntity;

import java.util.List;

public record DamCatalogSyncResponse(Integer syncCount, List<DamCatalogEntity> dailyMeasurementEntityList, List<String> syncErrorMessageList) {
}

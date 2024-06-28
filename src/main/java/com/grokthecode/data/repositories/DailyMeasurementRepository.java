package com.grokthecode.data.repositories;

import com.grokthecode.data.entities.DailyMeasurementEntity;
import com.grokthecode.data.entities.DamCatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyMeasurementRepository extends JpaRepository<DailyMeasurementEntity, Long>, JpaSpecificationExecutor<DailyMeasurementEntity> {

    List<DailyMeasurementEntity> findByDamCatalogEntityOrderByIdDesc(DamCatalogEntity damCatalogEntity);
    Boolean existsByDamCatalogEntityAndMeasurementDate(DamCatalogEntity damCatalogEntity, LocalDate measurementDate);
    List<DailyMeasurementEntity> findByMeasurementDateBetweenOrderByMeasurementDateAsc(LocalDate startDate, LocalDate endDate);
    List<DailyMeasurementEntity> findByDamCatalogEntityAndMeasurementDateBetweenOrderByMeasurementDateAsc(DamCatalogEntity damCatalogEntity, LocalDate startDate, LocalDate endDate);
}

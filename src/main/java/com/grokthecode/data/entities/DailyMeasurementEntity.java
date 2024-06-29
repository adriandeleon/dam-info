package com.grokthecode.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.grokthecode.common.GlobalConstants;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "dailyMeasurements")
public class DailyMeasurementEntity extends AbstractEntity{

    public DailyMeasurementEntity() {}

    public DailyMeasurementEntity(final Double currentElevation, final Double currentCapacity,
                                  final Double currentFillPercentage, final LocalDate measurementDate,
                                  final DamCatalogEntity damCatalogEntity) {

        this.currentElevation = currentElevation;
        this.currentCapacity = currentCapacity;
        this.currentFillPercentage = currentFillPercentage;
        this.measurementDate = measurementDate;
        this.damCatalogEntity = damCatalogEntity;
    }

    @NotNull(message = "currentElevation" + GlobalConstants.MESSAGE_MUST_NOT_BE_NULL)
    @Column(name = "currentElevation", nullable = false)
    private Double currentElevation;

    @NotNull(message = "currentCapacity" + GlobalConstants.MESSAGE_MUST_NOT_BE_NULL)
    @Column(name = "currentCapacity", nullable = false)
    private Double currentCapacity;

    @NotNull(message = "currentFillPercentage" + GlobalConstants.MESSAGE_MUST_NOT_BE_NULL)
    @Column(name = "currentFillPercentage", nullable = false)
    private Double currentFillPercentage;

    @Column(name = "measurementDate")
    private LocalDate measurementDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "damId", referencedColumnName = "id")
    private DamCatalogEntity damCatalogEntity;

}

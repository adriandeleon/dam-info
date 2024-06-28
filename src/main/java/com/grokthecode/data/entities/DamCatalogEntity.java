package com.grokthecode.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.grokthecode.common.GlobalConstants;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "dams")
public class DamCatalogEntity extends AbstractEntity {

    public DamCatalogEntity() {}

    public DamCatalogEntity(final String sihKey, final String officialName, final String commonName, final String state,
                            final String municipality, final String cnaRegion, final Double latitude, final Double longitude,
                            final String usage, final String currents, final String verterType, final String operationStartYear,
                            final String elevationCrown, final Double freeBorder, final Double nameElevation, final Double nameCapacity,
                            final String shadeHeight) {
        this.sihKey = sihKey;
        this.officialName = officialName;
        this.commonName = commonName;
        this.state = state;
        this.municipality = municipality;
        this.cnaRegion = cnaRegion;
        this.latitude = latitude;
        this.longitude = longitude;
        this.usage = usage;
        this.currents = currents;
        this.verterType = verterType;
        this.operationStartYear = operationStartYear;
        this.elevationCrown = elevationCrown;
        this.freeBorder = freeBorder;
        this.nameElevation = nameElevation;
        this.nameCapacity = nameCapacity;
        this.shadeHeight = shadeHeight;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "sihKey" + GlobalConstants.MESSAGE_MUST_NOT_BE_NULL)
    @NotBlank(message = "sihKey" + GlobalConstants.MESSAGE_MUST_NOT_BE_BLANK)
    @Column(name = "sihKey", nullable = false)
    private String sihKey;

    @NotNull(message = "officialName" + GlobalConstants.MESSAGE_MUST_NOT_BE_NULL)
    @NotBlank(message = "officialName" + GlobalConstants.MESSAGE_MUST_NOT_BE_BLANK)
    @Column(name = "officialName", nullable = false)
    private String officialName;

    @NotNull(message = "commonName" + GlobalConstants.MESSAGE_MUST_NOT_BE_NULL)
    @Column(name = "commonName", nullable = false)
    private String commonName;

    @NotNull(message = "state" + GlobalConstants.MESSAGE_MUST_NOT_BE_NULL)
    @Column(name = "state", nullable = false)
    private String state;

    @NotNull(message = "municipality" + GlobalConstants.MESSAGE_MUST_NOT_BE_NULL)
    @Column(name = "municipality", nullable = false)
    private String municipality;

    @NotNull(message = "cnaRegion" + GlobalConstants.MESSAGE_MUST_NOT_BE_NULL)
    @Column(name = "cnaRegion", nullable = false)
    private String  cnaRegion;

    @NotNull(message = "latitude" + GlobalConstants.MESSAGE_MUST_NOT_BE_NULL)
    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @NotNull(message = "longitude" + GlobalConstants.MESSAGE_MUST_NOT_BE_NULL)
    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "use")
    private String usage;

    @NotNull(message = "currents" + GlobalConstants.MESSAGE_MUST_NOT_BE_NULL)
    @Column(name = "currents", nullable = false)
    private String currents;

    @NotNull(message = "verterType" + GlobalConstants.MESSAGE_MUST_NOT_BE_NULL)
    @Column(name = "verterType", nullable = false)
    private String verterType;

    @Column(name = "operationStartYear")
    private String operationStartYear;

    @Column(name = "elevationCrown")
    private String elevationCrown;

    @NotNull(message = "freeBorder" + GlobalConstants.MESSAGE_MUST_NOT_BE_NULL)
    @Column(name = "freeBorder", nullable = false)
    private Double freeBorder;

    @NotNull(message = "NAMEElevation" + GlobalConstants.MESSAGE_MUST_NOT_BE_NULL)
    @Column(name = "NAMEElevation", nullable = false)
    private Double nameElevation;

    @NotNull(message = "NAMECapacity" + GlobalConstants.MESSAGE_MUST_NOT_BE_NULL)
    @Column(name = "NAMECapacity", nullable = false)
    private Double nameCapacity;

    @Column(name = "shadeHeight")
    private String shadeHeight;

    @OneToMany(mappedBy = "damCatalogEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<DailyMeasurementEntity> dailyMeasurementEntityList;
}

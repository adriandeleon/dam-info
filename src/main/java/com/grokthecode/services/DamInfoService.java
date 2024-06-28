package com.grokthecode.services;

import com.grokthecode.data.entities.DailyMeasurementEntity;
import com.grokthecode.data.entities.DamCatalogEntity;
import com.grokthecode.data.repositories.DailyMeasurementRepository;
import com.grokthecode.data.repositories.DamCatalogRepository;
import com.grokthecode.data.responses.DamInfoResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The type Dam info service.
 */
@Service
@Transactional
@Log4j2
public class DamInfoService {

    final private DamCatalogRepository damCatalogRepository;
    final private DailyMeasurementRepository dailyMeasurementRepository;

    /**
     * Instantiates a new Dam info service.
     *
     * @param damCatalogRepository       the dam catalog repository
     * @param dailyMeasurementRepository the daily measurement repository
     */
    public DamInfoService(final DamCatalogRepository damCatalogRepository,
                          final DailyMeasurementRepository dailyMeasurementRepository) {
        this.damCatalogRepository = damCatalogRepository;
        this.dailyMeasurementRepository = dailyMeasurementRepository;
    }

    /**
     * Gets dams info.
     *
     * @return the dams info
     */
    public List<DamInfoResponse> getDamsInfo() {
        final List<DamInfoResponse> damInfoResponseList = new ArrayList<>();

        for (final DamCatalogEntity damCatalogEntity : damCatalogRepository.findAll()) {
            final List<DailyMeasurementEntity> dailyMeasurementEntityList = dailyMeasurementRepository.findByDamCatalogEntityOrderByMeasurementDateDesc(damCatalogEntity);
            final DamInfoResponse damInfoResponse = new DamInfoResponse(damCatalogEntity, dailyMeasurementEntityList);

            damInfoResponseList.add(damInfoResponse);
            }

        return  damInfoResponseList;
    }

    /**
     * Gets dams info by dates.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return the dams info by dates
     */
    public List<DamInfoResponse> getDamsInfoByDates(final LocalDate startDate, final LocalDate endDate) {
        Objects.requireNonNull(startDate, "startDate cannot be null.");
        Objects.requireNonNull(endDate, "endDate cannot be null.");

        final List<DamInfoResponse> damInfoResponseList = new ArrayList<>();

        for (final DamCatalogEntity damCatalogEntity : damCatalogRepository.findAll()) {
            final List<DailyMeasurementEntity> dailyMeasurementEntityList =
                    dailyMeasurementRepository.findByMeasurementDateBetweenOrderByMeasurementDateDesc(startDate, endDate);
            final DamInfoResponse damInfoResponse = new DamInfoResponse(damCatalogEntity, dailyMeasurementEntityList);

            damInfoResponseList.add(damInfoResponse);
        }
        return damInfoResponseList;
    }

    /**
     * Gets dams info by state.
     *
     * @param state the state
     * @return the dams info by state
     */
    public List<DamInfoResponse> getDamsInfoByState(final String state) {
        Objects.requireNonNull(state, "state cannot be null.");

        final List<DamInfoResponse> damInfoResponseList = new ArrayList<>();

        for (final DamCatalogEntity damCatalogEntity : damCatalogRepository.findByState(state)) {
            final List<DailyMeasurementEntity> dailyMeasurementEntityList =
                    dailyMeasurementRepository.findByDamCatalogEntityOrderByMeasurementDateDesc(damCatalogEntity);
            final DamInfoResponse damInfoResponse = new DamInfoResponse(damCatalogEntity, dailyMeasurementEntityList);

            damInfoResponseList.add(damInfoResponse);
        }
        return damInfoResponseList;
    }

    /**
     * Gets dams info by state.
     *
     * @param state     the state
     * @param startDate the start date
     * @param endDate   the end date
     * @return the dams info by state
     */
    public List<DamInfoResponse> getDamsInfoByState(final String state, final LocalDate startDate, final LocalDate endDate) {
        Objects.requireNonNull(state, "state cannot be null.");
        Objects.requireNonNull(startDate, "startDate cannot be null.");
        Objects.requireNonNull(endDate, "endDate cannot be null.");

        final List<DamInfoResponse> damInfoResponseList = new ArrayList<>();

        for (final DamCatalogEntity damCatalogEntity : damCatalogRepository.findByState(state)) {
            final List<DailyMeasurementEntity> dailyMeasurementEntityList =
                    dailyMeasurementRepository.findByDamCatalogEntityAndMeasurementDateBetweenOrderByMeasurementDateDesc(damCatalogEntity, startDate, endDate);
            final DamInfoResponse damInfoResponse = new DamInfoResponse(damCatalogEntity, dailyMeasurementEntityList);

            damInfoResponseList.add(damInfoResponse);
        }
        return damInfoResponseList;
    }

    /**
     * Gets dams info by sih key.
     *
     * @param sihKey the sih key
     * @return the dams info by sih key
     */
    public DamInfoResponse getDamsInfoBySihKey(final String sihKey) {
        final Optional<DamCatalogEntity> damCatalogEntityOptional = damCatalogRepository.findBySihKey(sihKey);

        if(damCatalogEntityOptional.isEmpty()) {
            throw new IllegalArgumentException("DamCatalogEntity with sihKey: " + sihKey + " not found");
        }

        final DamCatalogEntity damCatalogEntity = damCatalogEntityOptional.get();

        final List<DailyMeasurementEntity> dailyMeasurementEntityList =
                dailyMeasurementRepository.findByDamCatalogEntityOrderByMeasurementDateDesc(damCatalogEntity);

        return new DamInfoResponse(damCatalogEntity, dailyMeasurementEntityList);
    }

    /**
     * Gets dams info by sih key.
     *
     * @param sihKey    the sih key
     * @param startDate the start date
     * @param endDate   the end date
     * @return the dams info by sih key
     */
    public DamInfoResponse getDamsInfoBySihKey(final String sihKey, final LocalDate startDate, final LocalDate endDate) {
        Objects.requireNonNull(sihKey, "sihKey cannot be null.");
        Objects.requireNonNull(startDate, "startDate cannot be null.");
        Objects.requireNonNull(endDate, "endDate cannot be null.");

        final Optional<DamCatalogEntity> damCatalogEntityOptional = damCatalogRepository.findBySihKey(sihKey);

        if(damCatalogEntityOptional.isEmpty()) {
            throw new IllegalArgumentException("DamCatalogEntity with sihKey: " + sihKey + " not found");
        }

        final DamCatalogEntity damCatalogEntity = damCatalogEntityOptional.get();

        final List<DailyMeasurementEntity> dailyMeasurementEntityList =
                dailyMeasurementRepository.findByDamCatalogEntityAndMeasurementDateBetweenOrderByMeasurementDateDesc(damCatalogEntity, startDate, endDate);

        return new DamInfoResponse(damCatalogEntity, dailyMeasurementEntityList);
    }
}

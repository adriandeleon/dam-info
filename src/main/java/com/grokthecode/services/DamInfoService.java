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
 * The DamInfoService class provides methods to retrieve information about dams
 * from the dam catalog and associated daily measurements.
 */
@Service
@Transactional
@Log4j2
public class DamInfoService {

    final private DamCatalogRepository damCatalogRepository;
    final private DailyMeasurementRepository dailyMeasurementRepository;

    public DamInfoService(final DamCatalogRepository damCatalogRepository,
                          final DailyMeasurementRepository dailyMeasurementRepository) {
        this.damCatalogRepository = damCatalogRepository;
        this.dailyMeasurementRepository = dailyMeasurementRepository;
    }

    /**
     * Retrieves information about all dams from the dam catalog.
     *
     * @return The list of DamInfoResponse objects containing the dam catalog information and
     *         associated daily measurements, sorted by measurement date in descending order.
     */
    public List<DamInfoResponse> getDamsInfo() {
        final List<DamInfoResponse> damInfoResponseList = new ArrayList<>();

        for (final DamCatalogEntity damCatalogEntity : damCatalogRepository.findAll()) {
            final List<DailyMeasurementEntity> dailyMeasurementEntityList = dailyMeasurementRepository.findByDamCatalogEntityOrderByMeasurementDateDesc(damCatalogEntity);
            final DamInfoResponse damInfoResponse = new DamInfoResponse(damCatalogEntity, dailyMeasurementEntityList);

            damInfoResponseList.add(damInfoResponse);
        }

        return damInfoResponseList;
    }

    /**
     * Retrieves information about dams between the given start and end dates.
     *
     * @param startDate the start date to filter the dams (not null)
     * @param endDate the end date to filter the dams (not null)
     * @return a list of DamInfoResponse objects containing information about the dams
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
     * Retrieves a list of dam information for a given state.
     *
     * @param state the state for which dam information needs to be retrieved
     * @return a list of DamInfoResponse objects containing the dam catalog entity and daily measurement entity
     *         information for each dam in the given state
     * @throws NullPointerException if the state is null
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
     * Retrieves the information about dams in a specific state within a given date range.
     *
     * @param state     the state for which to retrieve the dam information (must not be null)
     * @param startDate the start date of the date range for which to retrieve dam information (must not be null)
     * @param endDate   the end date of the date range for which to retrieve dam information (must not be null)
     * @return a list of DamInfoResponse objects containing the information about dams in the given state within the date range
     * @throws NullPointerException if any of the parameters state, startDate, or endDate is null
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
     * Retrieves the information about a dam based on the specified SihKey.
     *
     * @param sihKey the SihKey to search for
     * @return a DamInfoResponse object containing the dam catalog entity and the list of daily measurement entities
     * @throws IllegalArgumentException if no DamCatalogEntity with the specified sihKey is found
     */
    public DamInfoResponse getDamsInfoBySihKey(final String sihKey) {
        Objects.requireNonNull(sihKey, "sihKey cannot be null.");

        final Optional<DamCatalogEntity> damCatalogEntityOptional = damCatalogRepository.findBySihKey(sihKey);

        if (damCatalogEntityOptional.isEmpty()) {
            throw new IllegalArgumentException("DamCatalogEntity with sihKey: " + sihKey + " not found");
        }

        final DamCatalogEntity damCatalogEntity = damCatalogEntityOptional.get();

        final List<DailyMeasurementEntity> dailyMeasurementEntityList =
                dailyMeasurementRepository.findByDamCatalogEntityOrderByMeasurementDateDesc(damCatalogEntity);

        return new DamInfoResponse(damCatalogEntity, dailyMeasurementEntityList);
    }

    /**
     * Retrieves the dam information based on the SIH key, start date, and end date.
     *
     * @param sihKey The SIH key used to identify the dam.
     * @param startDate The start date for fetching dam measurements.
     * @param endDate The end date for fetching dam measurements.
     * @return The dam information response containing the dam catalog entity and
     *         the list of daily measurement entities.
     * @throws IllegalArgumentException if the dam catalog entity with the given
     *         SIH key is not found.
     * @throws NullPointerException if any of the parameters (sihKey, startDate,
     *         endDate) is null.
     */
    public DamInfoResponse getDamsInfoBySihKey(final String sihKey, final LocalDate startDate, final LocalDate endDate) {
        Objects.requireNonNull(sihKey, "sihKey cannot be null.");
        Objects.requireNonNull(startDate, "startDate cannot be null.");
        Objects.requireNonNull(endDate, "endDate cannot be null.");

        final Optional<DamCatalogEntity> damCatalogEntityOptional = damCatalogRepository.findBySihKey(sihKey);

        if (damCatalogEntityOptional.isEmpty()) {
            throw new IllegalArgumentException("DamCatalogEntity with sihKey: " + sihKey + " not found");
        }

        final DamCatalogEntity damCatalogEntity = damCatalogEntityOptional.get();

        final List<DailyMeasurementEntity> dailyMeasurementEntityList =
                dailyMeasurementRepository.findByDamCatalogEntityAndMeasurementDateBetweenOrderByMeasurementDateDesc(damCatalogEntity, startDate, endDate);

        return new DamInfoResponse(damCatalogEntity, dailyMeasurementEntityList);
    }
}

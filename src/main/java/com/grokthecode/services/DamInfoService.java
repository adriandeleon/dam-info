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

    public List<DamInfoResponse> getDamsInfo() {
        final List<DamInfoResponse> damInfoResponseList = new ArrayList<>();

        for (final DamCatalogEntity damCatalogEntity : damCatalogRepository.findAll()) {
            final List<DailyMeasurementEntity> dailyMeasurementEntityList = dailyMeasurementRepository.findByDamCatalogEntityOrderByIdDesc(damCatalogEntity);
            final DamInfoResponse damInfoResponse = new DamInfoResponse(damCatalogEntity, dailyMeasurementEntityList);

            damInfoResponseList.add(damInfoResponse);
            }

        return  damInfoResponseList;
    }

    public List<DamInfoResponse> getDamsInfoByDates(final LocalDate startDate, final LocalDate endDate) {
        Objects.requireNonNull(startDate, "startDate cannot be null.");
        Objects.requireNonNull(endDate, "endDate cannot be null.");

        final List<DamInfoResponse> damInfoResponseList = new ArrayList<>();

        for (final DamCatalogEntity damCatalogEntity : damCatalogRepository.findAll()) {
            final List<DailyMeasurementEntity> dailyMeasurementEntityList =
                    dailyMeasurementRepository.findByMeasurementDateBetweenOrderByMeasurementDateAsc(startDate, endDate);
            final DamInfoResponse damInfoResponse = new DamInfoResponse(damCatalogEntity, dailyMeasurementEntityList);

            damInfoResponseList.add(damInfoResponse);
        }
        return damInfoResponseList;
    }

    public List<DamInfoResponse> getDamsInfoByState(final String state) {
        Objects.requireNonNull(state, "state cannot be null.");

        final List<DamInfoResponse> damInfoResponseList = new ArrayList<>();

        for (final DamCatalogEntity damCatalogEntity : damCatalogRepository.findByState(state)) {
            final List<DailyMeasurementEntity> dailyMeasurementEntityList =
                    dailyMeasurementRepository.findByDamCatalogEntityOrderByIdDesc(damCatalogEntity);
            final DamInfoResponse damInfoResponse = new DamInfoResponse(damCatalogEntity, dailyMeasurementEntityList);

            damInfoResponseList.add(damInfoResponse);
        }
        return damInfoResponseList;
    }

    public List<DamInfoResponse> getDamsInfoByState(final String state, final LocalDate startDate, final LocalDate endDate) {
        Objects.requireNonNull(state, "state cannot be null.");
        Objects.requireNonNull(startDate, "startDate cannot be null.");
        Objects.requireNonNull(endDate, "endDate cannot be null.");

        final List<DamInfoResponse> damInfoResponseList = new ArrayList<>();

        for (final DamCatalogEntity damCatalogEntity : damCatalogRepository.findByState(state)) {
            final List<DailyMeasurementEntity> dailyMeasurementEntityList =
                    dailyMeasurementRepository.findByDamCatalogEntityAndMeasurementDateBetweenOrderByMeasurementDateAsc(damCatalogEntity, startDate, endDate);
            final DamInfoResponse damInfoResponse = new DamInfoResponse(damCatalogEntity, dailyMeasurementEntityList);

            damInfoResponseList.add(damInfoResponse);
        }
        return damInfoResponseList;
    }

    public DamInfoResponse getDamsInfoBySihKey(final String sihKey) {
        final Optional<DamCatalogEntity> damCatalogEntityOptional = damCatalogRepository.findBySihKey(sihKey);

        if(damCatalogEntityOptional.isEmpty()) {
            throw new IllegalArgumentException("DamCatalogEntity with sihKey: " + sihKey + " not found");
        }

        final DamCatalogEntity damCatalogEntity = damCatalogEntityOptional.get();

        final List<DailyMeasurementEntity> dailyMeasurementEntityList =
                dailyMeasurementRepository.findByDamCatalogEntityOrderByIdDesc(damCatalogEntity);

        return new DamInfoResponse(damCatalogEntity, dailyMeasurementEntityList);
    }

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
                dailyMeasurementRepository.findByDamCatalogEntityAndMeasurementDateBetweenOrderByMeasurementDateAsc(damCatalogEntity, startDate, endDate);

        return new DamInfoResponse(damCatalogEntity, dailyMeasurementEntityList);
    }
}

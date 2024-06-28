package com.grokthecode.services;

import com.grokthecode.common.GlobalConstants;
import com.grokthecode.data.entities.DailyMeasurementEntity;
import com.grokthecode.data.entities.DamCatalogEntity;
import com.grokthecode.data.repositories.DailyMeasurementRepository;
import com.grokthecode.data.repositories.DamCatalogRepository;
import com.grokthecode.models.restapi.PresasDto;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class DailyMeasurementService {

    private static final Logger log = LoggerFactory.getLogger(DailyMeasurementService.class);
    private final DailyMeasurementRepository dailyMeasurementRepository;
    public final DamCatalogRepository damCatalogRepository;

    @Value("${app.datasource.url}")
    private String appDatasourceUrl;

    public DailyMeasurementEntity createDamDailyFill(final DailyMeasurementEntity dailyMeasurementEntity) {
        Objects.requireNonNull(dailyMeasurementEntity, "dailyMeasurementEntity" + GlobalConstants.MESSAGE_MUST_NOT_BE_NULL);

        //Check that the daily measurement does not exist.
        if ((dailyMesureExistsByDate(dailyMeasurementEntity.getDamCatalogEntity().getId(), dailyMeasurementEntity.getMeasurementDate()))) {
            throw new IllegalArgumentException("Daily measurement with damId " + dailyMeasurementEntity.getDamCatalogEntity().getId() + " and " +
                    "measurement date " + dailyMeasurementEntity.getMeasurementDate() + " already exists");
        }

        return dailyMeasurementRepository.save(dailyMeasurementEntity);
    }

    public DailyMeasurementService(final DailyMeasurementRepository dailyMeasurementRepository,
                                   final DamCatalogRepository damCatalogRepository) {
        this.dailyMeasurementRepository = dailyMeasurementRepository;
        this.damCatalogRepository = damCatalogRepository;
    }

    public List<DailyMeasurementEntity> getDailyMeasurements() {

        return List.copyOf(dailyMeasurementRepository.findAll());
    }

    public List<DailyMeasurementEntity> getDailyMeasurements(String sihKey) {
        Objects.requireNonNull(sihKey, "sihKey cannot be null.");

        final Optional<DamCatalogEntity> optionalDamCatalogEntity = damCatalogRepository.findBySihKey(sihKey);

        if (optionalDamCatalogEntity.isEmpty()) {
            throw new IllegalArgumentException(" dam with sihKey " + sihKey + " not found.");
        }

        return List.copyOf(dailyMeasurementRepository.findByDamCatalogEntityOrderByIdDesc(optionalDamCatalogEntity.get()));
    }

    public List<DailyMeasurementEntity> getDailyMeasurements(final LocalDate startDate, final LocalDate endDate) {
        Objects.requireNonNull(startDate, "startDate cannot be null.");
        Objects.requireNonNull(endDate, "endDate cannot be null.");

        return List.copyOf(dailyMeasurementRepository.findByMeasurementDateBetweenOrderByMeasurementDateAsc(startDate, endDate));
    }

    public List<DailyMeasurementEntity> getDailyMeasurements(final String sihKey, final LocalDate startDate, final LocalDate endDate) {
        Objects.requireNonNull(sihKey, "sihKey cannot be null.");
        Objects.requireNonNull(startDate, "startDate cannot be null.");
        Objects.requireNonNull(endDate, "endDate cannot be null.");

        final Optional<DamCatalogEntity> optionalDamCatalogEntity = damCatalogRepository.findBySihKey(sihKey);

        if (optionalDamCatalogEntity.isEmpty()) {
            throw new IllegalArgumentException(" dam with sihKey " + sihKey + " not found.");
        }

        return List.copyOf(dailyMeasurementRepository.findByDamCatalogEntityAndMeasurementDateBetweenOrderByMeasurementDateAsc(optionalDamCatalogEntity.get(), startDate, endDate));
    }

    public Pair<List<DailyMeasurementEntity>, List<String>> syncDamsDailyFill(final String formatedDate) throws URISyntaxException {
        Objects.requireNonNull(formatedDate, "formatedDate cannot be null or empty.");

        final RestClient restClient = RestClient.create();
        final String endpoint = appDatasourceUrl + formatedDate;

        final List<PresasDto> presasDtoList = restClient.get().uri(new URI(endpoint))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        final List<DailyMeasurementEntity> dailyMeasurementEntityList = new ArrayList<>();
        final List<String> syncErrorMessageList = new ArrayList<>();

        for (final PresasDto presasDto : presasDtoList) {
            final Optional<DamCatalogEntity> optionalDamCatalogEntity = damCatalogRepository.findBySihKey(presasDto.getClavesih());

            if (optionalDamCatalogEntity.isEmpty()) {
                throw new IllegalArgumentException(" dam with SihKey " + presasDto.getClavesih() + " not found.");
            }

            final DailyMeasurementEntity dailyMeasureMentEntity = new DailyMeasurementEntity(
                    presasDto.getElevacionactual(),
                    presasDto.getAlmacenaactual(),
                    presasDto.getLlenano(),
                    LocalDate.parse(formatedDate),
                    optionalDamCatalogEntity.get()
            );

            try {
                dailyMeasurementEntityList.add(createDamDailyFill(dailyMeasureMentEntity));
            } catch (IllegalArgumentException e) {
                log.info(e.getMessage());
                syncErrorMessageList.add(e.getMessage());
            }
        }
        return Pair.of(dailyMeasurementEntityList, syncErrorMessageList);
    }

    public boolean dailyMesureExistsByDate(final Long damId, final LocalDate localDate) {
        Objects.requireNonNull(damId, "damId cannot be null.");
        Objects.requireNonNull(localDate, "localDate cannot be null.");

        final Optional<DamCatalogEntity> optionalDamCatalogEntity = damCatalogRepository.findById(damId);

        if (optionalDamCatalogEntity.isEmpty()) {
            throw new IllegalArgumentException(" dam with damId " + damId + " not found");
        }
        return dailyMeasurementRepository.existsByDamCatalogEntityAndMeasurementDate(optionalDamCatalogEntity.get(), localDate);
    }
}

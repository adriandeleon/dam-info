package com.grokthecode.services;

import com.grokthecode.common.GlobalConstants;
import com.grokthecode.data.entities.DailyMeasurementEntity;
import com.grokthecode.data.entities.DamCatalogEntity;
import com.grokthecode.data.repositories.DailyMeasurementRepository;
import com.grokthecode.data.repositories.DamCatalogRepository;
import com.grokthecode.models.restapi.PresasDto;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The type Daily measurement service.
 */
@Service
@Transactional
public class DailyMeasurementService {

    private static final Logger log = LoggerFactory.getLogger(DailyMeasurementService.class);
    private final DailyMeasurementRepository dailyMeasurementRepository;
    /**
     * The Dam catalog repository.
     */
    public final DamCatalogRepository damCatalogRepository;

    @Value("${app.datasource.url}")
    private String appDatasourceUrl;

    /**
     * Create dam daily fill daily measurement entity.
     *
     * @param dailyMeasurementEntity the daily measurement entity
     * @return the daily measurement entity
     */
    public DailyMeasurementEntity createDamDailyFill(final DailyMeasurementEntity dailyMeasurementEntity) {
        Objects.requireNonNull(dailyMeasurementEntity, "dailyMeasurementEntity" + GlobalConstants.MESSAGE_MUST_NOT_BE_NULL);

        //Check that the daily measurement does not exist.
        if ((dailyMesureExistsByDate(dailyMeasurementEntity.getDamCatalogEntity().getId(), dailyMeasurementEntity.getMeasurementDate()))) {
            throw new IllegalArgumentException("Daily measurement with damId " + dailyMeasurementEntity.getDamCatalogEntity().getId() + " and " +
                    "measurement date " + dailyMeasurementEntity.getMeasurementDate() + " already exists");
        }

        return dailyMeasurementRepository.save(dailyMeasurementEntity);
    }

    /**
     * Instantiates a new Daily measurement service.
     *
     * @param dailyMeasurementRepository the daily measurement repository
     * @param damCatalogRepository       the dam catalog repository
     */
    public DailyMeasurementService(final DailyMeasurementRepository dailyMeasurementRepository,
                                   final DamCatalogRepository damCatalogRepository) {
        this.dailyMeasurementRepository = dailyMeasurementRepository;
        this.damCatalogRepository = damCatalogRepository;
    }

    /**
     * Gets daily measurements.
     *
     * @return the daily measurements
     */
    public List<DailyMeasurementEntity> getDailyMeasurements() {

        return List.copyOf(dailyMeasurementRepository.findAll());
    }

    /**
     * Gets daily measurements.
     *
     * @param sihKey the sih key
     * @return the daily measurements
     */
    public List<DailyMeasurementEntity> getDailyMeasurements(String sihKey) {
        Objects.requireNonNull(sihKey, "sihKey cannot be null.");

        final Optional<DamCatalogEntity> optionalDamCatalogEntity = damCatalogRepository.findBySihKey(sihKey);

        if (optionalDamCatalogEntity.isEmpty()) {
            throw new IllegalArgumentException(" dam with sihKey " + sihKey + " not found.");
        }

        return List.copyOf(dailyMeasurementRepository.findByDamCatalogEntityOrderByMeasurementDateDesc(optionalDamCatalogEntity.get()));
    }

    /**
     * Gets daily measurements.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return the daily measurements
     */
    public List<DailyMeasurementEntity> getDailyMeasurements(final LocalDate startDate, final LocalDate endDate) {
        Objects.requireNonNull(startDate, "startDate cannot be null.");
        Objects.requireNonNull(endDate, "endDate cannot be null.");

        return List.copyOf(dailyMeasurementRepository.findByMeasurementDateBetweenOrderByMeasurementDateDesc(startDate, endDate));
    }

    /**
     * Gets daily measurements.
     *
     * @param sihKey    the sih key
     * @param startDate the start date
     * @param endDate   the end date
     * @return the daily measurements
     */
    public List<DailyMeasurementEntity> getDailyMeasurements(final String sihKey, final LocalDate startDate, final LocalDate endDate) {
        Objects.requireNonNull(sihKey, "sihKey cannot be null.");
        Objects.requireNonNull(startDate, "startDate cannot be null.");
        Objects.requireNonNull(endDate, "endDate cannot be null.");

        final Optional<DamCatalogEntity> optionalDamCatalogEntity = damCatalogRepository.findBySihKey(sihKey);

        if (optionalDamCatalogEntity.isEmpty()) {
            throw new IllegalArgumentException(" dam with sihKey " + sihKey + " not found.");
        }

        return List.copyOf(dailyMeasurementRepository.findByDamCatalogEntityAndMeasurementDateBetweenOrderByMeasurementDateDesc(optionalDamCatalogEntity.get(), startDate, endDate));
    }

    /**
     * Sync dams daily fill pair.
     *
     * @param formatedDate the formated date
     * @return the pair
     * @throws URISyntaxException the uri syntax exception
     */
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
        return Pair.of(List.copyOf(dailyMeasurementEntityList), List.copyOf(syncErrorMessageList));
    }

    /**
     * Sync dams daily fill list.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return the list
     * @throws URISyntaxException     the uri syntax exception
     * @throws DateTimeParseException the date time parse exception
     */
    public List<Pair<List<DailyMeasurementEntity>, List<String>>> syncDamsDailyFill(final String startDate, final String endDate) throws URISyntaxException, DateTimeParseException {
        Objects.requireNonNull(startDate, "startDate cannot be null or empty.");
        Objects.requireNonNull(endDate, "endDate cannot be null or empty.");

        final List<Pair<List<DailyMeasurementEntity>, List<String>>> dailyMeasurementEntityList = new ArrayList<>();

        final LocalDate parsedStartDate =LocalDate.parse(startDate);
        final LocalDate parsedEndDate = LocalDate.parse(endDate);

        final List<LocalDate> localDateList = generateDatesBetween(parsedStartDate, parsedEndDate);

        for (final LocalDate localDate : localDateList) {
            pause(10);
            dailyMeasurementEntityList.add(syncDamsDailyFill(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)));
        }

        return dailyMeasurementEntityList;
    }

    /**
     * Daily mesure exists by date boolean.
     *
     * @param damId     the dam id
     * @param localDate the local date
     * @return the boolean
     */
    public boolean dailyMesureExistsByDate(final Long damId, final LocalDate localDate) {
        Objects.requireNonNull(damId, "damId cannot be null.");
        Objects.requireNonNull(localDate, "localDate cannot be null.");

        final Optional<DamCatalogEntity> optionalDamCatalogEntity = damCatalogRepository.findById(damId);

        if (optionalDamCatalogEntity.isEmpty()) {
            throw new IllegalArgumentException(" dam with damId " + damId + " not found");
        }
        return dailyMeasurementRepository.existsByDamCatalogEntityAndMeasurementDate(optionalDamCatalogEntity.get(), localDate);
    }

    private List<LocalDate> generateDatesBetween(final LocalDate startDate, final LocalDate endDate) {
        long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate);

        return IntStream.rangeClosed(0, (int) numOfDaysBetween)
                .mapToObj(startDate::plusDays)
                .collect(Collectors.toList());
    }

    private void pause(final int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Sleep interrupted: {}", e.getMessage());
        }
    }
}

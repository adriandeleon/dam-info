package com.grokthecode.services;

import com.grokthecode.common.GlobalConstants;
import com.grokthecode.data.entities.DailyMeasurementEntity;
import com.grokthecode.data.entities.DamCatalogEntity;
import com.grokthecode.data.repositories.DailyMeasurementRepository;
import com.grokthecode.data.repositories.DamCatalogRepository;
import com.grokthecode.data.responses.DailyMeasurementSyncResponse;
import com.grokthecode.models.restapi.PresasDto;
import com.grokthecode.services.exceptions.DailyMeasurementAlreadyExistsException;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
public class DailyMeasurementService {

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
    public DailyMeasurementEntity createDamDailyMeasurement(final DailyMeasurementEntity dailyMeasurementEntity) throws DailyMeasurementAlreadyExistsException {
        Objects.requireNonNull(dailyMeasurementEntity, "dailyMeasurementEntity" + GlobalConstants.MESSAGE_MUST_NOT_BE_NULL);

        //Check that the daily measurement does not exist.
        if ((dailyMesureExistsByDate(dailyMeasurementEntity.getDamCatalogEntity().getId(), dailyMeasurementEntity.getMeasurementDate()))) {
            throw new DailyMeasurementAlreadyExistsException(dailyMeasurementEntity.getDamCatalogEntity().getId(),
                    dailyMeasurementEntity.getMeasurementDate().toString());
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
    public DailyMeasurementSyncResponse syncDamsDailyFill(final String formatedDate) throws URISyntaxException {
        Objects.requireNonNull(formatedDate, "formatedDate cannot be null or empty.");

        // Call the endpoint to get the measurements
        final RestClient restClient = RestClient.create();
        final String endpoint = appDatasourceUrl + formatedDate;

        final List<PresasDto> presasDtoList = restClient.get().uri(new URI(endpoint))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        // Create the new measurement and error lists.
        final List<DailyMeasurementEntity> dailyMeasurementEntityList = new ArrayList<>();
        final List<String> syncErrorMessageList = new ArrayList<>();

        assert presasDtoList != null;
        for (final PresasDto presasDto : presasDtoList) {
            final Optional<DamCatalogEntity> optionalDamCatalogEntity = damCatalogRepository.findBySihKey(presasDto.getClavesih());

            if (optionalDamCatalogEntity.isEmpty()) { //TODO: replace exception.
                throw new IllegalArgumentException(" dam with SihKey " + presasDto.getClavesih() + " not found.");
            }

            final DailyMeasurementEntity dailyMeasureMentEntity = new DailyMeasurementEntity(
                    presasDto.getElevacionactual(),
                    presasDto.getAlmacenaactual(),
                    presasDto.getLlenano(),
                    LocalDate.parse(formatedDate),
                    optionalDamCatalogEntity.get()
            );

            try { //Try to create a new daily measurement....
                dailyMeasurementEntityList.add(createDamDailyMeasurement(dailyMeasureMentEntity));
            } catch (DailyMeasurementAlreadyExistsException e) {
                log.info(e.getMessage()); //If not, add to the sync error list.
                syncErrorMessageList.add(e.getMessage());
            }
        }

        return new DailyMeasurementSyncResponse(formatedDate, dailyMeasurementEntityList.size(), dailyMeasurementEntityList, syncErrorMessageList);
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
    public List<DailyMeasurementSyncResponse> syncDamsDailyFill(final String startDate, final String endDate) throws URISyntaxException, DateTimeParseException {
        Objects.requireNonNull(startDate, "startDate cannot be null or empty.");
        Objects.requireNonNull(endDate, "endDate cannot be null or empty.");

        final List<DailyMeasurementSyncResponse> dailyMeasurementEntityList = new ArrayList<>();

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

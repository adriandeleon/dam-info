package com.grokthecode.controllers;

import com.grokthecode.data.entities.DailyMeasurementEntity;
import com.grokthecode.data.requests.DailyMeasurementDatesRequest;
import com.grokthecode.data.requests.DailyMeasurementRequest;
import com.grokthecode.data.responses.DailyMeasurementSyncResponse;
import com.grokthecode.services.DailyMeasurementService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.atmosphere.config.service.Post;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@Log4j2
public class DailyMeasurementController {

    public final DailyMeasurementService dailyMeasurementService;

    public DailyMeasurementController(final DailyMeasurementService dailyMeasurementService) {
        this.dailyMeasurementService = dailyMeasurementService;
    }

    @GetMapping("/api/dams/measurements")
    public ResponseEntity<List<DailyMeasurementEntity>> getDailyAllMeasurements() {

        return ResponseEntity.ok(dailyMeasurementService.getDailyMeasurements());
    }

    @PostMapping("/api/dams/measurements")
    public ResponseEntity<List<DailyMeasurementEntity>> getDailyMeasurementsByDamId(@RequestBody final DailyMeasurementRequest dailyMeasurementRequest) {
        Objects.requireNonNull(dailyMeasurementRequest, "dailyMeasurementRequest cannot be null.");

        final String sihKey = dailyMeasurementRequest.sihKey();

        LocalDate startDate = null;
        try {
            startDate = LocalDate.parse(dailyMeasurementRequest.startDate());
        } catch (final DateTimeParseException ignored) {}

        LocalDate endDate;
        try {
            endDate = LocalDate.parse(dailyMeasurementRequest.endDate());
        } catch (final DateTimeParseException e) {
            log.info(e.getMessage());
            endDate = startDate;
        }

        if(StringUtils.isNotBlank(sihKey) && startDate != null) {
            return ResponseEntity.ok(dailyMeasurementService.getDailyMeasurements(sihKey, startDate, endDate));
        }

        if(StringUtils.isBlank(sihKey) && startDate != null) {
            return ResponseEntity.ok(dailyMeasurementService.getDailyMeasurements(startDate, endDate));
        }

        if(StringUtils.isNotBlank(sihKey) && startDate == null && endDate == null) {
            return ResponseEntity.ok(dailyMeasurementService.getDailyMeasurements(sihKey));
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/api/dams/measurements/sync/date/today")
    public ResponseEntity<DailyMeasurementSyncResponse> syncDailyMeasurements() throws URISyntaxException {
        final Pair<List<DailyMeasurementEntity>, List<String>> resultPair = dailyMeasurementService.syncDamsDailyFill(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        final DailyMeasurementSyncResponse dailyMeasurementSyncResponse =
                new DailyMeasurementSyncResponse(resultPair.getLeft().size(), resultPair.getLeft(), resultPair.getRight());

        return ResponseEntity.ok(dailyMeasurementSyncResponse);
    }

    @GetMapping("/api/dams/measurements/sync/date/{formatedDate}")
    public  ResponseEntity<DailyMeasurementSyncResponse> syncDailyMeasurements(@PathVariable(required = false) String formatedDate) throws URISyntaxException {
        Objects.requireNonNull(formatedDate, "formatedDate cannot be null.");

        final Pair<List<DailyMeasurementEntity>, List<String>> resultPair = dailyMeasurementService.syncDamsDailyFill(formatedDate);

        final DailyMeasurementSyncResponse dailyMeasurementSyncResponse =
                new DailyMeasurementSyncResponse(resultPair.getLeft().size(), resultPair.getLeft(), resultPair.getRight());

        return ResponseEntity.ok(dailyMeasurementSyncResponse);
    }

    @PostMapping("/api/dams/measurements/sync/dates")
    public  ResponseEntity<List<DailyMeasurementSyncResponse>> syncDailyMeasurements(@RequestBody DailyMeasurementDatesRequest dailyMeasurementDatesRequest) throws URISyntaxException {
        Objects.requireNonNull(dailyMeasurementDatesRequest, "y cannot be null.");

        final String startDate = dailyMeasurementDatesRequest.startDate();
        String endDate = dailyMeasurementDatesRequest.endDate();

        if(StringUtils.isBlank(endDate) || endDate.equals("string")) {
            endDate = startDate;
        }

        final List<Pair<List<DailyMeasurementEntity>, List<String>>> dailyMeasurementEntityList  = dailyMeasurementService.syncDamsDailyFill(startDate, endDate);
        final List<DailyMeasurementSyncResponse> dailyMeasurementSyncResponseList = new ArrayList<>();

        for (Pair<List<DailyMeasurementEntity>, List<String>> listListPair : dailyMeasurementEntityList) {
            dailyMeasurementSyncResponseList.add(new DailyMeasurementSyncResponse(listListPair.getLeft().size(), listListPair.getLeft(), listListPair.getRight()));
        }

        return ResponseEntity.ok(dailyMeasurementSyncResponseList);
    }
}

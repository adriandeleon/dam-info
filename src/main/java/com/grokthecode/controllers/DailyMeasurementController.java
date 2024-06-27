package com.grokthecode.controllers;

import com.grokthecode.data.entities.DailyMeasurementEntity;
import com.grokthecode.services.DailyMeasurementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class DailyMeasurementController {

    public final DailyMeasurementService dailyMeasurementService;

    public DailyMeasurementController(final DailyMeasurementService dailyMeasurementService) {
        this.dailyMeasurementService = dailyMeasurementService;
    }

    @GetMapping("/api/dams/measurements")
    public ResponseEntity<List<DailyMeasurementEntity>> getDailyAllMeasurements() {

        return ResponseEntity.ok(dailyMeasurementService.getDailyMeasurements());
    }

    @GetMapping("/api/dams/measurements/damId/{damId}")
    public ResponseEntity<List<DailyMeasurementEntity>> getDailyMeasurementsByDamId(@PathVariable final Long damId) {

        return ResponseEntity.ok(dailyMeasurementService.getDailyMeasurements(damId));
    }

    @GetMapping("/api/dams/measurements/startDate/{startDate}/endDate/{endDate}")
    public ResponseEntity<List<DailyMeasurementEntity>> getDailyMeasurementsByStartAndEndDate(@PathVariable final String startDate,
                                                                                              @PathVariable final String endDate) {

        return ResponseEntity.ok(dailyMeasurementService.getDailyMeasurements(LocalDate.parse(startDate), LocalDate.parse(endDate)));
    }

    @GetMapping("/api/dams/measurements/damId/{damId}/startDate/{startDate}/endDate/{endDate}")
    public ResponseEntity<List<DailyMeasurementEntity>> getDailyMeasurementsByStartAndEndDate(@PathVariable final Long damId,
                                                                                              @PathVariable final String startDate,
                                                                                              @PathVariable final String endDate) {

        return ResponseEntity.ok(dailyMeasurementService.getDailyMeasurements(damId, LocalDate.parse(startDate), LocalDate.parse(endDate)));
    }

    @GetMapping("/api/dams/measurements/sync/date/{formatedDate},/api/dams/measurements/sync")
    public ResponseEntity syncDailyMeasurements(@PathVariable(required = false) String formatedDate) throws URISyntaxException {
        if (formatedDate == null) {
            formatedDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        dailyMeasurementService.syncDamsDailyFill(formatedDate);

        return ResponseEntity.ok().build();
    }
}

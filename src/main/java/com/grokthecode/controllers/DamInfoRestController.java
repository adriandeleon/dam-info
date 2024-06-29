package com.grokthecode.controllers;

import com.grokthecode.data.requests.DamInfoDatesRequest;
import com.grokthecode.data.requests.DamInfoSihKeyRequest;
import com.grokthecode.data.requests.DamInfoStateRequest;
import com.grokthecode.data.responses.DamInfoResponse;
import com.grokthecode.services.DamInfoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

@RestController
@Log4j2
public class DamInfoRestController {
    final private DamInfoService damInfoService;

    public DamInfoRestController(final DamInfoService damInfoService) {
        this.damInfoService = damInfoService;
    }

    @GetMapping("/api/dams/info")
    public ResponseEntity<List<DamInfoResponse>> getAllDamsInfo() {

        return ResponseEntity.ok(damInfoService.getDamsInfo());
    }

    @PostMapping("/api/dams/info/sihKey")
    public ResponseEntity<DamInfoResponse> getAllDamsInfoBySihKey(@RequestBody final DamInfoSihKeyRequest damInfoSihKeyRequest) {
        Objects.requireNonNull(damInfoSihKeyRequest, "DamInfoSihKeyRequest cannot be null.");

        ResponseEntity<DamInfoResponse> responseEntity = null;
        final String sihKey = damInfoSihKeyRequest.sihKey();

        LocalDate startDate = null;
        try {
            startDate = LocalDate.parse(damInfoSihKeyRequest.startDate());
        } catch (final DateTimeParseException ignored) {}

        LocalDate endDate;
        try {
            endDate = LocalDate.parse(damInfoSihKeyRequest.endDate());
        } catch (final DateTimeParseException e) {
            log.info(e.getMessage());
            endDate = startDate;
        }

        if(sihKey != null & startDate != null) {
            responseEntity = ResponseEntity.ok(damInfoService.getDamsInfoBySihKey(sihKey, startDate, endDate));
        }

        if( sihKey != null & startDate == null && endDate == null ) {
            responseEntity = ResponseEntity.ok(damInfoService.getDamsInfoBySihKey(sihKey));
        }

        return responseEntity;
    }

    @PostMapping("/api/dams/info/state")
    public ResponseEntity<List<DamInfoResponse>> getAllDamsInfoByState(@RequestBody final DamInfoStateRequest damInfoStateRequest) {
        Objects.requireNonNull(damInfoStateRequest, "DamInfoStateRequest cannot be null.");

        ResponseEntity<List<DamInfoResponse>> responseEntity = null;
        final String state = damInfoStateRequest.state();

        LocalDate startDate = null;
        try {
            startDate = LocalDate.parse(damInfoStateRequest.startDate());
        } catch (final DateTimeParseException ignored) {}

        LocalDate endDate = null;
        try {
            endDate = LocalDate.parse(damInfoStateRequest.endDate());
        } catch (final DateTimeParseException ignored) {}

        if( state != null & startDate != null && endDate != null ) {
            responseEntity = ResponseEntity.ok(damInfoService.getDamsInfoByState(state, startDate, endDate));
        }

        if( state != null & startDate == null && endDate == null ) {
            responseEntity = ResponseEntity.ok(damInfoService.getDamsInfoByState(state));
        }

        return responseEntity;
    }

    @PostMapping("/api/dams/info/dates")
    public ResponseEntity<List<DamInfoResponse>> getAllDamsInfoByState(@RequestBody final DamInfoDatesRequest damInfoDatesRequest) {
        Objects.requireNonNull(damInfoDatesRequest, "DamInfoDatesRequest cannot be null.");

        ResponseEntity<List<DamInfoResponse>> responseEntity = null;

        LocalDate startDate = null;
        try {
            startDate = LocalDate.parse(damInfoDatesRequest.startDate());
        } catch (final DateTimeParseException ignored) {}

        LocalDate endDate = null;
        try {
            endDate = LocalDate.parse(damInfoDatesRequest.endDate());
        } catch (final DateTimeParseException ignored) {}

        if(startDate != null && endDate != null ) {
            responseEntity = ResponseEntity.ok(damInfoService.getDamsInfoByDates(startDate, endDate));
        }

        return responseEntity;
    }
}

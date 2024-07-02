package com.grokthecode.controllers;

import com.grokthecode.controllers.exceptions.ResourceNotFoundException;
import com.grokthecode.controllers.exceptions.SyncDamCatalogException;
import com.grokthecode.data.entities.DamCatalogEntity;
import com.grokthecode.data.responses.DamCatalogSyncResponse;
import com.grokthecode.data.responses.HttpErrorResponse;
import com.grokthecode.services.DamCatalogService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@Log4j2
public class DamCatalogRestController {

    public final DamCatalogService damCatalogService;

    public DamCatalogRestController(final DamCatalogService damCatalogService) {
        this.damCatalogService = damCatalogService;
    }

    @GetMapping("/api/dams/catalog")
    public ResponseEntity<List<DamCatalogEntity>> damns() {

        return ResponseEntity.ok(damCatalogService.listAllDams());
    }

    @GetMapping("/api/dams/catalog/sihKey/{sihKey}")
    public ResponseEntity<DamCatalogEntity> getDamCatalogBySihKey(@PathVariable final String sihKey) throws ResourceNotFoundException {
        Objects.requireNonNull(sihKey, "sihKey cannot be null.");

        final Optional<DamCatalogEntity> optionalDamCatalogEntity = damCatalogService.getDamCatalogBySihKey(sihKey);

        if (optionalDamCatalogEntity.isEmpty()) {
            throw new ResourceNotFoundException(sihKey);
        }

        return ResponseEntity.ok(optionalDamCatalogEntity.get());
    }

    @GetMapping("/api/dams/catalog/state/{state}")
    public ResponseEntity<?> getDamCatalogByState(@PathVariable final String state) throws ResourceNotFoundException {
        Objects.requireNonNull(state, "state cannot be null.");

        final List<DamCatalogEntity> damCatalogEntityList = damCatalogService.getDamCatalogByState(state);

        if(damCatalogEntityList.isEmpty()) {
            throw new ResourceNotFoundException(state);
        }

        return ResponseEntity.ok(damCatalogEntityList);
    }

    @GetMapping("/api/dams/catalog/sync")
    public ResponseEntity<DamCatalogSyncResponse> syncDamCatalog() throws SyncDamCatalogException {

        final Pair<List<DamCatalogEntity>,List<String>> pairResponse;

        try {
            pairResponse = damCatalogService.syncDamsCatalog();
        } catch (Exception e) {
            throw new SyncDamCatalogException(e.getMessage());
        }

        final DamCatalogSyncResponse damCatalogSyncResponse =
                new DamCatalogSyncResponse(pairResponse.getLeft().size(), pairResponse.getLeft(), pairResponse.getRight());

        return ResponseEntity.ok(damCatalogSyncResponse);
    }
}

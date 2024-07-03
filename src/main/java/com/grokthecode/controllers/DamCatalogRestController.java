package com.grokthecode.controllers;

import com.grokthecode.services.exceptions.ResourceNotFoundException;
import com.grokthecode.services.exceptions.SyncDamCatalogException;
import com.grokthecode.data.entities.DamCatalogEntity;
import com.grokthecode.data.responses.DamCatalogSyncResponse;
import com.grokthecode.services.DamCatalogService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

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

       return ResponseEntity.ok(damCatalogService.getDamCatalogBySihKey(sihKey));
    }

    @GetMapping("/api/dams/catalog/state/{state}")
    public ResponseEntity<List<DamCatalogEntity>> getDamCatalogByState(@PathVariable final String state) throws ResourceNotFoundException {
        Objects.requireNonNull(state, "state cannot be null.");

        return ResponseEntity.ok(damCatalogService.getDamCatalogByState(state));
    }

    @GetMapping("/api/dams/catalog/sync")
    public ResponseEntity<DamCatalogSyncResponse> syncDamCatalog() throws SyncDamCatalogException {
        try {
            return ResponseEntity.ok(damCatalogService.syncDamsCatalog());
        } catch (SyncDamCatalogException e) {
            throw new SyncDamCatalogException(e.getMessage());
        }
    }
}
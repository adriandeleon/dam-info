package com.grokthecode.controllers;

import com.grokthecode.data.entities.DamCatalogEntity;
import com.grokthecode.data.responses.DamCatalogSyncResponse;
import com.grokthecode.services.DamCatalogService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.net.URISyntaxException;
import java.util.List;

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
    public ResponseEntity<DamCatalogEntity> getDamCatalogBySihKey(@PathVariable final String sihKey) {

        return ResponseEntity.ok(damCatalogService.getDamCatalogBySihKey(sihKey).orElseThrow());
    }

    @GetMapping("/api/dams/catalog/state/{state}")
    public ResponseEntity<List<DamCatalogEntity>> getDamCatalogByState(@PathVariable final String state) {

        return ResponseEntity.ok(damCatalogService.getDamCatalogByState(state));
    }

    @GetMapping("/api/dams/catalog/sync")
    public ResponseEntity<DamCatalogSyncResponse> syncDamCatalog() throws URISyntaxException {
        final Pair<List<DamCatalogEntity>,List<String>> pairResponse =  damCatalogService.syncDamsCatalog();

        final DamCatalogSyncResponse damCatalogSyncResponse =
                new DamCatalogSyncResponse(pairResponse.getLeft().size(), pairResponse.getLeft(), pairResponse.getRight());
        return ResponseEntity.ok(damCatalogSyncResponse);
    }
}

package com.grokthecode.controllers;

import com.grokthecode.data.entities.DamCatalogEntity;
import com.grokthecode.services.DamCatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.net.URISyntaxException;
import java.util.List;

@RestController
public class DamRestController {

    public DamCatalogService damCatalogService;

    public  DamRestController(DamCatalogService damCatalogService) {
        this.damCatalogService = damCatalogService;
    }

    @GetMapping("/api/dams")
    public ResponseEntity<List<DamCatalogEntity>> damns() {

        return ResponseEntity.ok(damCatalogService.listAllDams());
    }

    @GetMapping("/api/dams/sihKey/{sihKey}")
    public ResponseEntity<DamCatalogEntity> getDamCatalogBySihKey(@PathVariable final String sihKey) {

        return ResponseEntity.ok(damCatalogService.getDamCatalogBySihKey(sihKey).orElseThrow());
    }

    @GetMapping("/api/dams/state/{state}")
    public ResponseEntity<List<DamCatalogEntity>> getDamCatalogByState(@PathVariable final String state) {

        return ResponseEntity.ok(damCatalogService.getDamCatalogByState(state));
    }

    @GetMapping("/api/catalogs/dams/sync")
    public ResponseEntity syncDamCatalog() throws URISyntaxException {
        damCatalogService.syncDamsCatalog();

        return ResponseEntity.ok().build();
    }
}

package com.grokthecode.controllers;

import com.grokthecode.data.responses.DamInfoResponse;
import com.grokthecode.services.DamInfoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@Log4j2
public class DamInfoRestController {
    final private DamInfoService damInfoService;

    public DamInfoRestController(DamInfoService damInfoService) {
        this.damInfoService = damInfoService;
    }

    @GetMapping("/api/dams/info")
    public ResponseEntity<List<DamInfoResponse>> getAllDamsInfo() {
        return ResponseEntity.ok(damInfoService.getAllDamsInfo());
    }
}

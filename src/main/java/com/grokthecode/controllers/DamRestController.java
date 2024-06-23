package com.grokthecode.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DamRestController {

    @GetMapping("/api/dams")
    public ResponseEntity<String> damns() {
        return ResponseEntity.ok("Hello API");
    }
}

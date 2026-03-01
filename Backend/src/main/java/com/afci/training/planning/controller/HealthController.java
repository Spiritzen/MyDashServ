package com.afci.training.planning.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

  @GetMapping
  public Map<String, Object> ping() {
    return Map.of(
        "status", "OK",
        "ts", Instant.now().toString()
    );
  }
}

// src/main/java/com/afci/training/planning/controller/AdminTestController.java
package com.afci.training.planning.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminTestController {
  @GetMapping("/api/admin/ping")
  public String ping() {
    return "pong-admin";
  }
}

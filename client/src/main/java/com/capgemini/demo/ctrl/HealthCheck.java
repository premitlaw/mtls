package com.capgemini.demo.ctrl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@EnableScheduling
public class HealthCheck {

  @Value("${targetSrvUrl}")
  private String targetSrvUrl;

  private final String PATH = "/healthcheck";

  @Autowired RestTemplate restTemplate;

  @GetMapping(value="/healthcheck", produces = "text/plain")
  public ResponseEntity getCheck() {
    return ResponseEntity.ok("OK");
  }

  @GetMapping(value="/targetsrv/healthcheck", produces = "text/plain")
  public ResponseEntity<String> getClientCheck() {

    ResponseEntity resp = restTemplate.getForEntity(targetSrvUrl + PATH, String.class);
    return resp;
  }
}

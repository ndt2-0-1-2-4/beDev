package com.example.doan.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

@RestController
@RequestMapping("/football")
public class FootballController {

    @Value("${football.api.url}")
    private String apiUrl;

    @Value("${football.api.token}")
    private String apiToken;

    private final RestTemplate restTemplate;

    public FootballController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/matches")
    public ResponseEntity<String> getMatches(
            @RequestParam("dateFrom") String dateFrom,
            @RequestParam("dateTo") String dateTo) {
        try {
            String url = apiUrl + "?dateFrom=" + dateFrom + "&dateTo=" + dateTo;

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Auth-Token", apiToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Content-Type", "application/json");

            return ResponseEntity.status(response.getStatusCode())
                    .headers(responseHeaders)
                    .body(response.getBody());
        } catch (Exception e) {
            System.err.println("Error calling football API: " + e.getMessage());
            e.printStackTrace();

            HttpHeaders errorHeaders = new HttpHeaders();
            errorHeaders.set("Content-Type", "application/json");

            return ResponseEntity.status(500)
                    .headers(errorHeaders)
                    .body("{\"error\": \"Failed to fetch football data\", \"details\": \"" + e.getMessage() + "\"}");
        }
    }
}
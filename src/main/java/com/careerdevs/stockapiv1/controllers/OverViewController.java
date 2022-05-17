package com.careerdevs.stockapiv1.controllers;

import jdk.swing.interop.SwingInterOpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/overview")
public class OverViewController {

    @Autowired
    private Environment env;

    private final String BASE_URL = "https://www.alphavantage.co/query?function=OVERVIEW";

    // http://localhost:4000/api/overview/test
    @GetMapping("/test")
    public ResponseEntity<?> testOverview(RestTemplate restTemplate) {
        try {
            String url = BASE_URL + "&symbol=IBM&apikey=demo";

            String alphaVantageResponse = restTemplate.getForObject(url, String.class);
           // Object response = restTemplate.getForObject(url, Object.class);

            return ResponseEntity.ok(alphaVantageResponse);

        } catch (Exception e) {
            System.out.println(e.getClass());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //   // http://localhost:4000/api/overview/{symbol}
    @GetMapping("/{symbol}")
    public ResponseEntity<?> getOverviewBySymbol(RestTemplate restTemplate, @PathVariable String symbol ) {
        try {
            String url = BASE_URL + "&symbol=" + symbol + "&apikey=" + env.getProperty("AV_API_KEY");

            String alphaVantageResponse = restTemplate.getForObject(url, String.class);
            //Object response = restTemplate.getForObject(url, Object.class);

            return ResponseEntity.ok(alphaVantageResponse);

        } catch (Exception e) {
            System.out.println(e.getClass());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}



package com.careerdevs.stockapiv1.controllers;

import com.careerdevs.stockapiv1.models.Overview;
import com.careerdevs.stockapiv1.repositories.OverviewRepository;
import com.careerdevs.stockapiv1.utils.ApiErrorHandling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.zip.DataFormatException;

@RestController
@RequestMapping("/api/overview")
public class OverViewController {

    @Autowired
    private Environment env;

    @Autowired
    private OverviewRepository overviewRepository;


    private final String BASE_URL = "https://www.alphavantage.co/query?function=OVERVIEW";

    //GET ALL overview from sql database
    // return [] of all overview

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        try {
            Iterable<Overview> allSymbols = overviewRepository.findAll();
            return new ResponseEntity<>(allSymbols, HttpStatus.OK);
        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }


    //   // http://localhost:4000/api/overview/{symbol}
    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<?> getOverviewBySymbol(RestTemplate restTemplate, @PathVariable String symbol) {
        try {

            Overview foundOverview = overviewRepository.findBySymbol(symbol);
            //Object response = restTemplate.getForObject(url, Object.class);

            if (foundOverview == null) {
                return ApiErrorHandling.customApiError("Did not receive response from AV",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return ResponseEntity.ok(foundOverview);

        } catch (HttpClientErrorException e) {
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getOverviewById(@PathVariable("id") String id) {
        try {
            // control over error message and you get the 400. And code block is not needed.

            long overviewID = Integer.parseInt(id);

            Optional<Overview> foundOverview = overviewRepository.findById(overviewID);

            if (foundOverview.isEmpty()) {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, " User not found with ID: " + id);
            }
            return new ResponseEntity<>(foundOverview, HttpStatus.OK);

        } catch (HttpClientErrorException e) {
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }

    @PostMapping("/symbol/{symbol}")
    public ResponseEntity<?> uploadOverviewBySymbol(RestTemplate restTemplate, @PathVariable String symbol) {
        try {

            String apikey = env.getProperty("AV_API_KEY");
            String url = BASE_URL + "&symbol=" + symbol + "&apikey=" + apikey;

            Overview alphaVantageResponse = restTemplate.getForObject(url, Overview.class);
            //Object response = restTemplate.getForObject(url, Object.class);

            if (alphaVantageResponse == null) {
                return ApiErrorHandling.customApiError("Did not receive response from AV",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (alphaVantageResponse.getSymbol() == null) {
                return ApiErrorHandling.customApiError("Invalid stock symbol:" + symbol, HttpStatus.NOT_FOUND);

            }

            Overview savedOverview = overviewRepository.save(alphaVantageResponse);

            return ResponseEntity.ok(savedOverview);

        } catch (DataIntegrityViolationException e) {
            return ApiErrorHandling.customApiError("Can not upload duplicate Stock data",
                    HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }


    //Delete ALL overview from sql database
    // return # of deletes overview

    @DeleteMapping("/deleteall")
    public ResponseEntity<?> deleteAllSymbols() {
        try {

            long totalOverview = overviewRepository.count(); // count method whole number
            overviewRepository.deleteAll();

            return new ResponseEntity<Long>(totalOverview, HttpStatus.OK);

        } catch (HttpClientErrorException e) {
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> deleteOverviewById(@PathVariable("id") String id, RestTemplate restTemplate
    ) {
        try {

            long uID = Integer.parseInt(id);

            //check the range => other things to do

            Optional<Overview> foundUser = overviewRepository.findById(uID);

            if (foundUser.isEmpty()) {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "User Not Found with ID: " + id);
            }

            overviewRepository.deleteById(uID);

            return new ResponseEntity<>(foundUser, HttpStatus.OK);

        } catch (HttpClientErrorException e) {
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }

}



package com.careerdevs.stockapiv1.controllers;

import com.careerdevs.stockapiv1.models.Overview;
import com.careerdevs.stockapiv1.repositories.OverviewRepository;
import com.careerdevs.stockapiv1.utils.ApiError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RestController
@RequestMapping("/api/overview")
public class OverViewController {

    @Autowired
    private Environment env;

    @Autowired
    private OverviewRepository overviewRepository;


    private final String BASE_URL = "https://www.alphavantage.co/query?function=OVERVIEW";

    // GET ALL overview from sql database
    // return [] of all overview

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        try {
            Iterable<Overview> allOverviews = overviewRepository.findAll();

            return ResponseEntity.ok(allOverviews);

        } catch (HttpClientErrorException e) {
            return ApiError.customApiError(e.getMessage(), e.getStatusCode().value());

        } catch (Exception e) {
            return ApiError.genericApiError(e);
        }
    }


    // http://localhost:4000/api/overview/{symbol}
    @GetMapping("/{symbol}")
    public ResponseEntity<?> getOverviewBySymbol(RestTemplate restTemplate, @PathVariable String symbol) {
        try {

            Overview alphaVantageResponse = overviewRepository.findBySymbol(symbol);
            //Object response = restTemplate.getForObject(url, Object.class);

            if (alphaVantageResponse == null) {
                ApiError.throwErr(500, "Did not receive response from AV");
            } else if (alphaVantageResponse.getSymbol() == null) {
                ApiError.throwErr(500, "Invalid stock Symbol");
            }

            return ResponseEntity.ok(alphaVantageResponse);

        } catch (HttpClientErrorException e) {
            return ApiError.customApiError(e.getMessage(), e.getStatusCode().value());

        } catch (Exception e) {
            return ApiError.genericApiError(e);
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getOverviewById(@PathVariable("id") String id) {
        try {
            // control over error message and you get the 400. And code block is not needed.
            // long overviewID = Integer.parseInt(id);
            Optional<Overview> foundOverview = overviewRepository.findById(Long.parseLong(id));
            if (foundOverview.isEmpty()) {
                ApiError.throwErr(404, "ID: " + id + " did not match any overview.");
            }

            return ResponseEntity.ok(foundOverview);

        } catch (HttpClientErrorException e) {
            return ApiError.customApiError(e.getMessage(), e.getStatusCode().value());

        } catch (NumberFormatException e) {
            return ApiError.customApiError("ID must be a number: " + id, 404);

        } catch (Exception e) {
            return ApiError.genericApiError(e);
        }
    }

    @PostMapping("/{symbol}")
    // restTemplate is used to make external API request or HTTP request. Rest Template => request!
    public ResponseEntity<?> uploadOverviewBySymbol(RestTemplate restTemplate, @PathVariable String symbol) {
        try {

            String apikey = env.getProperty("AV_API_KEY");
            String url = BASE_URL + "&symbol=" + symbol + "&apikey=" + apikey;

            Overview alphaVantageResponse = restTemplate.getForObject(url, Overview.class);
            //Object response = restTemplate.getForObject(url, Object.class);

            if (alphaVantageResponse == null) {
                ApiError.throwErr(500, "Did not receive response from AV");
//                return ApiError.customApiError("Did not receive response from AV",
//                        500);
            } else if (alphaVantageResponse.getSymbol() == null) {
                ApiError.throwErr(404, "Invalid stock symbol:" + symbol);
            }

            Overview savedOverview = overviewRepository.save(alphaVantageResponse); // storing AV data to MySQL
            // database.

            return ResponseEntity.ok(savedOverview);

        } catch (HttpClientErrorException e) {
            return ApiError.customApiError(e.getMessage(), e.getStatusCode().value());

        } catch (DataIntegrityViolationException e) {
            return ApiError.customApiError("Can not upload duplicate Stock data",
                    400);

        } catch (Exception e) {
            return ApiError.genericApiError(e);
        }
    }


    @DeleteMapping("/all")
    public ResponseEntity<?> deleteAllSymbols() {
        try {

            long AllOverviewCount = overviewRepository.count(); // count method whole number
            if (AllOverviewCount == 0) {
                return ResponseEntity.ok("No overviews to delete!");
            }
            overviewRepository.deleteAll();

            return ResponseEntity.ok("Deleted Overviews: " + AllOverviewCount);

        } catch (HttpClientErrorException e) {
            return ApiError.customApiError(e.getMessage(), e.getStatusCode().value());

        } catch (Exception e) {
            return ApiError.genericApiError(e);
        }
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> deleteOverviewById(@PathVariable("id") String id, RestTemplate restTemplate
    ) {
        try {
            // Can cause NumberFormatException
            long overviewId = Integer.parseInt(id);

            //check the range => other things to do

            Optional<Overview> foundOverview = overviewRepository.findById(overviewId);

            if (foundOverview.isEmpty()) {
                ApiError.throwErr(404, id + " did not match any overview");
            }

            overviewRepository.deleteById(overviewId);

            return ResponseEntity.ok(foundOverview);

        } catch (HttpClientErrorException e) {
            return ApiError.customApiError(e.getMessage(), e.getStatusCode().value());

        } catch (NumberFormatException e) { //looking for a number
            return ApiError.customApiError("ID must be a number: " + id, 400);

        } catch (Exception e) {
            return ApiError.genericApiError(e);
        }
    }

}



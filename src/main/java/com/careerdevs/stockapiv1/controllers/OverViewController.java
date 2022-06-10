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

import java.util.ArrayList;
import java.util.List;



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
    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<?> getOverviewBySymbol(RestTemplate restTemplate, @PathVariable String symbol) {
        try {

            List<Overview> foundOverview = overviewRepository.findBySymbol(symbol);
            //Object response = restTemplate.getForObject(url, Object.class);

            if (foundOverview.isEmpty()) {
                ApiError.throwErr(404, symbol + " did not match overview");
            }

            return ResponseEntity.ok(foundOverview);

        } catch (HttpClientErrorException e) {
            return ApiError.customApiError(e.getMessage(), e.getStatusCode().value());

        } catch (Exception e) {
            return ApiError.genericApiError(e);
        }
    }


    @DeleteMapping("/{field}/{value}")
    public ResponseEntity<?> deleteOverviewByField(@PathVariable String field, @PathVariable String value) {
        try {

            List<Overview> foundOverview = null;
            //Better logic

            field = field.toLowerCase();

            switch (field) {
                case "id" -> foundOverview = overviewRepository.deleteById(Long.parseLong(value));
                case "symbol" -> foundOverview = overviewRepository.deleteBySymbol(value);
                case "sector" -> foundOverview = overviewRepository.deleteBySector(value);
                case "assetType" -> foundOverview = overviewRepository.deleteByAssetType(value);
                case "name" -> foundOverview = overviewRepository.deleteByName(value);
                case "currency" -> foundOverview = overviewRepository.deleteByCurrency(value);
                case "country" -> foundOverview = overviewRepository.deleteByCountry(value);
                case "exchange" -> foundOverview = overviewRepository.deleteByExchange(value);
            }

            if (foundOverview == null || foundOverview.isEmpty()) {
                ApiError.throwErr(404, field + " did not match any overview.");
            }

            return ResponseEntity.ok(foundOverview);

        } catch (HttpClientErrorException e) {
            return ApiError.customApiError(e.getMessage(), e.getStatusCode().value());

        } catch (NumberFormatException e) {
            return ApiError.customApiError("ID must be a number: " + field, 404);

        } catch (Exception e) {
            return ApiError.genericApiError(e);
        }
    }

    @GetMapping ("/{field}/{value}")
    public ResponseEntity<?> getOverviewByField(@PathVariable String field, @PathVariable String value) {
        try {

            List<Overview> foundOverview = null;
            //Better logic

            field = field.toLowerCase();

            switch (field) {
                case "id" -> foundOverview = overviewRepository.findById(Long.parseLong(value));
                case "symbol" -> foundOverview = overviewRepository.findBySymbol(value);
                case "sector" -> foundOverview = overviewRepository.findBySector(value);
                case "assetType" -> foundOverview = overviewRepository.findByAssetType(value);
                case "name" -> foundOverview = overviewRepository.findByName(value);
                case "currency" -> foundOverview = overviewRepository.findByCurrency(value);
                case "country" -> foundOverview = overviewRepository.findByCountry(value);
                case "exchange" -> foundOverview = overviewRepository.findByExchange(value);
            }

            if (foundOverview == null || foundOverview.isEmpty()) {
                ApiError.throwErr(404, field + " did not match any overview.");
            }

            return ResponseEntity.ok(foundOverview);

        } catch (HttpClientErrorException e) {
            return ApiError.customApiError(e.getMessage(), e.getStatusCode().value());

        } catch (NumberFormatException e) {
            return ApiError.customApiError("ID must be a number: " + field, 404);

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

    @PostMapping("/all")
    // restTemplate is used to make external API request or HTTP request. Rest Template => request!
    public ResponseEntity<?> uploadTestOverview(RestTemplate restTemplate) {
        try {
            String[] testSymbols = {"AAPL", "IBM", "AA", "A", "ABC"};
            ArrayList<Overview> overviews = new ArrayList<>();
            for (int i = 0; i < testSymbols.length; i++) {

                String symbol = testSymbols[i];

                String apikey = env.getProperty("AV_API_KEY");
                String url = BASE_URL + "&symbol=" + symbol + "&apikey=" + apikey;

                Overview alphaVantageResponse = restTemplate.getForObject(url, Overview.class);

                if (alphaVantageResponse == null) {
                    ApiError.throwErr(500, "Did not receive response from AV");
//
                } else if (alphaVantageResponse.getSymbol() == null) {
                    ApiError.throwErr(404, "Invalid stock symbol:" + symbol);
                }
                overviews.add(alphaVantageResponse);
            }

            Iterable<Overview> savedOverview = overviewRepository.saveAll(overviews);

            return ResponseEntity.ok("Saved!");

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

}

//    @DeleteMapping("/id/{id}")
//    public ResponseEntity<?> deleteOverviewById(@PathVariable("id") String id, RestTemplate restTemplate
//    ) {
//        try {
//            // Can cause NumberFormatException
//            long overviewId = Integer.parseInt(id);
//
//            //check the range => other things to do
//
//            List<Overview> foundOverview = overviewRepository.findById(overviewId);
//
//            if (foundOverview.isEmpty()) {
//                ApiError.throwErr(404, id + " did not match any overview");
//            }
//
//            overviewRepository.deleteById(overviewId);
//
//            return ResponseEntity.ok(foundOverview);
//
//        } catch (HttpClientErrorException e) {
//            return ApiError.customApiError(e.getMessage(), e.getStatusCode().value());
//
//        } catch (NumberFormatException e) { //looking for a number
//            return ApiError.customApiError("ID must be a number: " + id, 400);
//
//        } catch (Exception e) {
//            return ApiError.genericApiError(e);
//        }
//    }

// old code
//    @DeleteMapping("/symbol/{symbol}")
//    public ResponseEntity<?> deleteBySymbol( @PathVariable String symbol) {
//        try {
//
//            List<Overview> foundOverview = overviewRepository.findBySymbol(symbol);
//            //Object response = restTemplate.getForObject(url, Object.class);
//
//            if (foundOverview.isEmpty()) {
//                ApiError.throwErr(404, symbol + " did not match overview");
//            }
//            overviewRepository.deleteBySymbol(symbol);
//            return ResponseEntity.ok(foundOverview);
//
//        } catch (HttpClientErrorException e) {
//            return ApiError.customApiError(e.getMessage(), e.getStatusCode().value());
//
//        } catch (Exception e) {
//            return ApiError.genericApiError(e);
//        }
//    }




//    @GetMapping("/country/{country}")
//    public ResponseEntity<?> getOverviewByCountry(RestTemplate restTemplate, @PathVariable String country) {
//        try {
//
//            List<Overview> foundOverview = (List<Overview>) overviewRepository.findByCountry(country);
//            //Object response = restTemplate.getForObject(url, Object.class);
//
//            if (foundOverview.isEmpty()) {
//                ApiError.throwErr(404, country + " did not match overview");
//            }
//
//            return ResponseEntity.ok(foundOverview);
//
//        } catch (HttpClientErrorException e) {
//            return ApiError.customApiError(e.getMessage(), e.getStatusCode().value());
//
//        } catch (Exception e) {
//            return ApiError.genericApiError(e);
//        }
//    }
//
//    @GetMapping("/assettype/{assettype}")
//    public ResponseEntity<?> getOverviewByAssetType(RestTemplate restTemplate, @PathVariable String assettype) {
//        try {
//
//            List<Overview> foundOverview = overviewRepository.findByAssetType(assettype);
//            //Object response = restTemplate.getForObject(url, Object.class);
//
//            if (foundOverview.isEmpty()) {
//                ApiError.throwErr(404, assettype + " did not match overview");
//            }
//
//            return ResponseEntity.ok(foundOverview);
//
//        } catch (HttpClientErrorException e) {
//            return ApiError.customApiError(e.getMessage(), e.getStatusCode().value());
//
//        } catch (Exception e) {
//            return ApiError.genericApiError(e);
//        }
//    }
//
//    @GetMapping("/currency/{currency}")
//    public ResponseEntity<?> getOverviewByCurrency(RestTemplate restTemplate, @PathVariable String currency) {
//        try {
//
//            List<Overview> foundOverview = overviewRepository.findByCurrency(currency);
//            //Object response = restTemplate.getForObject(url, Object.class);
//
//            if (foundOverview.isEmpty()) {
//                ApiError.throwErr(404, currency + " did not match overview");
//            }
//
//            return ResponseEntity.ok(foundOverview);
//
//        } catch (HttpClientErrorException e) {
//            return ApiError.customApiError(e.getMessage(), e.getStatusCode().value());
//
//        } catch (Exception e) {
//            return ApiError.genericApiError(e);
//        }
//    }
//
//    @GetMapping("/sector/{sector}")
//    public ResponseEntity<?> getOverviewBySector(RestTemplate restTemplate, @PathVariable String sector) {
//        try {
//
//            List<Overview> foundOverview = overviewRepository.findBySector(sector);
//            //Object response = restTemplate.getForObject(url, Object.class);
//
//            if (foundOverview.isEmpty()) {
//                ApiError.throwErr(404, sector + " did not match overview");
//            }
//
//            return ResponseEntity.ok(foundOverview);
//
//        } catch (HttpClientErrorException e) {
//            return ApiError.customApiError(e.getMessage(), e.getStatusCode().value());
//
//        } catch (Exception e) {
//            return ApiError.genericApiError(e);
//        }
//    }
//
//    @GetMapping("/exchange/{exchange}")
//    public ResponseEntity<?> getOverviewByExchange(RestTemplate restTemplate, @PathVariable String exchange) {
//        try {
//
//            List<Overview> foundOverview = (List<Overview>) overviewRepository.findByExchange(exchange);
//            //Object response = restTemplate.getForObject(url, Object.class);
//
//            if (foundOverview.isEmpty()) {
//                ApiError.throwErr(404, exchange + " did not match overview");
//            }
//
//            return ResponseEntity.ok(foundOverview);
//
//        } catch (HttpClientErrorException e) {
//            return ApiError.customApiError(e.getMessage(), e.getStatusCode().value());
//
//        } catch (Exception e) {
//            return ApiError.genericApiError(e);
//        }
//    }
//
//    @GetMapping("/name/{name}")
//    public ResponseEntity<?> getOverviewByName(RestTemplate restTemplate, @PathVariable String name) {
//        try {
//
//            Optional<Overview> foundOverview = overviewRepository.findByName(name);
//            //Object response = restTemplate.getForObject(url, Object.class);
//
//            if (foundOverview.isEmpty()) {
//                ApiError.throwErr(404, name + " did not match overview");
//            }
//
//            return ResponseEntity.ok(foundOverview);
//
//        } catch (HttpClientErrorException e) {
//            return ApiError.customApiError(e.getMessage(), e.getStatusCode().value());
//
//        } catch (Exception e) {
//            return ApiError.genericApiError(e);
//        }
//    }

//    @GetMapping("/id/{id}")
//    public ResponseEntity<?> getOverviewById(@PathVariable("id") String id) {
//        try {
//            // control over error message and you get the 400. And code block is not needed.
//            // long overviewID = Integer.parseInt(id);
//            List<Overview> foundOverview = overviewRepository.findById(Long.parseLong(id));
//            if (foundOverview.isEmpty()) {
//                ApiError.throwErr(404, "ID: " + id + " did not match any overview.");
//            }
//
//            return ResponseEntity.ok(foundOverview.get(0));
//
//        } catch (HttpClientErrorException e) {
//            return ApiError.customApiError(e.getMessage(), e.getStatusCode().value());
//
//        } catch (NumberFormatException e) {
//            return ApiError.customApiError("ID must be a number: " + id, 404);
//
//        } catch (Exception e) {
//            return ApiError.genericApiError(e);
//        }
//    }


//    @DeleteMapping("/currency/{currency}")
//    public ResponseEntity<?> deleteByCurrency(RestTemplate restTemplate, @PathVariable String currency) {
//        try {
//
//            List<Overview> foundOverview = overviewRepository.deleteByCurrency(currency);
//            //Object response = restTemplate.getForObject(url, Object.class);
//
//            if (foundOverview.isEmpty()) {
//                ApiError.throwErr(404, currency + " did not match overview");
//            }
//            overviewRepository.deleteByCurrency(currency);
//            return ResponseEntity.ok(foundOverview);
//
//        } catch (HttpClientErrorException e) {
//            return ApiError.customApiError(e.getMessage(), e.getStatusCode().value());
//
//        } catch (Exception e) {
//            return ApiError.genericApiError(e);
//        }
//    }
//
//    @DeleteMapping("/country/{country}")
//    public ResponseEntity<?> deleteByCountry(RestTemplate restTemplate, @PathVariable String country) {
//        try {
//
//            List<Overview> foundOverview = overviewRepository.deleteByCountry(country);
//            //Object response = restTemplate.getForObject(url, Object.class);
//
//            if (foundOverview.isEmpty()) {
//                ApiError.throwErr(404, country + " did not match overview");
//            }
//            overviewRepository.deleteByCountry(country);
//            return ResponseEntity.ok(foundOverview);
//
//        } catch (HttpClientErrorException e) {
//            return ApiError.customApiError(e.getMessage(), e.getStatusCode().value());
//
//        } catch (Exception e) {
//            return ApiError.genericApiError(e);
//        }
//    }
//
//    @DeleteMapping("/exchange/{exchange}")
//    public ResponseEntity<?> deleteByExchange(RestTemplate restTemplate, @PathVariable String exchange) {
//        try {
//
//            List<Overview> foundOverview = overviewRepository.deleteByExchange(exchange);
//            //Object response = restTemplate.getForObject(url, Object.class);
//
//            if (foundOverview.isEmpty()) {
//                ApiError.throwErr(404, exchange + " did not match overview");
//            }
//            overviewRepository.deleteByExchange(exchange);
//            return ResponseEntity.ok(foundOverview);
//
//        } catch (HttpClientErrorException e) {
//            return ApiError.customApiError(e.getMessage(), e.getStatusCode().value());
//
//        } catch (Exception e) {
//            return ApiError.genericApiError(e);
//        }
//    }
//
//    @DeleteMapping("/assettype/{assettype}")
//    public ResponseEntity<?> deleteByAssetType(RestTemplate restTemplate, @PathVariable String assettype) {
//        try {
//
//            List<Overview> foundOverview = overviewRepository.deleteByAssetType(assettype);
//            //Object response = restTemplate.getForObject(url, Object.class);
//
//            if (foundOverview.isEmpty()) {
//                ApiError.throwErr(404, assettype + " did not match overview");
//            }
//            overviewRepository.deleteByAssetType(assettype);
//            return ResponseEntity.ok(foundOverview);
//
//        } catch (HttpClientErrorException e) {
//            return ApiError.customApiError(e.getMessage(), e.getStatusCode().value());
//
//        } catch (Exception e) {
//            return ApiError.genericApiError(e);
//        }
//    }
//
//    @DeleteMapping("/sector/{sector}")
//    public ResponseEntity<?> deleteBySector(RestTemplate restTemplate, @PathVariable String sector) {
//        try {
//
//            List<Overview> foundOverview = overviewRepository.deleteBySector(sector);
//            //Object response = restTemplate.getForObject(url, Object.class);
//
//            if (foundOverview.isEmpty()) {
//                ApiError.throwErr(404, sector + " did not match overview");
//            }
//            overviewRepository.deleteBySector(sector);
//            return ResponseEntity.ok(foundOverview);
//
//        } catch (HttpClientErrorException e) {
//            return ApiError.customApiError(e.getMessage(), e.getStatusCode().value());
//
//        } catch (Exception e) {
//            return ApiError.genericApiError(e);
//        }
//    }
//
//    @DeleteMapping("/symbol/{symbol}")
//    public ResponseEntity<?> deleteBySymbol(RestTemplate restTemplate, @PathVariable String symbol) {
//        try {
//
//            Optional<Overview> foundOverview = overviewRepository.deleteBySymbol(symbol);
//            //Object response = restTemplate.getForObject(url, Object.class);
//
//            if (foundOverview.isEmpty()) {
//                ApiError.throwErr(404, symbol + " did not match overview");
//            }
//            overviewRepository.deleteBySymbol(symbol);
//            return ResponseEntity.ok(foundOverview);
//
//        } catch (HttpClientErrorException e) {
//            return ApiError.customApiError(e.getMessage(), e.getStatusCode().value());
//
//        } catch (Exception e) {
//            return ApiError.genericApiError(e);
//        }
//    }
//
//    @DeleteMapping("/name/{name}")
//    public ResponseEntity<?> deleteByName(RestTemplate restTemplate, @PathVariable String name) {
//        try {
//
//            Optional<Overview> foundOverview = overviewRepository.deleteByName(name);
//            //Object response = restTemplate.getForObject(url, Object.class);
//
//            if (foundOverview.isEmpty()) {
//                ApiError.throwErr(404, name + " did not match overview");
//            }
//            overviewRepository.deleteByName(name);
//            return ResponseEntity.ok(foundOverview);
//
//        } catch (HttpClientErrorException e) {
//            return ApiError.customApiError(e.getMessage(), e.getStatusCode().value());
//
//        } catch (Exception e) {
//            return ApiError.genericApiError(e);
//        }
//    }





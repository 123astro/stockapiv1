package com.careerdevs.stockapiv1.repositories;

import com.careerdevs.stockapiv1.models.Overview;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface OverviewRepository extends CrudRepository<Overview, Long> {
        Optional<Overview> findBySymbol(String symbol);
        List<Overview> findByCountry(String country);
        List<Overview> findByExchange(String exchange);
        List<Overview> findByAssetType(String asset);
        List<Overview> findBySector(String sector);
        List<Overview> findByCurrency(String currency);
        Optional<Overview> findByName(String name);
        Optional<Overview> deleteByName(String name);
        List<Overview> deleteByCurrency(String currency);
        List<Overview> deleteByCountry(String country);
        List<Overview> deleteByExchange(String exchange);
        List<Overview> deleteByAssetType(String assettype);
        }

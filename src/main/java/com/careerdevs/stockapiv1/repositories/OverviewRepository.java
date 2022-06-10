package com.careerdevs.stockapiv1.repositories;

import com.careerdevs.stockapiv1.models.Overview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface OverviewRepository extends JpaRepository<Overview, Long> {
    List<Overview> findBySymbol(String symbol);

    List<Overview> findById(long id);

    List<Overview> findBySector(String sector);

    List<Overview> findByAssetType(String assetType);

    List<Overview> findByName(String name);

    List<Overview> findByCurrency(String currency);

    List<Overview> findByCountry(String country);

    List<Overview> findByExchange(String exchange);

    List<Overview> deleteById(long id);

    List<Overview> deleteBySymbol(String symbol);

    List<Overview> deleteBySector(String sector);

    List<Overview> deleteByAssetType(String assetType);

    List<Overview> deleteByName(String name);

    List<Overview> deleteByCurrency(String currency);

    List<Overview> deleteByCountry(String country);

    List<Overview> deleteByExchange(String exchange);


    //jpa is creating an SQL query with findBySymbol

}
//public interface OverviewRepository extends CrudRepository<Overview, Long> {
//        Optional<Overview> findBySymbol(String symbol);
//        List<Overview> findByCountry(String country);
//        List<Overview> findByExchange(String exchange);
//        List<Overview> findByAssetType(String asset);
//        List<Overview> findBySector(String sector);
//        List<Overview> findByCurrency(String currency);
//        Optional<Overview> findByName(String name);
//        Optional<Overview> deleteByName(String name);
//        List<Overview> deleteByCurrency(String currency);
//        List<Overview> deleteByCountry(String country);
//        List<Overview> deleteByExchange(String exchange);
//        List<Overview> deleteByAssetType(String assettype);
//        List<Overview> deleteBySector(String sector);
//        Optional<Overview> deleteBySymbol(String symbol);
//        }

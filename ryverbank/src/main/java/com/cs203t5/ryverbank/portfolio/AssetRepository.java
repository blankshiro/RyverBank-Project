package com.cs203t5.ryverbank.portfolio;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A AssetRepository that provides the mechanism for storage, retrieval,
 * search, update and delete operation on asset objects
 */
@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    /**
     * Optional query to find asset by asset code, portfolio id and whether it is
     * currently traded.
     * 
     * @param symbol
     * @param id     The id of the portfolio.
     * @param ans
     * @return The asset found.
     */
    Optional<Asset> findByCodeAndPortfolioIdAndIsTraded(String symbol, Long id, boolean ans);

    /**
     * Derived query to find asset by portfolio id.
     * 
     * @param id The id of the portfolio.
     * @return The list of assets found.
     */
    List<Asset> findAllByPortfolioId(Long id);

    // List<Asset> findAllByCodeAndPortfolioIdAndIsTraded(String symbol, Long id, boolean ans);

    // maybe should return back the asset and save it into another database?
    // void deleteByCodeAndCustomerId(String symbol, Long id);
}

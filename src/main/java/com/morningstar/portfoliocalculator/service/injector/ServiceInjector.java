package com.morningstar.portfoliocalculator.service.injector;

import com.morningstar.portfoliocalculator.service.PortfolioCalculatorService;
import com.morningstar.portfoliocalculator.service.exception.PortfolioCalculatorServiceException;

/**
 * Interface for ServiceInjector
 *
 * Created by ebrigand on 02/02/14.
 */
public interface ServiceInjector {

    /**
     * Get an instance of PortfolioCalculatorService with the dependent services injected
     *
     * @return
     * @throws PortfolioCalculatorServiceException
     */
    PortfolioCalculatorService getPortfolioCalculatorService() throws PortfolioCalculatorServiceException ;
}

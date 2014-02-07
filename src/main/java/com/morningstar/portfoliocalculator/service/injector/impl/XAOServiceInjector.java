package com.morningstar.portfoliocalculator.service.injector.impl;

import com.morningstar.portfoliocalculator.service.PortfolioCalculatorService;
import com.morningstar.portfoliocalculator.service.exception.PortfolioCalculatorServiceException;
import com.morningstar.portfoliocalculator.service.impl.PortfolioCalculatorServiceImpl;
import com.morningstar.portfoliocalculator.service.injector.ServiceInjector;
import com.morningstar.portfoliocalculator.xaoservice.exception.DataAccessException;
import com.morningstar.portfoliocalculator.xaoservice.impl.XAOLocationEnum;
import com.morningstar.portfoliocalculator.xaoservice.impl.XAOPortfolioService;
import com.morningstar.portfoliocalculator.xaoservice.impl.XAOSecurityService;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Implementation of the ServiceInjector with the injection of XAOPortfolioService and XAOSecurityService
 *
 * Created by ebrigand on 02/02/14.
 */
public class XAOServiceInjector implements ServiceInjector {

    @Override
    public PortfolioCalculatorService getPortfolioCalculatorService() throws PortfolioCalculatorServiceException {
        try{
            return PortfolioCalculatorServiceImpl.getInstance(XAOPortfolioService.getInstance(), XAOSecurityService.getInstance());
        } catch (DataAccessException e) {
            throw new PortfolioCalculatorServiceException(e.getMessage());
        }
    }
}

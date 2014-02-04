package com.morningstar.portfoliocalculator.service;

import com.morningstar.portfoliocalculator.model.portfolio.Portfolio;
import com.morningstar.portfoliocalculator.service.exception.PortfolioCalculatorServiceException;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Interface of PortfolioCalculatorService
 *
 * Created by ebrigand on 02/02/14.
 */
public interface PortfolioCalculatorService {

    /**
     * Compute the value of a portfolio for a date.
     * - Get all the transactions inferior to the date, and compute the amount of shares (related to the security id) with the amount and the price given by the transaction date
     * - Add or remove the shares of the total shares related to the security id (BUY or SELL)
     * - For the total shares related to the security id, compute the price related to the date
     *
     * @param portfolioId
     * @param date
     * @return
     * @throws PortfolioCalculatorServiceException
     */
    BigDecimal computePortfolioValue(int portfolioId, Date date) throws PortfolioCalculatorServiceException;

    /**
     * Create the best portfolio between a start and a end date
     * For each day, find the security with the best ratio between the current price and the price for the closest next day
     * In the case where the same security is bought twice, both transactions are merge in one
     *
     * @param investPrice
     * @param startDate
     * @param endDate
     * @return
     * @throws PortfolioCalculatorServiceException
     */
    Portfolio createBestPortfolio(int investPrice, Date startDate, Date endDate) throws PortfolioCalculatorServiceException;
}

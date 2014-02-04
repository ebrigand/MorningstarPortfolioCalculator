package com.morningstar.portfoliocalculator.service.impl;

import com.morningstar.portfoliocalculator.model.portfolio.*;
import com.morningstar.portfoliocalculator.model.security.HistoryDetailType;
import com.morningstar.portfoliocalculator.model.security.Security;
import com.morningstar.portfoliocalculator.service.PortfolioCalculatorService;
import com.morningstar.portfoliocalculator.service.exception.PortfolioCalculatorServiceException;
import com.morningstar.portfoliocalculator.xaoservice.PortfolioDataAccessService;
import com.morningstar.portfoliocalculator.xaoservice.SecurityDataAccessService;
import com.morningstar.portfoliocalculator.xaoservice.exception.DataAccessException;

import java.math.BigDecimal;
import java.util.*;

/**
 * Portfolio Calculator Service
 * Provides services for:
 * - Computing a portfolio value for a portfolio id and a input date
 * - Creating the best portfolio for a date range
 *
 * Created by ebrigand on 02/02/14.
 */
public class PortfolioCalculatorServiceImpl implements PortfolioCalculatorService {

    private PortfolioDataAccessService portfolioService;

    private SecurityDataAccessService securityService;

    public PortfolioCalculatorServiceImpl(PortfolioDataAccessService portfolioService, SecurityDataAccessService securityService){
        this.portfolioService = portfolioService;
        this.securityService = securityService;
    }

    @Override
    public BigDecimal computePortfolioValue(int portfolioId, Date date) throws PortfolioCalculatorServiceException {
        BigDecimal zeroBigDecimal = new BigDecimal(0);
        BigDecimal portfolioValue = zeroBigDecimal;
        try {
            //Create the map of shares by security
            Map<String, BigDecimal> map = new HashMap<>();
            TransactionsType transactionsType = portfolioService.getById(portfolioId).getTransactions();
            //Loop on all the transactions for a portfolio id
            for(TransactionType transactionType : transactionsType.getTransaction()){
                //Process all the transactions for the input date that is equal or after the transaction date
                if(date.compareTo(transactionType.getDate().toGregorianCalendar().getTime()) >= 0){
                    Security security = securityService.getById(transactionType.getSecurityId());
                    //Loop on the history detail related to the security id of the transaction
                    for(HistoryDetailType historyDetailType : security.getHistoryDetail())
                        //Find the most recent price for the transaction date, break the loop when the price is found.
                        if (transactionType.getDate().compare(historyDetailType.getEndDate()) <= 0) {
                            //Compute the shares of the current security id, the amount divide by the share price
                            BigDecimal currentValue = new BigDecimal(transactionType.getAmount() / historyDetailType.getValue());
                            //Get the current share value for the current security id
                            BigDecimal existingValueFromMap = map.get(transactionType.getSecurityId());
                            if (existingValueFromMap == null) {
                                existingValueFromMap = zeroBigDecimal;
                            }
                            //Add or remove the current computed shares by security id to the related total shares following the action type
                            if (transactionType.getType() == ActionType.BUY) {
                                existingValueFromMap = existingValueFromMap.add(currentValue);
                            } else if (transactionType.getType() == ActionType.SELL) {
                                existingValueFromMap = existingValueFromMap.min(currentValue);
                            }
                            //If the new total shares is negative, the data files are wrong, an exception is throw
                            if (existingValueFromMap.compareTo(zeroBigDecimal) < 0) {
                                throw new PortfolioCalculatorServiceException("The portfolio amount is negative");
                            }
                            //Save the new total shares in the map for the current security id
                            map.put(transactionType.getSecurityId(), currentValue);
                            break;
                        }
                }
            }
            //Compute the portfolio value for the input date (for each security id, multiply the sum of share related to the security id with the share price related to the input date)
            for(Map.Entry<String, BigDecimal> entry : map.entrySet()){
                Security security = securityService.getById(entry.getKey());
                for(int i=0; i<security.getHistoryDetail().size(); i++){
                    //Find the matching price for the input date
                    //IMPORTANT If the input date is after the last date for a price of the security, the last date is choose, the rule is not specified for this case
                    //Because the actual rule is when a price is not found for a date, the next price for the NEXT date is chose
                    if(date.compareTo(security.getHistoryDetail().get(i).getEndDate().toGregorianCalendar().getTime()) <= 0 || i == security.getHistoryDetail().size()-1){
                        portfolioValue = portfolioValue.add(entry.getValue().multiply(new BigDecimal(security.getHistoryDetail().get(i).getValue())));
                    }
                }
            }
        } catch (DataAccessException e) {
            throw new PortfolioCalculatorServiceException(e.getMessage());
        }
        //Round up the price with 2 decimals
        return portfolioValue.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public Portfolio createBestPortfolio(int investPrice, Date startDate, Date endDate) throws PortfolioCalculatorServiceException {
        try {
            //Create the Portfolio
            ObjectFactory objectFactory = new ObjectFactory();
            Portfolio portfolio = objectFactory.createPortfolio();
            //Create the TransactionsType
            TransactionsType transactionsType = objectFactory.createTransactionsType();
            //Get all the Security
            List<Security> securities = securityService.getAll();
            //Amount used when buy the transaction
            long investToBuy = investPrice;
            //Amount used when sell the transaction
            long investAfterSell = 0L;
            //Save the history details with the best ratio and close to the startDate
            HistoryDetailType historyDetailTypeForBuy = null;
            HistoryDetailType historyDetailTypeForSell = null;
            //After a buy sell transactions, the startDate become the date of the sell transaction
            while(startDate.before(endDate)){
                //Variable used to find the best ration of the next transaction, a ratio less than 1 mean that the share is loosing value
                float savedDiffValueBetweenClosestDate = 1;
                //Save the best security to fill the created transactions
                Security savedBestSecurity = null;
                for(Security security : securities){
                    for(int i=0; i<security.getHistoryDetail().size()-1; i++){
                        //Find the price related to the startDate
                        if (startDate.compareTo(security.getHistoryDetail().get(i).getEndDate().toGregorianCalendar().getTime()) <= 0) {
                            //If the share is increasing and if the date of the next possible sell transaction is not after the endDate
                            if (security.getHistoryDetail().get(i).getValue() < security.getHistoryDetail().get(i+1).getValue() && endDate.compareTo(security.getHistoryDetail().get(i+1).getEndDate().toGregorianCalendar().getTime()) >= 0) {
                                //Compute the ratio between the next possible sell transaction and the next possible buy transaction
                                float diffValueBetweenClosestDate = security.getHistoryDetail().get(i+1).getValue() / security.getHistoryDetail().get(i).getValue();
                                //If the best ratio is found, the transactions buy and sell are saved and the invest amount after the sell transaction
                                if(diffValueBetweenClosestDate > savedDiffValueBetweenClosestDate){
                                    savedDiffValueBetweenClosestDate = diffValueBetweenClosestDate;
                                    savedBestSecurity = security;
                                    historyDetailTypeForBuy = security.getHistoryDetail().get(i);
                                    historyDetailTypeForSell = security.getHistoryDetail().get(i+1);
                                    BigDecimal currentShareValue = new BigDecimal(investToBuy / historyDetailTypeForBuy.getValue());
                                    investAfterSell = currentShareValue.multiply(new BigDecimal(historyDetailTypeForSell.getValue())).longValue();
                                }
                                //A price has been found, break the loop
                                break;
                            }
                        }
                    }
                }
                //A buy and sell transactions have been found
                if(savedDiffValueBetweenClosestDate > 1){
                    //Save the buy transaction
                    TransactionType transactionTypeBuy = objectFactory.createTransactionType();
                    transactionTypeBuy.setAmount(investToBuy);
                    transactionTypeBuy.setDate(historyDetailTypeForBuy.getEndDate());
                    transactionTypeBuy.setSecurityId(savedBestSecurity.getId());
                    transactionTypeBuy.setType(ActionType.BUY);

                    //Save the sell transaction
                    TransactionType transactionTypeSell = objectFactory.createTransactionType();
                    transactionTypeSell.setAmount(investAfterSell);
                    transactionTypeSell.setDate(historyDetailTypeForSell.getEndDate());
                    transactionTypeSell.setSecurityId(savedBestSecurity.getId());
                    transactionTypeSell.setType(ActionType.SELL);

                    //TRICK part: The problem is when the security of the last sell transaction is the same of the security of the current buy transaction,
                    //it means that the security is the still the best investment, so the last sell and the current buy transaction are removed
                    if(transactionsType.getTransaction().size() >= 2){
                        TransactionType lastRecordedTransactionTypeSell = transactionsType.getTransaction().get(transactionsType.getTransaction().size()-1);
                        //FIX_ME do we need to compare the amount
                        if(lastRecordedTransactionTypeSell.getSecurityId().equals(transactionTypeBuy.getSecurityId()) && lastRecordedTransactionTypeSell.getAmount() == transactionTypeBuy.getAmount()){
                            transactionsType.getTransaction().remove(transactionsType.getTransaction().size()-1);
                        } else {
                            //Add the buy transaction to transactionsType
                            transactionsType.getTransaction().add(transactionTypeBuy);
                        }
                    } else {
                        //Add the buy transaction to transactionsType
                        transactionsType.getTransaction().add(transactionTypeBuy);
                    }

                    //Add the sell transaction to transactionsType
                    transactionsType.getTransaction().add(transactionTypeSell);

                    //The start date is moved to the date of the sell transaction
                    startDate = historyDetailTypeForSell.getEndDate().toGregorianCalendar().getTime();
                    //The invest to buy become the invest to sell
                    investToBuy = investAfterSell;
                } else {
                    //No increasing securities found for the start date
                    //The start date is moved to the tomorrow
                    GregorianCalendar calendar = new GregorianCalendar();
                    calendar.setTime(startDate);
                    calendar.add(Calendar.DATE, 1);
                    startDate = calendar.getTime();
                }
            }
            portfolio.setTransactions(transactionsType);
            return portfolio;
        } catch (DataAccessException e) {
            throw new PortfolioCalculatorServiceException(e.getMessage());
        }
    }
}

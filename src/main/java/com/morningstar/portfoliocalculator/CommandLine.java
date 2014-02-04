package com.morningstar.portfoliocalculator;

import com.morningstar.portfoliocalculator.service.PortfolioCalculatorService;
import com.morningstar.portfoliocalculator.service.exception.PortfolioCalculatorServiceException;
import com.morningstar.portfoliocalculator.service.injector.impl.XAOServiceInjector;

import java.math.BigDecimal;
import java.util.GregorianCalendar;

/**
 * Created by ebrigand on 02/02/14.
 */
public class CommandLine {

    public static void main(String... args){
        if(args.length != 2){
            throw new RuntimeException("Args length need to be 2");
        }
        try {
            PortfolioCalculatorService portfolioCalculatorService = (new XAOServiceInjector()).getPortfolioCalculatorService();

            String[] dateSplit = args[1].split("-");
            if(dateSplit.length != 3){
                throw new RuntimeException("Date malformated, format expected: YYYY-MM-DD");
            }
            GregorianCalendar calendar;
            try{
                calendar = new GregorianCalendar(Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1])-1, Integer.parseInt(dateSplit[2]));
            }catch (NumberFormatException e) {
                e.printStackTrace();
                throw new RuntimeException("Date malformated, format expected: YYYY-MM-DD");
            }
            BigDecimal portfolioValue = portfolioCalculatorService.computePortfolioValue(Integer.parseInt(args[0]), calendar.getTime());
            System.out.println(portfolioValue.toPlainString());

        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new RuntimeException("Portfolio id malformated, format expected: int");
        } catch (PortfolioCalculatorServiceException e) {
            e.printStackTrace();
            throw new RuntimeException("Exception during the computation; " + e.getMessage());
        }
    }

}

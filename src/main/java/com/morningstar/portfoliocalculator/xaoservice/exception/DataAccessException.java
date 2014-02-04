package com.morningstar.portfoliocalculator.xaoservice.exception;

/**
 * Handle exception for {@link com.morningstar.portfoliocalculator.xaoservice.PortfolioDataAccessService} and {@link com.morningstar.portfoliocalculator.xaoservice.SecurityDataAccessService}
 *
 * Created by ebrigand on 03/02/14.
 */
public class DataAccessException extends Exception {

    public DataAccessException(String message, Exception e){
        super(message, e);
    }
}

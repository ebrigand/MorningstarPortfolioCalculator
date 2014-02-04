package com.morningstar.portfoliocalculator.xaoservice;

import com.morningstar.portfoliocalculator.model.portfolio.Portfolio;
import com.morningstar.portfoliocalculator.xaoservice.exception.DataAccessException;

/**
 * Interface PortfolioDataAccessService to define the method access to entities
 *
 * Created by ebrigand on 02/02/14.
 */
public interface PortfolioDataAccessService {

    /**
     * Find a Portfolio by portfolio id
     *
     * @param id
     * @return
     * @throws DataAccessException
     */
    Portfolio getById(int id) throws DataAccessException;
}

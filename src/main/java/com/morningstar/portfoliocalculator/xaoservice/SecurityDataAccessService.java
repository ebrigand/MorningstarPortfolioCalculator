package com.morningstar.portfoliocalculator.xaoservice;

import com.morningstar.portfoliocalculator.model.portfolio.Portfolio;
import com.morningstar.portfoliocalculator.model.security.Security;
import com.morningstar.portfoliocalculator.xaoservice.exception.DataAccessException;

import java.util.Date;
import java.util.List;

/**
 * Interface SecurityDataAccessService to define the method access to entities
 *
 * Created by ebrigand on 02/02/14.
 */
public interface SecurityDataAccessService {

    /**
     * Find a Portfolio by portfolio id
     *
     * @param id
     * @return
     * @throws DataAccessException
     */
    Security getById(String id) throws DataAccessException;

    /**
     * Find all the security
     *
     * @return
     * @throws DataAccessException
     */
    List<Security> getAll() throws DataAccessException;
}

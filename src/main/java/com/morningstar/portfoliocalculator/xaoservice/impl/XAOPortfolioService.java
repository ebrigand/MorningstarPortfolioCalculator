package com.morningstar.portfoliocalculator.xaoservice.impl;

import com.morningstar.portfoliocalculator.model.portfolio.Portfolio;
import com.morningstar.portfoliocalculator.xaoservice.PortfolioDataAccessService;
import com.morningstar.portfoliocalculator.xaoservice.exception.DataAccessException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.nio.file.Path;

/**
 * XML Access Object Service for the Portfolio entity
 *
 * Created by ebrigand on 02/02/14.
 */
public class XAOPortfolioService implements PortfolioDataAccessService {

    private Path portfolioDirPath = XAOLocationEnum.PORTFOLIOS_PATH.getPath();

    private JAXBContext context;

    public XAOPortfolioService() throws DataAccessException {
        try{
            context = JAXBContext.newInstance(Portfolio.class);
        }catch (JAXBException e){
            throw new DataAccessException(e.getMessage(), e);
        }
    }
    @Override
    public Portfolio getById(int id) throws DataAccessException {
        try{
            return (Portfolio) context.createUnmarshaller().unmarshal(portfolioDirPath.resolve(id + ".xml").toFile());
        }catch (JAXBException e){
            throw new DataAccessException(e.getMessage(), e);
        }
    }
}

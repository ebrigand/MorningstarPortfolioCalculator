package com.morningstar.portfoliocalculator.xaoservice.impl;

import com.morningstar.portfoliocalculator.model.security.Security;
import com.morningstar.portfoliocalculator.xaoservice.SecurityDataAccessService;
import com.morningstar.portfoliocalculator.xaoservice.exception.DataAccessException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * XML Access Object Service for the Security entity
 *
 * Created by ebrigand on 02/02/14.
 */
public class XAOSecurityService implements SecurityDataAccessService {

    private Path securityDirPath = XAOLocationEnum.SECURITIES_PATH.getPath();

    private JAXBContext context;

    private static XAOSecurityService xaoSecurityService = null;

    public static synchronized XAOSecurityService getInstance() throws DataAccessException{
        if(xaoSecurityService != null){
            xaoSecurityService = new XAOSecurityService();
        }
        return xaoSecurityService;
    }

    public XAOSecurityService() throws DataAccessException {
        try{
            context = JAXBContext.newInstance(Security.class);
        }catch (JAXBException e){
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    public Security getById(String id) throws DataAccessException {
        try{
            return (Security) context.createUnmarshaller().unmarshal(securityDirPath.resolve(id + ".xml").toFile());
        }catch (JAXBException e){
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    public List<Security> getAll() throws DataAccessException{
        try{
            List<Security> securities = new ArrayList<>();
            JAXBContext context = JAXBContext.newInstance(Security.class);
            //Add a filter ".xml"
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(securityDirPath, "*.xml")) {
                for (Path entry : stream) {
                    securities.add((Security) context.createUnmarshaller().unmarshal(entry.toFile()));
                }
            } catch (DirectoryIteratorException ex) {
                // I/O error encounted during the iteration, the cause is an IOException
                throw ex.getCause();
            }
            return securities;
        }catch (JAXBException e){
            throw new DataAccessException(e.getMessage(), e);
        }
        catch (IOException e){
            throw new DataAccessException(e.getMessage(), e);
        }
    }
}

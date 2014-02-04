import com.morningstar.portfoliocalculator.model.portfolio.ActionType;
import com.morningstar.portfoliocalculator.model.portfolio.Portfolio;
import com.morningstar.portfoliocalculator.model.portfolio.TransactionType;
import com.morningstar.portfoliocalculator.service.PortfolioCalculatorService;
import com.morningstar.portfoliocalculator.service.exception.PortfolioCalculatorServiceException;
import com.morningstar.portfoliocalculator.service.injector.impl.XAOServiceInjector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by ebrigand on 02/02/14.
 */
public class PortfolioCalculatorServiceTest {

    PortfolioCalculatorService portfolioCalculatorService;

    static final int DATE_YEAR_OF_30_11_2011_FOR_COMPUTE_PORTFOLIO_VALUE = 2011;
    static final int DATE_MONTH_OF_30_11_2011_FOR_COMPUTE_PORTFOLIO_VALUE = 10;
    static final int DATE_DAY_OF_30_11_2011_FOR_COMPUTE_PORTFOLIO_VALUE = 30;

    static final int DATE_YEAR_OF_01_11_2011_FOR_BEST_PORTFOLIO = 2011;
    static final int DATE_MONTH_OF_01_11_2011_FOR_BEST_PORTFOLIO = 10;
    static final int DATE_DAY_OF_01_11_2011_FOR_BEST_PORTFOLIO = 01;

    static final int DATE_YEAR_OF_15_11_2011_FOR_BEST_PORTFOLIO = 2011;
    static final int DATE_MONTH_OF_15_11_2011_FOR_BEST_PORTFOLIO = 10;
    static final int DATE_DAY_OF_15_11_2011_FOR_BEST_PORTFOLIO = 15;

    static final int DATE_YEAR_OF_15_11_2020_FOR_BEST_PORTFOLIO = 2020;
    static final int DATE_MONTH_OF_15_11_2020_FOR_BEST_PORTFOLIO = 10;
    static final int DATE_DAY_OF_15_11_2020_FOR_BEST_PORTFOLIO = 15;

    static final int DATE_YEAR_OF_8_11_2011_FOR_BEST_PORTFOLIO_CHECK_MERGE = 2011;
    static final int DATE_MONTH_OF_8_11_2011_FOR_BEST_PORTFOLIO_CHECK_MERGE = 10;
    static final int DATE_DAY_OF_8_11_2011_FOR_BEST_PORTFOLIO_CHECK_MERGE = 8;

    static final int DATE_YEAR_OF_15_11_2011_FOR_BEST_PORTFOLIO_CHECK_FINAL_AMOUNT = 2011;
    static final int DATE_MONTH_OF_15_11_2011_FOR_BEST_PORTFOLIO_CHECK_FINAL_AMOUNT = 10;
    static final int DATE_DAY_OF_15_11_2011_FOR_BEST_PORTFOLIO_CHECK_FINAL_AMOUNT = 15;

    static final int PORT_ID_3 = 3;
    static final int PORT_ID_4 = 4;

    static final int INVEST_AMOUNT_1000 = 1000;

    static final double EXPECTED_RESULT_FOR_PORT_ID_3_AND_YEAR_30_11_2011 = 14765.28;
    static final double EXPECTED_RESULT_FOR_PORT_ID_4_AND_YEAR_30_11_2011 = 15863.54;

    static  final int TRANSACTION_LIST_SIZE_FOR_BEST_PORTFOLIO_WITH_END_DATE_BETWEEN_01_11_2011_AND_01_11_2020 = 16;

    static final TransactionType transactionTypeCheckMerge;

    static final TransactionType transactionTypeCheckFinalAmount;

    static final Calendar calendar30_11_2011;

    static final Calendar startCalendar01_11_2011;

    static final Calendar endCalendar15_11_2011;

    static final Calendar endCalendar15_11_2020;

    static {
        calendar30_11_2011 = new GregorianCalendar(DATE_YEAR_OF_30_11_2011_FOR_COMPUTE_PORTFOLIO_VALUE, DATE_MONTH_OF_30_11_2011_FOR_COMPUTE_PORTFOLIO_VALUE, DATE_DAY_OF_30_11_2011_FOR_COMPUTE_PORTFOLIO_VALUE);
        startCalendar01_11_2011 = new GregorianCalendar(DATE_YEAR_OF_01_11_2011_FOR_BEST_PORTFOLIO, DATE_MONTH_OF_01_11_2011_FOR_BEST_PORTFOLIO, DATE_DAY_OF_01_11_2011_FOR_BEST_PORTFOLIO);
        endCalendar15_11_2011 = new GregorianCalendar(DATE_YEAR_OF_15_11_2011_FOR_BEST_PORTFOLIO, DATE_MONTH_OF_15_11_2011_FOR_BEST_PORTFOLIO, DATE_DAY_OF_15_11_2011_FOR_BEST_PORTFOLIO);
        endCalendar15_11_2020 = new GregorianCalendar(DATE_YEAR_OF_15_11_2020_FOR_BEST_PORTFOLIO, DATE_MONTH_OF_15_11_2020_FOR_BEST_PORTFOLIO, DATE_DAY_OF_15_11_2020_FOR_BEST_PORTFOLIO);
        XMLGregorianCalendar xmlCalendarForTransactionTypeCheckMerge = null;
        XMLGregorianCalendar xmlCalendarForTransactionTypeCheckFinalAmount = null;
        try{
            GregorianCalendar calendarForTransactionTypeCheckMerge = new GregorianCalendar(DATE_YEAR_OF_8_11_2011_FOR_BEST_PORTFOLIO_CHECK_MERGE, DATE_MONTH_OF_8_11_2011_FOR_BEST_PORTFOLIO_CHECK_MERGE, DATE_DAY_OF_8_11_2011_FOR_BEST_PORTFOLIO_CHECK_MERGE);
            DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
            xmlCalendarForTransactionTypeCheckMerge =
                    datatypeFactory.newXMLGregorianCalendar(calendarForTransactionTypeCheckMerge.get(Calendar.YEAR), calendarForTransactionTypeCheckMerge.get(Calendar.MONTH)+1, calendarForTransactionTypeCheckMerge.get(Calendar.DAY_OF_MONTH),DatatypeConstants.FIELD_UNDEFINED,
                            DatatypeConstants.FIELD_UNDEFINED,
                            DatatypeConstants.FIELD_UNDEFINED,
                            DatatypeConstants.FIELD_UNDEFINED,
                            DatatypeConstants.FIELD_UNDEFINED);
            GregorianCalendar calendarForTransactionTypeCheckFinalAmount = new GregorianCalendar(DATE_YEAR_OF_15_11_2011_FOR_BEST_PORTFOLIO_CHECK_FINAL_AMOUNT, DATE_MONTH_OF_15_11_2011_FOR_BEST_PORTFOLIO_CHECK_FINAL_AMOUNT, DATE_DAY_OF_15_11_2011_FOR_BEST_PORTFOLIO_CHECK_FINAL_AMOUNT);
            xmlCalendarForTransactionTypeCheckFinalAmount =
                    datatypeFactory.newXMLGregorianCalendar(calendarForTransactionTypeCheckFinalAmount.get(Calendar.YEAR), calendarForTransactionTypeCheckFinalAmount.get(Calendar.MONTH)+1, calendarForTransactionTypeCheckFinalAmount.get(Calendar.DAY_OF_MONTH),DatatypeConstants.FIELD_UNDEFINED,
                            DatatypeConstants.FIELD_UNDEFINED,
                            DatatypeConstants.FIELD_UNDEFINED,
                            DatatypeConstants.FIELD_UNDEFINED,
                            DatatypeConstants.FIELD_UNDEFINED);
        } catch (DatatypeConfigurationException e){

        }
        transactionTypeCheckMerge = new TransactionType();
        transactionTypeCheckMerge.setType(ActionType.SELL);
        transactionTypeCheckMerge.setSecurityId("A");
        transactionTypeCheckMerge.setAmount(1045);
        transactionTypeCheckMerge.setDate(xmlCalendarForTransactionTypeCheckMerge);

        transactionTypeCheckFinalAmount = new TransactionType();
        transactionTypeCheckFinalAmount.setType(ActionType.SELL);
        transactionTypeCheckFinalAmount.setSecurityId("D");
        transactionTypeCheckFinalAmount.setAmount(1079);
        transactionTypeCheckFinalAmount.setDate(xmlCalendarForTransactionTypeCheckFinalAmount);
    }

    @Before
    public void init() throws PortfolioCalculatorServiceException {
        portfolioCalculatorService = (new XAOServiceInjector()).getPortfolioCalculatorService();
    }

    @Test
    public void computePortfolioValueForPortId3AndDate30_11_2011() throws PortfolioCalculatorServiceException {
        BigDecimal portfolioValue = portfolioCalculatorService.computePortfolioValue(PORT_ID_3, calendar30_11_2011.getTime());
        Assert.assertTrue(portfolioValue.doubleValue() == EXPECTED_RESULT_FOR_PORT_ID_3_AND_YEAR_30_11_2011);
    }

    @Test
    public void computePortfolioValueForPortId4AndDate30_11_2011() throws PortfolioCalculatorServiceException {
        BigDecimal portfolioValue = portfolioCalculatorService.computePortfolioValue(PORT_ID_4, calendar30_11_2011.getTime());
        Assert.assertTrue(portfolioValue.doubleValue() == EXPECTED_RESULT_FOR_PORT_ID_4_AND_YEAR_30_11_2011);
    }

    @Test
    public void createBestPortfolioForDateBetween30_10_2011And30_11_2011() throws PortfolioCalculatorServiceException {
        Portfolio portfolio = portfolioCalculatorService.createBestPortfolio(INVEST_AMOUNT_1000, startCalendar01_11_2011.getTime(), endCalendar15_11_2011.getTime());
        Assert.assertTrue(transactionTypeCheckMerge.equals(portfolio.getTransactions().getTransaction().get(1)));
        Assert.assertTrue(transactionTypeCheckFinalAmount.equals(portfolio.getTransactions().getTransaction().get(7)));
    }

    @Test
    public void createBestPortfolioForDateBetween30_10_2011And30_11_2020() throws PortfolioCalculatorServiceException {
        Portfolio portfolio = portfolioCalculatorService.createBestPortfolio(INVEST_AMOUNT_1000, startCalendar01_11_2011.getTime(), endCalendar15_11_2020.getTime());
        Assert.assertTrue(portfolio.getTransactions().getTransaction().size() == TRANSACTION_LIST_SIZE_FOR_BEST_PORTFOLIO_WITH_END_DATE_BETWEEN_01_11_2011_AND_01_11_2020);
    }
}

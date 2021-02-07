import com.calculator.Main;
import com.calculator.SimpleCalculator;
import com.calculator.consts.SimpleCalculatorConstants;
import com.calculator.utils.SimpleCalculatorUtils;
import com.calculator.impl.SimpleCalculatorImpl;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SimpleCalculatorImplTest {

    SimpleCalculator simpleCalculator;
    Validator validator;
    Path expressionsFile;
    Path incorrectExpressionsFile;
    Path resultFile;

    @Mock
    Appender appender;

    @Before
    public void setUp() throws Exception {
        simpleCalculator = new SimpleCalculatorImpl();
        validator = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                .newSchema(SimpleCalculatorImpl.class.getResource(SimpleCalculatorConstants.SCHEMA_PATH))
                .newValidator();
        expressionsFile = Paths.get(Main .class.getResource("/sample/SampleTest.xml").toURI());
        incorrectExpressionsFile = Paths.get(Main .class.getResource("/sample/SampleTestWithIncorrectData.xml").toURI());
        resultFile = Paths.get(Main.class.getResource("/sample/SampleTestResult.xml").toURI());
    }

    @Test
    public void testCalculate() throws Exception {
        simpleCalculator.calculate(expressionsFile, resultFile);

        List<Double> results = SimpleCalculatorUtils.parseResults(resultFile);
        Assert.assertEquals(results.get(0), Double.valueOf(-2443.75));
        Assert.assertEquals(results.get(1), Double.valueOf(59747.58686350021));
    }

    @Test
    public void testNullExpressionsFile() {
        Logger logger = Logger.getLogger(SimpleCalculatorImpl.class);
        logger.addAppender(appender);

        simpleCalculator.calculate(null, resultFile);

        ArgumentCaptor<LoggingEvent> captor = ArgumentCaptor.forClass(LoggingEvent.class);
        verify(appender, times(1)).doAppend(captor.capture());
    }

    @Test
    public void testNullResultFile() {
        Logger logger = Logger.getLogger(SimpleCalculatorImpl.class);
        logger.addAppender(appender);

        simpleCalculator.calculate(expressionsFile, null);

        ArgumentCaptor<LoggingEvent> captor = ArgumentCaptor.forClass(LoggingEvent.class);
        verify(appender, times(1)).doAppend(captor.capture());
    }

    @Test
    public void testIncorrectExpressionFile() {
        Logger logger = Logger.getLogger(SimpleCalculatorImpl.class);
        logger.addAppender(appender);

        simpleCalculator.calculate(incorrectExpressionsFile, resultFile);

        ArgumentCaptor<LoggingEvent> captor = ArgumentCaptor.forClass(LoggingEvent.class);
        verify(appender, times(1)).doAppend(captor.capture());
    }

    @Test
    public void testOutputFileFormat() throws Exception {
        simpleCalculator.calculate(expressionsFile, resultFile);

        SimpleCalculatorUtils.validateSchema(resultFile, validator);
    }

}

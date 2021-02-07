import com.calculator.Main;
import com.calculator.consts.SimpleCalculatorConstants;
import com.calculator.utils.SimpleCalculatorUtils;
import com.calculator.impl.SimpleCalculatorImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SimpleCalculatorUtilsTest {

    Validator validator;
    Path expressionsFile;
    Path incorrectExpressionsFile;
    Path resultFileExample;

    @Before
    public void setUp() throws Exception {
        validator = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                .newSchema(SimpleCalculatorImpl.class.getResource(SimpleCalculatorConstants.SCHEMA_PATH))
                .newValidator();
        expressionsFile = Paths.get(Main .class.getResource("/sample/SampleTest.xml").toURI());
        incorrectExpressionsFile = Paths.get(Main .class.getResource("/sample/SampleTestWithIncorrectData.xml").toURI());
        resultFileExample = Paths.get(Main.class.getResource("/sample/SampleTestResultExample.xml").toURI());
    }

    @Test
    public void testValidateSchema() throws IOException, SAXException {
        SimpleCalculatorUtils.validateSchema(expressionsFile, validator);
        SimpleCalculatorUtils.validateSchema(resultFileExample, validator);
    }

    @Test(expected = SAXException.class)
    public void testValidateSchemaNegative() throws IOException, SAXException {
        SimpleCalculatorUtils.validateSchema(incorrectExpressionsFile, validator);
    }

    @Test
    public void testParseResult() throws Exception {
        SimpleCalculatorUtils.validateSchema(resultFileExample, validator);
        List<Double> results = SimpleCalculatorUtils.parseResults(resultFileExample);
        Assert.assertEquals(results.get(0), Double.valueOf(-2443.75));
        Assert.assertEquals(results.get(1), Double.valueOf(59747.58686350021));
    }
}

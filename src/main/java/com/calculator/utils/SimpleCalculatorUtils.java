package com.calculator.utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.calculator.consts.SimpleCalculatorConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class SimpleCalculatorUtils {

    public static void validateSchema(Path path, Validator validator) throws IOException, SAXException {
        Source source = new StreamSource(Files.newInputStream(path));
        validator.validate(source);
    }

    public static Element appendChildElement(Node parent, String tagName, String textValue) {
        Element result;
        if(parent instanceof Document) {
            result = ((Document) parent).createElement(tagName);
        } else {
            result = parent.getOwnerDocument().createElement(tagName);
        }
        if(textValue != null && !"".equals(textValue)) {
            result.setTextContent(textValue);
        }
        parent.appendChild(result);
        return result;
    }

    public static List<Double> parseResults(Path path) throws Exception {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream inputStream = Files.newInputStream(path);
        Document parse = documentBuilder.parse(inputStream);

        List<Double> result = new ArrayList<>();
        NodeList expressions = parse.getElementsByTagName(SimpleCalculatorConstants.RESULT);
        for(int i = 0; i < expressions.getLength(); i++) {
            result.add(Double.valueOf(expressions.item(i).getTextContent()));
        }
        return result;
    }

}

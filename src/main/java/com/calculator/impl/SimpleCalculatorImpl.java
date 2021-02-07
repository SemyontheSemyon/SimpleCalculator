package com.calculator.impl;

import com.calculator.consts.Operations;
import com.calculator.SimpleCalculator;
import com.calculator.consts.SimpleCalculatorConstants;
import com.calculator.utils.SimpleCalculatorUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SimpleCalculatorImpl implements SimpleCalculator {

    Validator validator;
    DocumentBuilder documentBuilder;
    private static final Logger LOG = Logger.getLogger(SimpleCalculatorImpl.class);

    @Override
    public void calculate(Path file, Path resultFile) {
        try {
            if(file == null) {
                throw new IllegalArgumentException("Input file cannot be null");
            }
            if(resultFile == null) {
                throw new IllegalArgumentException("Output file cannot be null");
            }
            if(validator == null) {
                validator = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                        .newSchema(SimpleCalculatorImpl.class.getResource(SimpleCalculatorConstants.SCHEMA_PATH))
                        .newValidator();
            }
            if(documentBuilder == null) {
                documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            }
            SimpleCalculatorUtils.validateSchema(file, validator);
            if(!Files.exists(resultFile)) {
                Files.createFile(resultFile);
            }
            InputStream inputStream = Files.newInputStream(file);
            Document parse = documentBuilder.parse(inputStream);

            Document resultDoc = documentBuilder.newDocument();
            Element scElement = SimpleCalculatorUtils.appendChildElement(resultDoc, SimpleCalculatorConstants.SIMPLE_CALCULATOR, null);
            Element expResultsElement = SimpleCalculatorUtils.appendChildElement(scElement, SimpleCalculatorConstants.EXPRESSION_RESULTS, null);

            NodeList expressions = parse.getElementsByTagName(SimpleCalculatorConstants.EXPRESSION);
            for(int i = 0; i < expressions.getLength(); i++) {
                NodeList childNodes = expressions.item(i).getChildNodes();
                for(int j = 0; j < childNodes.getLength(); j++) {
                    if(SimpleCalculatorConstants.OPERATION.equals(childNodes.item(j).getNodeName())) {
                        double result = calculateOperation((Element) childNodes.item(j));
                        Element expResultElement = SimpleCalculatorUtils.appendChildElement(expResultsElement, SimpleCalculatorConstants.EXPRESSION_RESULT, null);
                        SimpleCalculatorUtils.appendChildElement(expResultElement, SimpleCalculatorConstants.RESULT, String.valueOf(result));
                        break;
                    }
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource domSource = new DOMSource(resultDoc);
            StreamResult streamResult = new StreamResult(Files.newOutputStream(resultFile));
            transformer.transform(domSource, streamResult);

            SimpleCalculatorUtils.validateSchema(resultFile, validator);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    private double calculateOperation(Element operation) {
        double result = 0;
        NodeList childNodes = operation.getChildNodes();
        List<Double> operands = new ArrayList<>();
        for(int i = 0; i < childNodes.getLength(); i++) {
            String nodeName = childNodes.item(i).getNodeName();
            if (SimpleCalculatorConstants.OPERATION.equals(nodeName)) {
                operands.add(calculateOperation((Element)childNodes.item(i)));
            } else if (SimpleCalculatorConstants.ARG.equals(nodeName)) {
                Double operand = Double.valueOf(childNodes.item(i).getTextContent());
                operands.add(operand);
            }
        }
        String operationType = operation.getAttribute(SimpleCalculatorConstants.OPERATION_TYPE);
        result = Operations.valueOf(operationType).calculate(operands);
        return result;
    }
}

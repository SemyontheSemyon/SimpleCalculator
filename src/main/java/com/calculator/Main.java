package com.calculator;

import com.calculator.impl.SimpleCalculatorImpl;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        Path path = null;
        Path resultFile = null;
        try {
            path = Paths.get(Main.class.getResource("/sample/SampleTest.xml").toURI());
            resultFile = Paths.get(Main.class.getResource("/sample/SampleTestResult.xml").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        SimpleCalculator sc = new SimpleCalculatorImpl();
        sc.calculate(path, resultFile);

    }

}

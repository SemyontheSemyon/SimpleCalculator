package com.calculator.consts;

import java.util.List;

public enum Operations {

    SUM{public double calculate(List<Double> operands) {return operands.stream().reduce((a,b) -> a+b).get();}},
    SUB{public double calculate(List<Double> operands) {return operands.stream().reduce((a,b) -> a-b).get();}},
    MUL{public double calculate(List<Double> operands) {return operands.stream().reduce((a,b) -> a*b).get();}},
    DIV{public double calculate(List<Double> operands) {return operands.stream().reduce((a,b) -> a/b).get();}};

    public abstract double calculate(List<Double> operands);

}

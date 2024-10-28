package com.aplavina;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class QuadraticApproximation {
    private static final double PRECISION = 0.000001;

    private final UnaryOperator<Double> function;
    private final double epsilon1;
    private final double epsilon2;
    private double x1;
    private final double deltaX;
    private double x2;
    private double x3;
    private double f1;
    private double f2;
    private double f3;
    private double fMin;
    private double xMin;
    private double xPolynomialMin;
    private double fPolynomialMin;

    public QuadraticApproximation(UnaryOperator<Double> function, double epsilon1, double epsilon2, double x1, double deltaX) {
        this.function = function;
        this.epsilon1 = epsilon1;
        this.epsilon2 = epsilon2;
        this.x1 = x1;
        this.deltaX = deltaX;
    }

    public double approximate() {
        calculatePoints();
        while (true) {
            calculateMin();
            try {
                calculatePolynomialMin();
            } catch (ZeroDivisorException e) {
                x1 = xMin;
                continue;
            }
            if (checkPrecision()) {
                return xPolynomialMin;
            } else {
                List<Double> sortedPoints = Stream.of(x1, x2, x3).sorted().toList();
                if (xPolynomialMin >= sortedPoints.get(0) && xPolynomialMin <= sortedPoints.get(2)) {
                    x2 = Math.min(xPolynomialMin, xMin);
                    x1 = sortedPoints.get(1) < x2 ? sortedPoints.get(1) : sortedPoints.get(0);
                    x3 = sortedPoints.get(2) > x2 ? sortedPoints.get(2) : sortedPoints.get(3);
                } else {
                    x1 = xPolynomialMin;
                    calculatePoints();
                }
            }
        }
    }

    private boolean checkPrecision() {
        boolean functionValuesPrecise = Math.abs((fMin - fPolynomialMin) / (fPolynomialMin)) < epsilon1;
        boolean coordinatesValuesPrecise = Math.abs((xMin - xPolynomialMin) / (xPolynomialMin)) < epsilon2;
        return functionValuesPrecise && coordinatesValuesPrecise;
    }

    private void calculatePoints() {
        x2 = x1 + deltaX;
        f1 = function.apply(x1);
        f2 = function.apply(x2);
        x3 = f1 > f2 ? x1 + 2 * deltaX : x1 - deltaX;
        f3 = function.apply(x3);
    }

    private void calculateMin() {
        if (f1 < f2 && f1 < f3) {
            fMin = f1;
            xMin = x1;
        } else if (f2 < f1 && f2 < f3) {
            fMin = f2;
            xMin = x2;
        } else {
            fMin = f3;
            xMin = x3;
        }
    }

    private void calculatePolynomialMin() throws ZeroDivisorException {
        double dividend = (x2 * x2 - x3 * x3) * f1 + (x3 * x3 - x1 * x1) * f2 + (x1 * x1 - x2 * x2) * f3;
        double divisor = (x2 - x3) * f1 + (x3 - x1) * f2 + (x1 - x2) * f3;
        if (Math.abs(divisor) < PRECISION) {
            throw new ZeroDivisorException("Dividing by zero");
        }
        xPolynomialMin = dividend / (2 * divisor);
        fPolynomialMin = function.apply(xPolynomialMin);
    }

    private static class ZeroDivisorException extends Exception {
        public ZeroDivisorException(String message) {
            super(message);
        }
    }
}

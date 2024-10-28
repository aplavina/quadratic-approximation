package com.aplavina;

import java.util.function.UnaryOperator;

public class Main {
    public static void main(String[] args) {
        UnaryOperator<Double> function = x -> {
            if (x <= 0) {
                return 0.0;
            }
            return x * x - 3 * x + x * Math.log(x);
        };
        double epsilon1 = 0.05;
        double epsilon2 = 0.5;
        double x1 = 1;
        double deltaX = 1;
        QuadraticApproximation approximation = new QuadraticApproximation(function, epsilon1, epsilon2, x1, deltaX);
        double res = approximation.approximate();
        System.out.println("x = " + res + " f(x) = " + function.apply(res));
    }
}
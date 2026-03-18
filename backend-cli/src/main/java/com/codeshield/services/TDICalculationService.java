package com.codeshield.services;

public class TDICalculationService {
    public double calculateTDI(int totalComplexity, double vulnerabilityDensity) {
        double complexityScore = totalComplexity * 0.5;
        double vulnerabilityScore = vulnerabilityDensity * 0.5;

        return complexityScore + vulnerabilityScore;
    }

    public String classifyRisk(double tdi) {
        if (tdi > 50) {
            return "High Risk ==> Extremely Complex / High Risk - Immediate Refactoring Recommended";
        } else if (tdi > 20) {
            return "Medium Risk ==> Moderate Complexity - Consider Review";
        } else {
            return "Low Risk ==> Code is Healthy";
        }
    }
}

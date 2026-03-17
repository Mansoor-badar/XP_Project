package com.codeshield.services;

import com.codeshield.security.VulnerabilityDetector;

public class SecurityAnalysisService {

    public double analyseVulnerabilityDensity(String code) {
        return VulnerabilityDetector.calculateVulnerabilityScore(code);
    }

    public int countRedFlags(String code) {
        return VulnerabilityDetector.detectedRedFlags(code);
    }

    public int countLOC(String code) {
        return VulnerabilityDetector.countLOC(code);
    }
}
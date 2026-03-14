package com.codeshield.services;
import com.codeshield.services.JavaComplexityAnalyser;
import com.codeshield.models.ScanResult;

public class ScanService {
    private final JavaComplexityAnalyser complexityAnalyser;
    private final SecurityAnalysisService securityService;
    private final TDICalculationService tdiService;

    public ScanService(){
        this.complexityAnalyser = new JavaComplexityAnalyser();
        this.securityService = new SecurityAnalysisService();
        this.tdiService = new TDICalculationService();
    }

    public ScanResult scan(String code){
        // complexity analysis
        int complexity = complexityAnalyser.analyseFile(code).totalComplexity();

        // security analysis
        double vulnerabilityDensity = securityService.analyseVulnerabilityDensity(code);
        int redFlags = securityService.countRedFlags(code);
        int loc = securityService.countLOC(code);

        double tdi = tdiService.calculateTDI(complexity, vulnerabilityDensity);
        String riskClassification = tdiService.classifyRisk(tdi);

        // compile results
        ScanResult result = new ScanResult();
        result.setTotalComplexity(complexity);
        result.setMethods(complexityAnalyser.analyseFile(code).methods());
        result.setVulnerabilityDensity(vulnerabilityDensity);
        result.setRedFlags(redFlags);
        result.setLoc(loc);
        result.setTdi(tdi);
        result.setRiskClassification(riskClassification);
        return result;
    }
}

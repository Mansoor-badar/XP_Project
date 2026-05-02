package com.codeshield.services;

import com.codeshield.TestResultWatcher;
import com.codeshield.models.ScanResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TestResultWatcher.class)
@DisplayName("FR4: TDI Calculation and Risk Classification")
class TDICalculationServiceTest {

    private TDICalculationService service;

    @BeforeEach
    void setUp() {
        service = new TDICalculationService();
    }

    // --- TDI Calculation ---

    @Test
    @DisplayName("FR4.1 - Appendix A worked example: M=4, Density=15.0")
    void test_4_1_appendixA_tdiIs9point5() {
        assertEquals(9.5, service.calculateTDI(4, 15.0), 0.01);
    }

    @Test
    @DisplayName("FR4.2 - Appendix B worked example: M=7, Density=150")
    void test_4_2_appendixB_tdiIs78point5() {
        assertEquals(78.5, service.calculateTDI(7, 150.0), 0.01);
    }

    @Test
    @DisplayName("FR4.3 - Minimum TDI: M=1, no red flags (calculation)")
    void test_4_3_minimumTdi_is0point5() {
        assertEquals(0.5, service.calculateTDI(1, 0.0), 0.01);
    }

    @Test
    @DisplayName("FR4.4 - BVA: TDI just below threshold (49.5) - calculation")
    void test_4_4_tdi49point5_calculatedCorrectly() {
        assertEquals(49.5, service.calculateTDI(9, 90.0), 0.01);
    }

    @Test
    @DisplayName("FR4.5 - BVA: TDI exactly at threshold (50.0) - calculation")
    void test_4_5_tdi50_calculatedCorrectly() {
        assertEquals(50.0, service.calculateTDI(10, 90.0), 0.01);
    }

    @Test
    @DisplayName("FR4.6 - BVA: TDI just above threshold (50.5) - calculation")
    void test_4_6_tdi50point5_calculatedCorrectly() {
        assertEquals(50.5, service.calculateTDI(11, 90.0), 0.01);
    }

    @Test
    @DisplayName("FR4.7 - High complexity, no vulnerabilities - calculation")
    void test_4_7_highComplexityNoVulns_tdiIs10() {
        assertEquals(10.0, service.calculateTDI(20, 0.0), 0.01);
    }

    @Test
    @DisplayName("FR4.8 - Very low complexity, extreme density - calculation")
    void test_4_8_extremeDensity_tdiIs1000point5() {
        assertEquals(1000.5, service.calculateTDI(1, 2000.0), 0.01);
    }

    @Test
    @DisplayName("FR4.13 - Floating point precision near threshold - calculation")
    void test_4_13_floatingPointPrecision_tdiIs50() {
        assertEquals(50.0, service.calculateTDI(7, 93.0), 0.001);
    }

    @Test
    @DisplayName("FR4.11 - TDI shown to correct decimal places")
    void test_4_11_tdiDecimalPrecision_notRounded() {
        double tdi = service.calculateTDI(4, 15.0);
        String formatted = String.format("%.1f", tdi);
        assertEquals("9.5", formatted);
    }

    // --- Risk Classification ---

    @Test
    @DisplayName("FR4.3 - Minimum TDI: Low risk label for TDI=0.5")
    void test_4_3_tdi0point5_isLowRisk() {
        String label = service.classifyRisk(0.5);
        assertTrue(label.toLowerCase().contains("low"),
            "Expected low risk label for TDI=0.5, got: " + label);
    }

    @Test
    @DisplayName("FR4.4 - BVA: TDI=49.5 not high risk")
    void test_4_4_tdi49point5_notHighRisk() {
        String label = service.classifyRisk(49.5);
        assertFalse(label.toLowerCase().contains("high"),
            "TDI=49.5 should NOT trigger high risk, got: " + label);
    }

    @Test
    @DisplayName("FR4.5 - BVA: TDI=50.0 triggers high risk alert")
    // EXPECTED TO FAIL: classifyRisk uses > 50 instead of >= 50
    void test_4_5_tdi50_triggersHighRisk() {
        String label = service.classifyRisk(50.0);
        assertTrue(label.toLowerCase().contains("high"),
            "TDI=50.0 MUST trigger high risk (threshold is >=50), got: " + label);
    }

    @Test
    @DisplayName("FR4.6 - BVA: TDI=50.5 is high risk")
    void test_4_6_tdi50point5_isHighRisk() {
        String label = service.classifyRisk(50.5);
        assertTrue(label.toLowerCase().contains("high"),
            "TDI=50.5 should be high risk, got: " + label);
    }

    @Test
    @DisplayName("FR4.7 - TDI=10.0 not high risk")
    void test_4_7_tdi10_notHighRisk() {
        String label = service.classifyRisk(10.0);
        assertFalse(label.toLowerCase().contains("high"),
            "TDI=10.0 should NOT be high risk, got: " + label);
    }

    @Test
    @DisplayName("FR4.8 - TDI=1000.5 is high risk, no crash")
    void test_4_8_tdi1000point5_isHighRisk() {
        String label = service.classifyRisk(1000.5);
        assertTrue(label.toLowerCase().contains("high"),
            "TDI=1000.5 should be high risk, got: " + label);
    }

    @Test
    @DisplayName("FR4.2 - TDI=78.5 is high risk")
    void test_4_2_tdi78point5_isHighRisk() {
        String label = service.classifyRisk(78.5);
        assertTrue(label.toLowerCase().contains("high"),
            "TDI=78.5 should be high risk, got: " + label);
    }

    @Test
    @DisplayName("FR4.13 - Floating point: TDI=50.0 triggers high risk")
    // EXPECTED TO FAIL: same threshold bug as FR4.5
    void test_4_13_tdi50_fromFloatInputs_triggersHighRisk() {
        double tdi = service.calculateTDI(7, 93.0);
        String label = service.classifyRisk(tdi);
        assertTrue(label.toLowerCase().contains("high"),
            "TDI=50.0 MUST trigger high risk even from float inputs, got: " + label);
    }

    @Test
    @DisplayName("FR4.14 - TDI consistent with complexity output")
    void test_4_14_tdiConsistentWithComplexityOutput() {
        String code =
            "public class Multi {\n" +
            "    public void linear() {}\n" +
            "    public void twoIfs(int a, int b) {\n" +
            "        if (a > 0) { run(); }\n" +
            "        if (b > 0) { run(); }\n" +
            "    }\n" +
            "    public void fourIfs(int a, int b, int c, int d) {\n" +
            "        if (a > 0) { run(); }\n" +
            "        if (b > 0) { run(); }\n" +
            "        if (c > 0) { run(); }\n" +
            "        if (d > 0) { run(); }\n" +
            "    }\n" +
            "}";
        ScanResult result = new ScanService().scan(code);
        double expectedTdi = service.calculateTDI(result.getTotalComplexity(), result.getVulnerabilityDensity());
        assertEquals(expectedTdi, result.getTdi(), 0.001,
            "TDI in result must match calculateTDI(M=" + result.getTotalComplexity() +
            ", density=" + result.getVulnerabilityDensity() + ")");
    }
}

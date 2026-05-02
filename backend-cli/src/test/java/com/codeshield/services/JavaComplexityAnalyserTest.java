package com.codeshield.services;

import com.codeshield.TestResultWatcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TestResultWatcher.class)
@DisplayName("FR2: Cyclomatic Complexity Calculation")
class JavaComplexityAnalyserTest {

    private final JavaComplexityAnalyser analyser = new JavaComplexityAnalyser();

    private int complexity(String javaClass) {
        return analyser.analyseFile(javaClass).totalComplexity();
    }

    private int methodComplexity(String methodCode) {
        return complexity("public class T {\n" + methodCode + "\n}");
    }

    @Test
    @DisplayName("FR2.1 - Linear method, no decisions")
    void test_2_1_linearMethod_M1() {
        assertEquals(1, methodComplexity(
            "public int greet() { return 1; }"));
    }

    @Test
    @DisplayName("FR2.2 - Single if statement")
    void test_2_2_singleIf_M2() {
        assertEquals(2, methodComplexity(
            "public int check(int x) { if (x > 0) { return x; } return 0; }"));
    }

    @Test
    @DisplayName("FR2.3 - if-else statement")
    void test_2_3_ifElse_M2() {
        assertEquals(2, methodComplexity(
            "public String label(int x) { if (x > 0) { return \"pos\"; } else { return \"neg\"; } }"));
    }

    @Test
    @DisplayName("FR2.4 - Two independent if statements")
    void test_2_4_twoIndependentIfs_M3() {
        assertEquals(3, methodComplexity(
            "public void check(int a, int b) { if (a > 0) { log(a); } if (b > 0) { log(b); } }"));
    }

    @Test
    @DisplayName("FR2.5 - for loop")
    void test_2_5_forLoop_M2() {
        assertEquals(2, methodComplexity(
            "public int sum(int n) { int s = 0; for (int i = 0; i < n; i++) { s += i; } return s; }"));
    }

    @Test
    @DisplayName("FR2.6 - while loop")
    void test_2_6_whileLoop_M2() {
        assertEquals(2, methodComplexity(
            "public void process() { while (!done) { step(); } }"));
    }

    @Test
    @DisplayName("FR2.7 - Nested if inside a for loop")
    void test_2_7_nestedIfInForLoop_M3() {
        assertEquals(3, methodComplexity(
            "public void run(int[] arr) { for (int x : arr) { if (x > 0) { log(x); } } }"));
    }

    @Test
    @DisplayName("FR2.8 - switch with 5 cases")
    // EXPECTED TO FAIL: switch statements fall through to a plain node in buildStmt
    void test_2_8_switchFiveCases_M6() {
        assertEquals(6, methodComplexity(
            "public String day(int d) { switch(d) { " +
            "case 1: return \"Mon\"; case 2: return \"Tue\"; " +
            "case 3: return \"Wed\"; case 4: return \"Thu\"; " +
            "default: return \"Other\"; } }"));
    }

    @Test
    @DisplayName("FR2.9 - Appendix B worked example: 6 decision points")
    void test_2_9_appendixBPayroll_M7() {
        String code =
            "public class PayrollBonus {\n" +
            "    public double calculateBonus(boolean isActive, double balance, int years, String role) {\n" +
            "        if (!isActive) { return 0; }\n" +
            "        if (balance > 5000) { return balance * 0.10; }\n" +
            "        if (balance > 2000) { return balance * 0.05; }\n" +
            "        if (years > 5) { return balance * 0.03; }\n" +
            "        if (years > 2) { return balance * 0.02; }\n" +
            "        if (role.equals(\"admin\")) { return balance * 0.01; }\n" +
            "        return 0;\n" +
            "    }\n" +
            "}";
        assertEquals(7, complexity(code));
    }

    @Test
    @DisplayName("FR2.10 - Compound && condition")
    // EXPECTED TO FAIL: compound conditions are not split in the CFG builder
    void test_2_10_compoundAnd_M3() {
        assertEquals(3, methodComplexity(
            "public void check(int x, int y) { if (x > 0 && y > 0) { run(); } }"));
    }

    @Test
    @DisplayName("FR2.11 - try-catch block")
    // EXPECTED TO FAIL: try-catch is not handled in buildStmt
    void test_2_11_tryCatch_M2() {
        assertEquals(2, methodComplexity(
            "public void load() { try { read(); } catch (Exception e) { handle(e); } }"));
    }

    @Test
    @DisplayName("FR2.12 - High complexity: 9 decision points")
    void test_2_12_nineDecisionPoints_M10() {
        String ifs =
            "if (a > 1) { run(); }\n" +
            "if (a > 2) { run(); }\n" +
            "if (a > 3) { run(); }\n" +
            "if (a > 4) { run(); }\n" +
            "if (a > 5) { run(); }\n" +
            "if (a > 6) { run(); }\n" +
            "if (a > 7) { run(); }\n" +
            "if (a > 8) { run(); }\n" +
            "if (a > 9) { run(); }";
        assertEquals(10, methodComplexity("public void method(int a) {\n" + ifs + "\n}"));
    }

    @Test
    @DisplayName("FR2.13 - Ternary operator")
    // EXPECTED TO FAIL: ternary expressions are not handled in buildStmt
    void test_2_13_ternaryOperator_M2() {
        assertEquals(2, methodComplexity(
            "public int max(int a, int b) { return a > b ? a : b; }"));
    }

    @Test
    @DisplayName("FR2.14 - Compound || condition")
    // EXPECTED TO FAIL: compound conditions are not split in the CFG builder
    void test_2_14_compoundOr_M3() {
        assertEquals(3, methodComplexity(
            "public void check(int a, int b) { if (a > 0 || b > 0) { run(); } }"));
    }

    @Test
    @DisplayName("FR2.15 - do-while loop")
    // EXPECTED TO FAIL: do-while is not handled in buildStmt
    void test_2_15_doWhileLoop_M2() {
        assertEquals(2, methodComplexity(
            "public void process() { do { step(); } while (!done); }"));
    }

    @Test
    @DisplayName("FR2.16 - Multi-method class: three methods with different complexity")
    void test_2_16_multiMethodClass_recordsActualBehaviour_currentlySum() {
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
        assertEquals(9, complexity(code));
        assertEquals(3, analyser.analyseFile(code).methods().size());
    }
}

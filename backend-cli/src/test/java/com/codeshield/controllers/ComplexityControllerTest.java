package com.codeshield.controllers;

import com.codeshield.TestResultWatcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ComplexityController.class)
@ExtendWith(TestResultWatcher.class)
@DisplayName("FR1 and FR5: Code Input, Parsing and Power-Calculation Rejection")
class ComplexityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String URL = "/api/analysis/analyze";

    private String body(String fileName, String code) throws Exception {
        return objectMapper.writeValueAsString(Map.of("fileName", fileName, "sourceCode", code));
    }

    // --- FR1: Code Input and Parsing ---

    @Test
    @DisplayName("FR1.1 - Valid Java class submitted")
    void test_1_1_validJavaClass_returns200() throws Exception {
        String code = "public class Calc { public int add(int a, int b) { return a + b; } }";
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body("Calc.java", code)))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("FR1.2 - Completely empty input")
    void test_1_2_emptyInput_returns400() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body("Test.java", "")))
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("FR1.3 - Whitespace and blank lines only")
    void test_1_3_whitespaceOnly_returns400() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body("Test.java", "   \n\n\n  ")))
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("FR1.4 - Java comments only, no executable code")
    void test_1_4_commentsOnly_doesNotCrash() throws Exception {
        String code = "// This is a comment\n// Another comment";
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body("Test.java", code)))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("FR1.5 - Valid Java method but no class declaration")
    void test_1_5_methodWithoutClass_returnsError() throws Exception {
        String code = "public int square(int x) { return x * x; }";
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body("Test.java", code)))
               .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("FR1.6 - Java syntax error: missing closing brace")
    void test_1_6_syntaxError_returnsErrorNoCrash() throws Exception {
        String code = "public class Broken { public void run() { if (x > 0) { doSomething(); }";
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body("Broken.java", code)))
               .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("FR1.7 - Python code submitted")
    void test_1_7_pythonCode_returns501() throws Exception {
        String code = "def foo(x):\n    if x > 0:\n        return x\n    return 0";
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body("foo.py", code)))
               .andExpect(status().isNotImplemented());
    }

    @Test
    @DisplayName("FR1.8 - Large Java class: 500+ lines, 10 methods")
    void test_1_8_largeClass500Lines_completesNoCrash() throws Exception {
        StringBuilder sb = new StringBuilder("public class Large {\n");
        for (int m = 1; m <= 10; m++) {
            sb.append("    public void method").append(m).append("(int x) {\n");
            for (int l = 0; l < 49; l++) {
                sb.append("        int v").append(l).append(" = x + ").append(l).append(";\n");
            }
            sb.append("    }\n");
        }
        sb.append("}");
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body("Large.java", sb.toString())))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("FR1.10 - Valid class with empty method bodies")
    void test_1_10_emptyMethodBodies_returnsComplexity() throws Exception {
        String code = "public class Empty { public void run() {} public void stop() {} }";
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body("Empty.java", code)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.totalComplexity").value(2));
    }

    @Test
    @DisplayName("FR1.12 - Minified one-line code with no whitespace")
    void test_1_12_minifiedCode_parsedCorrectly() throws Exception {
        String code = "public class A{public void doIt(){int x=1;if(x>0){x++;}}}";
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body("A.java", code)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.totalComplexity").value(2));
    }

    @Test
    @DisplayName("FR1.13 - Code referencing a missing external library")
    void test_1_13_missingExternalLibrary_parsedSuccessfully() throws Exception {
        String code = "import org.apache.commons.lang3.StringUtils;\n" +
                      "public class A { public void run() { StringUtils.isEmpty(\"x\"); } }";
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body("A.java", code)))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("FR1.14 - Extremely large file: 50,000 lines")
    void test_1_14_extremelyLargeFile_doesNotCrash() throws Exception {
        StringBuilder sb = new StringBuilder("public class Huge {\n    public void method(int x) {\n");
        for (int i = 0; i < 49995; i++) {
            sb.append("        int v").append(i % 1000).append(" = ").append(i).append(";\n");
        }
        sb.append("    }\n}");
        int status = mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                .content(body("Huge.java", sb.toString())))
                .andReturn().getResponse().getStatus();
        assertTrue(status < 500, "Expected no server crash (5xx) for large file, got HTTP " + status);
    }

    // --- FR5: Power-Calculation Rejection ---

    @Test
    @DisplayName("FR5.1 - Math.pow() in a return statement")
    // EXPECTED TO FAIL: Math.pow rejection is not yet implemented
    void test_5_1_mathPowInReturn_rejected() throws Exception {
        String code = "public class A { public double square(double x) { return Math.pow(x, 2); } }";
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body("A.java", code)))
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("FR5.2 - Math.pow() assigned to a variable")
    // EXPECTED TO FAIL: Math.pow rejection is not yet implemented
    void test_5_2_mathPowAssigned_rejected() throws Exception {
        String code = "public class A { public void calc() { double result = Math.pow(base, exponent); } }";
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body("A.java", code)))
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("FR5.3 - Math.pow() inside a larger expression")
    // EXPECTED TO FAIL: Math.pow rejection is not yet implemented
    void test_5_3_mathPowInLargerExpression_rejected() throws Exception {
        String code = "public class A { public double calc(int e, int n, double density) { " +
                      "double tdi = (Math.pow(e - n, 2) * 0.5) + density; return tdi; } }";
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body("A.java", code)))
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("FR5.4 - BigInteger.pow()")
    // EXPECTED TO FAIL: BigInteger.pow rejection is not yet implemented
    void test_5_4_bigIntegerPow_rejected() throws Exception {
        String code = "public class A { public void calc() { BigInteger result = base.pow(exponent); } }";
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body("A.java", code)))
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("FR5.5 - Math.pow in a comment only")
    // VACUOUS PASS: rejection not yet implemented — becomes a regression guard once FR5 is built
    void test_5_5_mathPowInComment_accepted() throws Exception {
        String code = "// Use Math.pow(x, 2) for squaring\n" +
                      "public class A { public double square(double x) { return x * x; } }";
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body("A.java", code)))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("FR5.6 - Math.pow inside a string literal")
    // VACUOUS PASS: rejection not yet implemented — becomes a regression guard once FR5 is built
    void test_5_6_mathPowInStringLiteral_accepted() throws Exception {
        String code = "public class A { String hint = \"Use Math.pow(x, 2) to compute squares.\"; }";
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body("A.java", code)))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("FR5.7 - Custom method named pow (not Math.pow)")
    // VACUOUS PASS: rejection not yet implemented — becomes a regression guard once FR5 is built
    void test_5_7_customMethodNamedPow_accepted() throws Exception {
        String code = "public class A { public int pow(int a, int b) { return a * b; } }";
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body("A.java", code)))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("FR5.8 - Custom method named calculatePower")
    // VACUOUS PASS: rejection not yet implemented — becomes a regression guard once FR5 is built
    void test_5_8_calculatePowerMethod_accepted() throws Exception {
        String code = "public class A { public double calculatePower(double base, double exp) { return base * exp; } }";
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body("A.java", code)))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("FR5.9 - Valid code with no power-calculation")
    // VACUOUS PASS: rejection not yet implemented — becomes a regression guard once FR5 is built
    void test_5_9_noPowCalculation_accepted() throws Exception {
        String code = "public class Calc { public int add(int a, int b) { return a + b; } }";
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body("Calc.java", code)))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("FR5.10 - Multiple Math.pow() calls in one submission")
    // EXPECTED TO FAIL: Math.pow rejection is not yet implemented
    void test_5_10_multipleMathPowCalls_rejected() throws Exception {
        String code = "public class A { public void calc(double x, double y) { " +
                      "double a = Math.pow(x, 2); double b = Math.pow(y, 3); } }";
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body("A.java", code)))
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("FR5.11 - Math.pow used via static import")
    // EXPECTED TO FAIL: static import detection is not yet implemented
    void test_5_11_staticImportPow_rejected() throws Exception {
        String code = "import static java.lang.Math.pow;\n" +
                      "public class A { public double calc(double x) { return pow(x, 2); } }";
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body("A.java", code)))
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("FR5.12 - Math.pow called with full package path")
    // EXPECTED TO FAIL: fully qualified path detection is not yet implemented
    void test_5_12_fullyQualifiedMathPow_rejected() throws Exception {
        String code = "public class A { public double calc(double x) { return java.lang.Math.pow(x, 2); } }";
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body("A.java", code)))
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("FR5.13 - StrictMath.pow() used instead of Math.pow()")
    // EXPECTED TO FAIL: StrictMath.pow rejection is not yet implemented
    void test_5_13_strictMathPow_rejected() throws Exception {
        String code = "public class A { public double calc(double x) { return StrictMath.pow(x, 2); } }";
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body("A.java", code)))
               .andExpect(status().isBadRequest());
    }
}

package com.codeshield.cli;
import com.codeshield.core.Language;
import com.codeshield.core.LanguageDetector;
import com.codeshield.java.JavaComplexityAnalyser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class CodeShieldCli {
    public static void main(String[] args) throws Exception {
        String filePath = (args.length > 0) ? args[0] : promptPath();

        String code = Files.readString(Path.of(filePath));
        Language lang = LanguageDetector.detectLanguage(filePath, code);

        System.out.println("=== CodeShield Iteration 1 (Java CLI) ===");
        System.out.println("Analyzing file: " + filePath);
        System.out.println("Detected language: " + lang);

        if (lang != Language.JAVA) {
            System.out.println("Unsupported language. Only Java is supported in this iteration.");
            return;
        }

        var analyser = new JavaComplexityAnalyser();
        var result = analyser.analyseFile(code);

        System.out.println("\n--- Per-method CFG results (M = E - N + 2P) ---");
        for (var m : result.methods()) {
            System.out.printf("Method: %s, N: %d, E: %d, P: %d, M: %d%n",
                    m.name(), m.N(), m.E(), m.P(), m.M());
        }

        System.out.println("\nTotal complexity (sum of M): " + result.totalComplexity());
    }

    private static String promptPath() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the path to the source code file: ");
        return scanner.nextLine().trim();
    }
}

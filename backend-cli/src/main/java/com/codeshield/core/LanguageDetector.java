package com.codeshield.core;
import java.util.regex.Pattern;

public class LanguageDetector {
    private LanguageDetector() {
        // Private constructor to prevent instantiation
    }

    public static Language detectLanguage(String filePath, String code) {
        String p = (filePath == null ? "" : filePath.toLowerCase());
        if (p.endsWith(".java")) {
            return Language.JAVA;
        } else if (p.endsWith(".py")) {
            return Language.PYTHON;
        } 
        
        int javaScore = 0;
        int pythonScore = 0;

        javaScore += countWord(code, "class");
        javaScore += countWord(code, "public");
        javaScore += countWord(code, "static");
        javaScore += countWord(code, "void");
        javaScore += countWord(code, "import");
        javaScore += code.contains("System.out") ? 2 : 0;
        javaScore += code.contains(";") ? 1 : 0;

        pythonScore += countWord(code, "def");
        pythonScore += countWord(code, "elif");
        pythonScore += countWord(code, "self");
        pythonScore += code.contains("print") ? 1 : 0;

        if (javaScore == 0 && pythonScore == 0) {
            return Language.UNKNOWN;
        }
        return javaScore >= pythonScore ? Language.JAVA : Language.PYTHON;
    }

    private static int countWord(String code, String word) {
        return (int) Pattern.compile("\\b" + Pattern.quote(word) + "\\b").matcher(code).results().count();
    }
}

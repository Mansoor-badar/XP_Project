package com.codeshield.models;

public record AnalyseRequest(
    String fileName,
    String sourceCode
) {
}
package com.codeshield.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AnalyseRequest(
    @JsonProperty("fileName") String fileName,
    @JsonProperty("sourceCode") String sourceCode
) {
}
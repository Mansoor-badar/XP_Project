package com.codeshield.models;

import java.util.List;

public record FileResult(int totalComplexity, List<MethodResult> methods) {
}
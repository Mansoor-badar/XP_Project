package com.codeshield.models;

public class MethodResult {
    private final String name;
    private final int N;
    private final int E;
    private final int P;
    private final int M;

    public MethodResult(String name, int N, int E, int P, int M) {
        this.name = name;
        this.N = N;
        this.E = E;
        this.P = P;
        this.M = M;
    }

    public String getName() {
        return name;
    }

    public int getN() {
        return N;
    }

    public int getE() {
        return E;
    }

    public int getP() {
        return P;
    }

    public int getM() {
        return M;
    }
}
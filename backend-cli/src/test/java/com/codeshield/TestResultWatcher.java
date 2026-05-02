package com.codeshield;

import org.junit.jupiter.api.extension.*;
import java.util.*;

public class TestResultWatcher implements TestWatcher, AfterAllCallback {

    private final List<String[]> results = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void testSuccessful(ExtensionContext context) {
        add(context, "PASS", "");
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        String msg = cause.getMessage() != null ? cause.getMessage().split("\n")[0].trim() : "";
        if (msg.contains(" ==> expected:")) msg = msg.substring(0, msg.indexOf(" ==> expected:"));
        if (msg.length() > 65) msg = msg.substring(0, 62) + "...";
        add(context, "FAIL", msg);
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        add(context, "ABORT", "");
    }

    private void add(ExtensionContext context, String result, String detail) {
        String display = context.getDisplayName();
        int dash = display.indexOf(" - ");
        String id   = dash >= 0 ? display.substring(0, dash).trim() : display;
        String desc = dash >= 0 ? display.substring(dash + 3).trim() : display;
        if (desc.length() > 50) desc = desc.substring(0, 47) + "...";
        results.add(new String[]{id, desc, result, detail});
    }

    @Override
    public void afterAll(ExtensionContext context) {
        int W = 130;
        System.out.println();
        System.out.println("=".repeat(W));
        System.out.printf("  RESULTS: %s%n", context.getDisplayName());
        System.out.println("=".repeat(W));
        System.out.printf("  %-8s  %-52s  %-6s  %s%n", "Test ID", "Description", "Result", "Actual Outcome");
        System.out.println("  " + "-".repeat(W - 2));
        for (String[] r : results) {
            System.out.printf("  %-8s  %-52s  %-6s  %s%n", r[0], r[1], r[2], r[3]);
        }
        long passed = results.stream().filter(r -> "PASS".equals(r[2])).count();
        long failed = results.stream().filter(r -> "FAIL".equals(r[2])).count();
        System.out.println("  " + "-".repeat(W - 2));
        System.out.printf("  Total: %d  |  Passed: %d  |  Failed: %d%n",
                results.size(), passed, failed);
        System.out.println("=".repeat(W));
        System.out.println();
    }
}

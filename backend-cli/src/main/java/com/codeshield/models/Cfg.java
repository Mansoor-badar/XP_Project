package com.codeshield.models;

import java.util.*;

public class Cfg {

    private final Node entry;
    private final Node exit;
    private final List<Node> nodes = new ArrayList<>();
    private final Set<Edge> edges = new HashSet<>();

    public Cfg(Node entry, Node exit) {
        this.entry = entry;
        this.exit = exit;
        nodes.add(entry);
        nodes.add(exit);
    }

    public Node entry() {
        return entry;
    }

    public Node exit() {
        return exit;
    }

    public Node newNode(String label) {
        Node n = new Node(nodes.size() + 1, label);
        nodes.add(n);
        return n;
    }

    public void addEdge(Node from, Node to) {
        if (from == null || to == null) {
            return;
        }
        edges.add(new Edge(from.id(), to.id()));
    }

    public int N() {
        return nodes.size();
    }

    public int E() {
        return edges.size();
    }

    public int P() {
        Map<Integer, Set<Integer>> adj = new HashMap<>();
        for (Node n : nodes) {
            adj.put(n.id(), new HashSet<>());
        }
        for (Edge e : edges) {
            adj.get(e.from()).add(e.to());
            adj.get(e.to()).add(e.from());
        }

        Set<Integer> seen = new HashSet<>();
        int components = 0;

        for (Node n : nodes) {
            if (seen.contains(n.id())) {
                continue;
            }
            components++;
            Deque<Integer> dq = new ArrayDeque<>();
            dq.add(n.id());
            seen.add(n.id());
            while (!dq.isEmpty()) {
                int cur = dq.removeFirst();
                for (int nb : adj.getOrDefault(cur, Set.of())) {
                    if (seen.add(nb)) {
                        dq.addLast(nb);
                    }
                }
            }
        }
        return components;
    }

    public int cyclomaticComplexity() {
        return E() - N() + 2 * P(); // M = E - N + 2P
    }
}

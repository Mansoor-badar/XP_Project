package com.codeshield.java;

import com.codeshield.cfg.Cfg;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;

import java.util.*;

public final class JavaComplexityAnalyser {
    public record MethodResult(String name, int N, int E, int P, int M) {}
    public record FileResult(int totalComplexity, List<MethodResult> methods) {}

    public FileResult analyseFile(String javaSource) {
        var cu = StaticJavaParser.parse(javaSource);
        List<MethodResult> methods = new ArrayList<>();
        int total = 0;

        for (MethodDeclaration m : cu.findAll(MethodDeclaration.class)) {
            if (m.getBody().isEmpty()) continue;
            MethodResult r = analyseCallable(m.getNameAsString(), m.getBody().get());
            methods.add(r);
            total += r.M();
        }

        for (ConstructorDeclaration c : cu.findAll(ConstructorDeclaration.class)) {
            MethodResult r = analyseCallable(c.getNameAsString(), c.getBody());
            methods.add(r);
            total += r.M();
        }

        return new FileResult(total, methods);
    }

    private MethodResult analyseCallable(String name, BlockStmt body) {
        Cfg.Node entry = new Cfg.Node(1, "ENTRY");
        Cfg.Node exit  = new Cfg.Node(2, "EXIT");
        Cfg cfg = new Cfg(entry, exit);

        BuildResult built = buildStmtList(cfg, body.getStatements());

        //connect entry to the first statement
        cfg.addEdge(cfg.entry(), built.entry);

        //connect final tails to exit
        for (Cfg.Node tail : built.tails) cfg.addEdge(tail, cfg.exit());

        int N = cfg.N();
        int E = cfg.E();
        int P = cfg.P();
        int M = cfg.cyclomaticComplexity();

        return new MethodResult(name, N, E, P, M);
    }

    private record BuildResult(Cfg.Node entry, Set<Cfg.Node> tails) {
    }

    private BuildResult buildStmtList(Cfg cfg, List<Statement> stmts) {
        Cfg.Node entry = null;
        Set<Cfg.Node> tails = new HashSet<>();

        for (Statement st : stmts) {
            BuildResult br = buildStmt(cfg, st);

            if (entry == null){
                entry = br.entry;
                tails = new HashSet<>(br.tails);
            } else {
                for (Cfg.Node t : tails) cfg.addEdge(t, br.entry);
                tails = new HashSet<>(br.tails);
            }

            if (tails.isEmpty()){
                break;
            }
        }

        if (entry == null) {
            Cfg.Node empty = cfg.newNode("EMPTY");
            return new BuildResult(empty, Set.of(empty));
        }

        return new BuildResult(entry, tails);
    }

    private BuildResult buildStmt(Cfg cfg, Statement st) {
        if (st.isBlockStmt()) return buildStmtList(cfg, st.asBlockStmt().getStatements());

        if (st.isReturnStmt() || st.isThrowStmt()) {
            Cfg.Node n = cfg.newNode("RETURN");
            cfg.addEdge(n, cfg.exit());
            return new BuildResult(n, Set.of()); //ensure that no tails are fall through after return/throw
        }

        if (st.isIfStmt()) {
            IfStmt ifs = st.asIfStmt();
            Cfg.Node cond = cfg.newNode("IF");

            BuildResult thenBr = buildStmt(cfg, ifs.getThenStmt());
            BuildResult elseBr = ifs.getElseStmt().isPresent()
                    ? buildStmt(cfg, ifs.getElseStmt().get())
                    : null;

            cfg.addEdge(cond, thenBr.entry);

            if (elseBr == null) {
                Cfg.Node join = cfg.newNode("JOIN");
                cfg.addEdge(cond, join); // false path
                for (Cfg.Node t : thenBr.tails) cfg.addEdge(t, join);
                return new BuildResult(cond, Set.of(join));
            }

            cfg.addEdge(cond, elseBr.entry);

            boolean thenFalls = !thenBr.tails.isEmpty();
            boolean elseFalls = !elseBr.tails.isEmpty();

            if (!thenFalls && !elseFalls) return new BuildResult(cond, Set.of());

            Cfg.Node join = cfg.newNode("JOIN");
            for (Cfg.Node t : thenBr.tails) cfg.addEdge(t, join);
            for (Cfg.Node t : elseBr.tails) cfg.addEdge(t, join);
            return new BuildResult(cond, Set.of(join));
        }

        if (st.isWhileStmt()) {
            Cfg.Node cond = cfg.newNode("WHILE");
            Cfg.Node after = cfg.newNode("AFTER_WHILE");
            BuildResult body = buildStmt(cfg, st.asWhileStmt().getBody());

            cfg.addEdge(cond, body.entry);
            cfg.addEdge(cond, after);
            for (Cfg.Node t : body.tails) cfg.addEdge(t, cond);

            return new BuildResult(cond, Set.of(after));
        }

        if (st.isForStmt() || st.isForEachStmt()) {
            Cfg.Node cond = cfg.newNode("FOR");
            Cfg.Node after = cfg.newNode("AFTER_FOR");
            Statement bodyStmt = st.isForStmt() ? st.asForStmt().getBody() : st.asForEachStmt().getBody();
            BuildResult body = buildStmt(cfg, bodyStmt);

            cfg.addEdge(cond, body.entry);
            cfg.addEdge(cond, after);
            for (Cfg.Node t : body.tails) cfg.addEdge(t, cond);

            return new BuildResult(cond, Set.of(after));
        }

        Cfg.Node n = cfg.newNode(st.getClass().getSimpleName());
        return new BuildResult(n, Set.of(n));
    }
}
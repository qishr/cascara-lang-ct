package io.github.qishr.cascara.lang.ct.ast;

import java.util.ArrayList;
import java.util.List;

public class CtDocument extends CtNode {
    private List<CtImport> imports = new ArrayList<>();
    private List<CtStatement> statements = new ArrayList<>();

    public CtDocument() {
        super(null);
    }

    public List<CtImport> getImports() {
        return imports;
    }

    public void addImport(CtImport i) {
        imports.add(i);
    }

    public List<CtStatement> getStatements() {
        return statements;
    }

    public void addStatement(CtStatement d) {
        statements.add(d);
    }
}

package io.github.qishr.cascara.lang.ct.ast;

import java.util.ArrayList;
import java.util.List;

import io.github.qishr.cascara.lang.ct.token.CtToken;

public class CtPaletteAssignmentValue extends CtAssignmentValue {
    private List<CtStatement> statements = new ArrayList<>();
    private CtMethodCall method;

    public CtPaletteAssignmentValue(CtToken token) {
        super(token);
    }

    public List<CtStatement> getStatements() {
        return statements;
    }

    public void addStatement(CtStatement d) {
        statements.add(d);
    }

    public CtMethodCall getMethod() {
        return method;
    }

    public void setMethodCall(CtMethodCall method) {
        this.method = method;
    }

}

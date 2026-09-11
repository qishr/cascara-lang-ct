package io.github.qishr.cascara.lang.ct.ast;

import io.github.qishr.cascara.lang.ct.token.CtToken;

public class CtSingleNameStatement extends CtStatement {
    private CtName name;

    public CtSingleNameStatement(CtToken token) {
        super(token);
    }

    public CtName getName() {
        return name;
    }

    public CtSingleNameStatement setName(CtName name) {
        this.name = name;
        return this;
    }
}

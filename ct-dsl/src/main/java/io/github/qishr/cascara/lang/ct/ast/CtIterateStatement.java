package io.github.qishr.cascara.lang.ct.ast;

import io.github.qishr.cascara.lang.ct.token.CtToken;

public class CtIterateStatement extends CtStatement{
    private CtIdentifier identifier;
    private CtSeparator separator;

    public CtIterateStatement(CtToken token) {
        super(token);
    }

    public CtIdentifier getIdentifier() {
        return identifier;
    }

    public CtIterateStatement setIdentifier(CtIdentifier identifier) {
        this.identifier = identifier;
        return this;
    }

    public CtSeparator getSeparator() {
        return separator;
    }

    public CtIterateStatement setSeparator(CtSeparator separator) {
        this.separator = separator;
        return this;
    }
}

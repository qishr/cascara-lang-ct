package io.github.qishr.cascara.lang.ct.ast;

import java.util.ArrayList;
import java.util.List;

import io.github.qishr.cascara.lang.ct.token.CtToken;

public class CtInsertStatement extends CtStatement{
    private List<CtIdentifier> identifiers = new ArrayList<>();
    private CtSeparator separator;

    public CtInsertStatement(CtToken token) {
        super(token);
    }

    public List<CtIdentifier> getIdentifiers() {
        return identifiers;
    }

    public CtInsertStatement addIdentifier(CtIdentifier identifier) {
        identifiers.add(identifier);
        return this;
    }

    public CtSeparator getSeparator() {
        return separator;
    }

    public CtInsertStatement setSeparator(CtSeparator separator) {
        this.separator = separator;
        return this;
    }
}

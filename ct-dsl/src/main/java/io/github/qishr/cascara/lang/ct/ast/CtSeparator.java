package io.github.qishr.cascara.lang.ct.ast;

import io.github.qishr.cascara.lang.ct.token.CtToken;

public class CtSeparator extends CtNode {
    private CtString string;
    private CtIdentifier identifier;

    public CtSeparator(CtToken token) {
        super(token);
    }

    public CtString getString() {
        return string;
    }

    public CtSeparator setString(CtString string) {
        this.string = string;
        return this;
    }

    public CtIdentifier getIdentifier() {
        return identifier;
    }

    public CtSeparator setIdentifier(CtIdentifier identifier) {
        this.identifier = identifier;
        return this;
    }
}

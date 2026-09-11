package io.github.qishr.cascara.lang.ct.ast;

import io.github.qishr.cascara.lang.ct.token.CtToken;

public class CtString extends CtNode {
    private String value;

    public CtString(CtToken token, String value) {
        super(token);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String asString() {
        return value;
    }

    public String toString() {
        return value;
    }
}

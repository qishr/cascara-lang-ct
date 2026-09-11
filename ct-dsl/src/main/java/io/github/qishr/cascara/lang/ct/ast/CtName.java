package io.github.qishr.cascara.lang.ct.ast;

import io.github.qishr.cascara.lang.ct.token.CtToken;

public class CtName extends CtNode {
    private String name;

    public CtName(CtToken token, String name) {
        super(token);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String asString() {
        return name;
    }

    public String toString() {
        return name;
    }
}

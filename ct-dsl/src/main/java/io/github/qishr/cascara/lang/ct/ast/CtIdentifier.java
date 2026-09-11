package io.github.qishr.cascara.lang.ct.ast;

import io.github.qishr.cascara.lang.ct.token.CtToken;

public class CtIdentifier extends CtName {
    // private String name;

    public CtIdentifier(CtToken token) {
        super(token, token.getLexeme());
        // this.name = ;
    }

    // public String getName() {
    //     return name;
    // }

    // public void setName(String name) {
    //     this.name = name;
    // }
}

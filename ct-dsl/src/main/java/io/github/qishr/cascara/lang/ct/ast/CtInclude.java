package io.github.qishr.cascara.lang.ct.ast;

import io.github.qishr.cascara.lang.ct.token.CtToken;

public class CtInclude extends CtStatement {
    private CtString fileName;

    public CtInclude(CtToken token, CtString fileName) { //, CtIdentifier className, CtIdentifier methodName) {
        super(token);
        this.fileName = fileName;
    }

    public CtString getFileName() {
        return fileName;
    }
}

package io.github.qishr.cascara.lang.ct.ast;

import io.github.qishr.cascara.lang.ct.internal.CtType;
import io.github.qishr.cascara.lang.ct.token.CtToken;

public class CtDeclaration extends CtStatement {
    protected CtIdentifier identifier;
    protected CtAssignmentValue assignment;
    private CtType type;

    public CtDeclaration(CtToken token, CtType type, CtIdentifier identifier, CtAssignmentValue assignment) {
        super(token);
        this.type = type;
        this.identifier = identifier;
        this.assignment = assignment;
    }

    public CtType getCtType() {
        return type;
    }

    public CtAssignmentValue getAssignment() {
        return assignment;
    }

    // public CtDeclaration(CtToken token, CtIdentifier identifier) {
    //     super(token);
    //     this.identifier = identifier;
    // }

    public CtIdentifier getIdentifier() {
        return identifier;
    }
}

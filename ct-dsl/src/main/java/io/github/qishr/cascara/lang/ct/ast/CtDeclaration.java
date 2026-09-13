// License & Terms
//
// This file is part of **Cascara CT**.
//
// **Cascara CT** is free software: you can redistribute
// it and/or modify them without restriction under the terms of
// the MIT License.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// MIT License for more details.

package io.github.qishr.cascara.lang.ct.ast;

import io.github.qishr.cascara.lang.ct.token.CtToken;
import io.github.qishr.cascara.lang.ct.util.CtType;

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

    public CtIdentifier getIdentifier() {
        return identifier;
    }
}

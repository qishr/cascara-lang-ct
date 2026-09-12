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

import java.util.ArrayList;
import java.util.List;

import io.github.qishr.cascara.lang.ct.token.CtToken;

public class CtPaletteAssignmentValue extends CtAssignmentValue {
    private List<CtStatement> statements = new ArrayList<>();
    private CtMethodCall method;

    public CtPaletteAssignmentValue(CtToken token) {
        super(token);
    }

    public List<CtStatement> getStatements() {
        return statements;
    }

    public void addStatement(CtStatement d) {
        statements.add(d);
    }

    public CtMethodCall getMethod() {
        return method;
    }

    public void setMethodCall(CtMethodCall method) {
        this.method = method;
    }

}

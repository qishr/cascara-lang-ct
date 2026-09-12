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

public class CtAssignment extends CtStatement {
    private CtName name;
    private CtAssignmentValue assignmentValue;

    public CtAssignment(CtName name, CtAssignmentValue assignmentValue) {
        super(name.getToken());
        this.name = name;
        this.assignmentValue = assignmentValue;
    }

    public CtName getName() {
        return name;
    }

    public CtAssignmentValue getAssignmentValue() {
        return assignmentValue;
    }
}

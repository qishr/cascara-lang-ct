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

public class CtMethodCall extends CtAssignmentValue {
    private CtIdentifier name;
    private List<CtParameter> parameters = new ArrayList<>();

    public CtMethodCall(CtToken token) {
        super(token);
    }

    public CtIdentifier getName() {
        return name;
    }

    public void setName(CtIdentifier name) {
        this.name = name;
    }

    public List<CtParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<CtParameter> parameters) {
        this.parameters = parameters;
    }
}

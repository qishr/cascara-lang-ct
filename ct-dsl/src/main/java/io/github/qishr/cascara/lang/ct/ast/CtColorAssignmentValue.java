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

public class CtColorAssignmentValue extends CtAssignmentValue {
    private CtMethodCall method;
    private String hexValue;
    private CtName name;

    public CtColorAssignmentValue(CtToken token) {
        super(token);
    }

    public CtMethodCall getMethod() {
        return method;
    }

    public void setMethodCall(CtMethodCall method) {
        this.method = method;
    }

    public String getHexValue() {
        return hexValue;
    }

    public void setHexValue(String hex) {
        hexValue = hex;
    }

    public CtName getName() {
        return name;
    }

    public void setName(CtName name) {
        this.name = name;
    }
}

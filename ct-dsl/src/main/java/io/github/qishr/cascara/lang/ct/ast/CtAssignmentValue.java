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

public class CtAssignmentValue extends CtNode {
    private CtMethodCall method;
    private String hexValue;
    private CtName name;
    private CtString string;

    private CtType fromType;
    private CtType toType;

    public CtAssignmentValue(CtToken token) {
        super(token);
    }

    public CtType getToType() {
        return toType;
    }

    public void setToType(CtType type) {
        toType = type;
    }

    public CtType getFromType() {
        return fromType;
    }

    public void setFromType(CtType type) {
        fromType = type;
    }

    public CtMethodCall getMethod() {
        return method;
    }

    public void setMethodCall(CtMethodCall method) {
        this.method = method;
        this.fromType = CtType.METHOD;
    }

    public String getHexValue() {
        return hexValue;
    }

    public void setHexValue(String hex) {
        hexValue = hex;
        this.fromType = CtType.COLOR;
    }

    public CtName getName() {
        return name;
    }

    public void setName(CtName name) {
        this.name = name;
        this.fromType = CtType.COLOR;
    }

    public CtString getString() {
        return string;
    }

    public void setString(CtString string) {
        this.string = string;
        this.fromType = CtType.STRING;
    }
}

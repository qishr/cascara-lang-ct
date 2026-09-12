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

public class CtParameter extends CtNode {
    private boolean isPercentage;
    private boolean isRelative;
    private Number number;
    private String name;
    private boolean isNumber;

    public CtParameter(CtToken token, Number number, boolean isPercentage, boolean isRelative) {
        super(token);
        this.number = number;
        this.isPercentage = isPercentage;
        this.isRelative = isRelative;
        isNumber = true;
    }

    public CtParameter(CtToken token, String name) {
        super(token);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Number getNumber() {
        return number;
    }

    public boolean isPercentage() {
        return isPercentage;
    }

    public boolean isRelative() {
        return isRelative;
    }

    public boolean isNumber() {
        return isNumber;
    }
}

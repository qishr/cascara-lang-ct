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

public class CtString extends CtNode {
    private String value;

    public CtString(CtToken token, String value) {
        super(token);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String asString() {
        return value;
    }

    public String toString() {
        return value;
    }
}

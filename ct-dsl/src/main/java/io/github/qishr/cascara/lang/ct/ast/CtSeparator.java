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

public class CtSeparator extends CtNode {
    private CtString string;
    private CtIdentifier identifier;

    public CtSeparator(CtToken token) {
        super(token);
    }

    public CtString getString() {
        return string;
    }

    public CtSeparator setString(CtString string) {
        this.string = string;
        return this;
    }

    public CtIdentifier getIdentifier() {
        return identifier;
    }

    public CtSeparator setIdentifier(CtIdentifier identifier) {
        this.identifier = identifier;
        return this;
    }
}

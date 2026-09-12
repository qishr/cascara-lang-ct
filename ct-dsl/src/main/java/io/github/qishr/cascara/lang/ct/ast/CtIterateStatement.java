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

public class CtIterateStatement extends CtStatement{
    private CtIdentifier identifier;
    private CtSeparator separator;

    public CtIterateStatement(CtToken token) {
        super(token);
    }

    public CtIdentifier getIdentifier() {
        return identifier;
    }

    public CtIterateStatement setIdentifier(CtIdentifier identifier) {
        this.identifier = identifier;
        return this;
    }

    public CtSeparator getSeparator() {
        return separator;
    }

    public CtIterateStatement setSeparator(CtSeparator separator) {
        this.separator = separator;
        return this;
    }
}

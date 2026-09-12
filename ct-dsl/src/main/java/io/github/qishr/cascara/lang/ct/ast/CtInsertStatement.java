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

public class CtInsertStatement extends CtStatement{
    private List<CtIdentifier> identifiers = new ArrayList<>();
    private CtSeparator separator;

    public CtInsertStatement(CtToken token) {
        super(token);
    }

    public List<CtIdentifier> getIdentifiers() {
        return identifiers;
    }

    public CtInsertStatement addIdentifier(CtIdentifier identifier) {
        identifiers.add(identifier);
        return this;
    }

    public CtSeparator getSeparator() {
        return separator;
    }

    public CtInsertStatement setSeparator(CtSeparator separator) {
        this.separator = separator;
        return this;
    }
}

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

import io.github.qishr.cascara.lang.ct.internal.CtType;
import io.github.qishr.cascara.lang.ct.token.CtToken;

public class CtPaletteDeclaration extends CtDeclaration {
    // private CtPaletteAssignmentValue value;

    public CtPaletteDeclaration(CtToken token, CtIdentifier identifier, CtAssignmentValue value) {
        super(token, CtType.PALETTE, identifier, value);
        // this.value = value;
    }

    public CtPaletteAssignmentValue getAssignmentValue() {
        return (CtPaletteAssignmentValue) assignment;
    }
}

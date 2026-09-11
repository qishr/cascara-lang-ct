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

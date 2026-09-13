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

public class CtTemplateDeclaration extends CtDeclaration {
    // private CtTemplate template;

    public CtTemplateDeclaration(CtToken token, CtIdentifier identifier, CtTemplate template) {
        super(token, CtType.TEMPLATE, identifier, template);
        // this.template = template;
    }

    public CtTemplate getAssignmentValue() {
        return (CtTemplate) assignment;
    }
}

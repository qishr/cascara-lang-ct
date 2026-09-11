package io.github.qishr.cascara.lang.ct.ast;

import io.github.qishr.cascara.lang.ct.internal.CtType;
import io.github.qishr.cascara.lang.ct.token.CtToken;

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

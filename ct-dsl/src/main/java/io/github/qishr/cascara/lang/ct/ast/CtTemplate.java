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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import io.github.qishr.cascara.lang.ct.token.CtToken;

public class CtTemplate extends CtAssignmentValue {
    private List<CtNode> content = new ArrayList<>();
    private CtIdentifier identifier;
    private URI uri;
    private boolean isAllOnOneLine;

    public CtTemplate(CtToken token) {
        super(token);
    }

    public CtTemplate addContent(CtNode node) {
        content.add(node);
        return this;
    }

    public CtTemplate setIdentifier(CtIdentifier identifier) {
        this.identifier = identifier;
        return this;
    }

    public List<CtNode> getContent() {
        return content;
    }

    public CtIdentifier getIdentifier() {
        return identifier;
    }

    public URI getUri() {
        return uri;
    }

    public CtTemplate setUri(URI uri) {
        this.uri = uri;
        return this;
    }

    public boolean isAllOnOneLine() {
        return isAllOnOneLine;
    }

    public CtTemplate setAllOnOneLine(boolean b) {
        isAllOnOneLine = b;
        return this;
    }

}

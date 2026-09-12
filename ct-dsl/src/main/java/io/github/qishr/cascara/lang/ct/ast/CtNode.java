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

import java.util.List;

import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.common.lang.ast.CommentAstNode;
import io.github.qishr.cascara.lang.ct.token.CtToken;

public class CtNode implements AstNode {
    private CtToken token;
    private int line;
    private int column;

    protected CtNode(CtToken token) {
        if (token == null) return;
        this.token = token;
        this.line = token.getStartLine();
        this.column = token.getStartColumn();
    }

    @Override
    public CtToken getToken() {
        return token;
    }

    @Override
    public int getStartLine() {
        return line;
    }

    @Override
    public int getStartColumn() {
        return column;
    }

    @Override
    public int getEndLine() {
        return 0;
    }

    @Override
    public int getEndColumn() {
        return 0;
    }

    @Override
    public List<? extends AstNode> getChildren() {
        return List.of();
    }

    @Override
    public List<? extends CommentAstNode> getComments() {
        return List.of();
    }

}

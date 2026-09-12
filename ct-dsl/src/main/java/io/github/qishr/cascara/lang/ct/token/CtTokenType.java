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

package io.github.qishr.cascara.lang.ct.token;

import io.github.qishr.cascara.common.lang.token.TokenCategory;
import io.github.qishr.cascara.common.lang.token.TokenType;

public enum CtTokenType implements TokenType {
    // Punctuation
    COMMA(TokenCategory.PUNCTUATION),
    DOT(TokenCategory.PUNCTUATION),
    PERCENT(TokenCategory.PUNCTUATION),
    SEMICOLON(TokenCategory.PUNCTUATION),
    EQUALS(TokenCategory.PUNCTUATION),
    HASH(TokenCategory.PUNCTUATION),
    OPEN_BRACE(TokenCategory.PUNCTUATION),
    CLOSE_BRACE(TokenCategory.PUNCTUATION),
    OPEN_PAREN(TokenCategory.PUNCTUATION),
    CLOSE_PAREN(TokenCategory.PUNCTUATION),
    OPEN_TEMPLATE(TokenCategory.PUNCTUATION),
    CLOSE_TEMPLATE(TokenCategory.PUNCTUATION),

    KEYWORD(TokenCategory.IDENTIFIER),
    DECLARATION(TokenCategory.IDENTIFIER),
    IDENTIFIER(TokenCategory.IDENTIFIER),
    STRING(TokenCategory.IDENTIFIER),
    NUMBER(TokenCategory.IDENTIFIER),

    COMMENT(TokenCategory.COMMENT),

    // Parser‑only tokens
    EOF(TokenCategory.INTERNAL),

    // Error
    ERROR(TokenCategory.ERROR);


    private final TokenCategory category;

    CtTokenType(TokenCategory category) {
        this.category = category;
    }

    @Override
    public String getId() {
        return name();
    }

    @Override
    public TokenCategory getCategory() {
        return category;
    }
}

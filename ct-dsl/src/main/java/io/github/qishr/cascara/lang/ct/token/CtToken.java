package io.github.qishr.cascara.lang.ct.token;

import java.net.URI;

import io.github.qishr.cascara.common.lang.token.Token;
import io.github.qishr.cascara.common.util.StringUtils;
import io.github.qishr.cascara.lang.ct.internal.CtKeyword;
import io.github.qishr.cascara.lang.ct.internal.CtType;

public class CtToken  implements Token {
    private URI uri;
    private int line;
    private int column;
    private int offset;
    private CtTokenType tokenType;
    private String lexeme;
    private CtKeyword keyword;
    private CtType declarationType;
    private boolean isPreceededByNewLine;

    /// Structural Token
    public CtToken(
        URI uri,
        int line,
        int column,
        int offset,
        CtTokenType tokenType)
    {
        this(uri, line, column, offset, tokenType, null);
    }

    public CtToken(
        URI uri,
        int line,
        int column,
        int offset,
        CtKeyword keyword)
    {
        this(uri, line, column, offset, CtTokenType.KEYWORD, keyword.toString());
        this.keyword = keyword;
    }

    public CtToken(
        URI uri,
        int line,
        int column,
        int offset,
        CtType declarationType)
    {
        this(uri, line, column, offset, CtTokenType.DECLARATION, declarationType.toString());
        this.declarationType = declarationType;
    }

    public CtToken(
        URI uri,
        int line,
        int column,
        int offset,
        CtTokenType type,
        String lexeme)
    {
        this.uri = uri;
        this.line = line;
        this.column = column;
        this.offset = offset;
        this.tokenType = type;
        this.lexeme = lexeme;
    }

    public URI getUri() {
        return uri;
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
    public int getOffset() {
        return offset;
    }

    @Override
    public CtTokenType getType() {
        return tokenType;
    }

    @Override
    public String getLexeme() {
        return lexeme;
    }

    @Override
    public String getContent() {
        return lexeme;
    }

    public CtKeyword getKeyword() {
        return keyword;
    }

    public CtType getCtType() {
        return declarationType;
    }

    public CtToken setTokenType(CtTokenType type) {
        this.tokenType = type;
        return this;
    }

    public boolean isPreceededByNewLine() {
        return isPreceededByNewLine;
    }

    public CtToken setPreceededByNewLine(boolean b) {
        isPreceededByNewLine = b;
        return this;
    }

    //
    //
    //

    public boolean isIdentifier() {
        return tokenType == CtTokenType.IDENTIFIER;
    }

    public boolean isKeyword() {
        return tokenType == CtTokenType.KEYWORD;
    }

    public boolean isDeclaration() {
        return tokenType == CtTokenType.DECLARATION;
    }

    public boolean isType() {
        return declarationType != null;
    }

    //
    //
    //

    @Override
    public String toString() {
        String displayLexeme = StringUtils.debugString(16, lexeme);
        return String.format("[%s %d:%d %s]",
            tokenType,
            line,
            column,
            displayLexeme
        );
    }
}

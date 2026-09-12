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

package io.github.qishr.cascara.lang.ct.processor;

import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import io.github.qishr.cascara.common.diagnostic.Diagnostic.Level;
import io.github.qishr.cascara.common.diagnostic.NoOpReporter;
import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.diagnostic.code.DiagnosticCode;
import io.github.qishr.cascara.common.lang.processor.AstParser;
import io.github.qishr.cascara.common.lang.util.LanguageOptions;
import io.github.qishr.cascara.common.util.ContentType;
import io.github.qishr.cascara.common.property.Properties;
import io.github.qishr.cascara.common.util.TermUtils;
import io.github.qishr.cascara.lang.ct.ast.CtAssignment;
import io.github.qishr.cascara.lang.ct.ast.CtAssignmentValue;
import io.github.qishr.cascara.lang.ct.ast.CtDeclaration;
import io.github.qishr.cascara.lang.ct.ast.CtDocument;
import io.github.qishr.cascara.lang.ct.ast.CtIdentifier;
import io.github.qishr.cascara.lang.ct.ast.CtIfStatement;
import io.github.qishr.cascara.lang.ct.ast.CtImport;
import io.github.qishr.cascara.lang.ct.ast.CtInclude;
import io.github.qishr.cascara.lang.ct.ast.CtInsertStatement;
import io.github.qishr.cascara.lang.ct.ast.CtIterateStatement;
import io.github.qishr.cascara.lang.ct.ast.CtMethodCall;
import io.github.qishr.cascara.lang.ct.ast.CtName;
import io.github.qishr.cascara.lang.ct.ast.CtNode;
import io.github.qishr.cascara.lang.ct.ast.CtPaletteAssignmentValue;
import io.github.qishr.cascara.lang.ct.ast.CtParameter;
import io.github.qishr.cascara.lang.ct.ast.CtSeparator;
import io.github.qishr.cascara.lang.ct.ast.CtSingleNameStatement;
import io.github.qishr.cascara.lang.ct.ast.CtStatement;
import io.github.qishr.cascara.lang.ct.ast.CtString;
import io.github.qishr.cascara.lang.ct.ast.CtTemplate;
import io.github.qishr.cascara.lang.ct.diagnostic.CtDiagnosticCode;
import io.github.qishr.cascara.lang.ct.diagnostic.CtParserException;
import io.github.qishr.cascara.lang.ct.internal.CtKeyword;
import io.github.qishr.cascara.lang.ct.internal.CtType;
import io.github.qishr.cascara.lang.ct.internal.PreloadedTokenBuffer;
import io.github.qishr.cascara.lang.ct.internal.TokenBuffer;
import io.github.qishr.cascara.lang.ct.token.CtErrorToken;
import io.github.qishr.cascara.lang.ct.token.CtToken;
import io.github.qishr.cascara.lang.ct.token.CtTokenType;

public class CtAstParser implements AstParser<CtNode, CtToken, CtTokenizer> {
    private Reporter reporter = new NoOpReporter();
    private CtTokenizer tokenizer;
    private TokenBuffer tokenBuffer;
    private CtDocument document;
    private int depth;
    private URI uri;

    @Override
    public Properties getServiceProperties() {
        throw new UnsupportedOperationException("Unimplemented method 'getServiceProperties'");
    }

    @Override
    public ContentType getContentType() {
        throw new UnsupportedOperationException("Unimplemented method 'getContentType'");
    }

    @Override
    public CtAstParser setReporter(Reporter reporter) {
        this.reporter = reporter == null ? new NoOpReporter() : reporter;
        return this;
    }

    @Override
    public CtAstParser setOptions(LanguageOptions<?> options) {
        throw new UnsupportedOperationException("Unimplemented method 'setOptions'");
    }

    public CtAstParser setUri(URI uri) {
        this.uri = uri;
        return this;
    }

    //
    //
    //

    @Override
    public CtNode parse(String text) {
        setup();
        tokenBuffer.open(text);
        return parseDocument();
    }

    @Override
    public CtNode parse(Reader reader) {
        setup();
        tokenBuffer.open(reader);
        return parseDocument();
    }

    @Override
    public CtNode parse(InputStream is) {
        setup();
        tokenBuffer.open(is);
        return parseDocument();
    }

    @Override
    public CtNode parse(CtTokenizer tokenizer) {
        setup();
        tokenBuffer.setTokenizer(tokenizer);
        return parseDocument();
    }

    @Override
    public CtNode parse(List<CtToken> tokens) {
        setup();
        PreloadedTokenBuffer preloaded = new PreloadedTokenBuffer();
        preloaded.preload(tokens);
        return parseDocument();
    }

    public CtTokenizer getTokenizer() {
        if (tokenizer == null) {
            tokenizer = new CtTokenizer();
            // We don't set the tokenizer's reporter.
            // If the caller wants to set it, they should use:
            //   parser.getTokenizer().setReporter()
            // tokenizer.setOptions(options);
        }
        return tokenizer;
    }

    public List<CtToken> getTokens() {
        if (tokenBuffer instanceof PreloadedTokenBuffer preloaded) {
            return preloaded.getTokens();
        }
        return List.of();
    }

    //
    //
    //

    private CtNode parseDocument() {
        debug(">parseDocument");
        depth++;
        try {
            tokenBuffer.getTokenizer().setUri(uri);
            document = new CtDocument();
            while (!tokenBuffer.isAtEnd()) {
                CtToken token = tokenBuffer.peek();
                if (token.isKeyword()) {
                    CtStatement statement = parseStatement();
                    if (statement != null) {
                        document.addStatement(statement);
                    }
                } else if (token.isDeclaration()) {
                    CtDeclaration declaration = parseDeclaration();
                    if (declaration != null) {
                        document.addStatement(declaration);
                    }
                    continue;
                } else if (token.isIdentifier()) {
                    if (isFollowedBy(CtTokenType.EQUALS)) {
                        document.addStatement(parseAssignment());
                        continue;
                    } else {
                        CtName name = parseName();
                        if (name == null) {
                            error(token, CtDiagnosticCode.UNEXPECTED_TOKEN, token);
                            skipError();
                        } else {
                            CtSingleNameStatement statement = new CtSingleNameStatement(token);
                            statement.setName(name);
                            document.addStatement(statement);
                            if (tokenBuffer.peek().getType() == CtTokenType.SEMICOLON) {
                                tokenBuffer.advance();
                            }
                            continue;
                        }
                    }
                } else {
                    error(token, CtDiagnosticCode.UNEXPECTED_TOKEN, token);
                    skipError();
                }
            }
            return document;
        } finally {
            depth--;
            debug("<parseDocument");
        }
    }

    private CtStatement parseStatement() {
        debug(">parseStatement");
        depth++;
        try {
            CtToken token = tokenBuffer.peek();
            CtKeyword keyword = token.getKeyword();
            if (keyword == CtKeyword.INCLUDE) {
                document.addStatement(parseInclude());
                return null;
            }
            else if (keyword == CtKeyword.IMPORT) {
                document.addImport(parseImport());
                return null;
            }
            else if (keyword == CtKeyword.INSERT) {
                return parseInsertStatement();
            }
            else if (keyword == CtKeyword.ITERATE) {
                return parseIterateStatement();
            }
            else if (keyword == CtKeyword.IF) {
                return parseIfStatement();
            } else {
                error(token, CtDiagnosticCode.UNEXPECTED_TOKEN, token);
                skipError();
                return null;
            }
        } finally {
            depth--;
            debug("<parseStatement");
        }
    }

    private CtImport parseImport() {
        debug(">parseImport");
        depth++;
        try {
            tokenBuffer.advance(); // consume the import keyword
            CtName packageName = parseName();
            consume(CtTokenType.SEMICOLON);
            return new CtImport(packageName);
        } finally {
            depth--;
            debug("<parseImport");
        }
    }

    private CtInclude parseInclude() {
        debug(">parseInclude");
        depth++;
        try {
            CtToken token = tokenBuffer.advance(); // consume the include keyword
            CtString fileName = parseString();
            consume(CtTokenType.SEMICOLON);
            return new CtInclude(token, fileName);
        } finally {
            depth--;
            debug("<parseInclude");
        }
    }

    //
    // Statement
    //

    private CtInsertStatement parseInsertStatement() {
        debug(">parseInsertStatement");
        depth++;
        try {
            CtToken token = tokenBuffer.advance();
            CtInsertStatement statement = new CtInsertStatement(token);
            token = tokenBuffer.peek();
            while (token.getType() != CtTokenType.SEMICOLON && !tokenBuffer.isAtEnd()) {
                CtKeyword keyword = CtKeyword.of(token.getLexeme());
                if (keyword == CtKeyword.SEPARATOR) {
                    statement.setSeparator(parseSeparator());
                } else if (token.getType() == CtTokenType.COMMA) {
                    tokenBuffer.advance();
                } else {
                    CtIdentifier identifier = parseIdentifier();
                    if (identifier != null) {
                        statement.addIdentifier(identifier);
                    }
                }
                token = tokenBuffer.peek();
            }
            if (tokenBuffer.peek().getType() == CtTokenType.SEMICOLON) {
                tokenBuffer.advance();
            }
            return statement;
        } finally {
            depth--;
            debug("<parseInsertStatement");
        }
    }

    private CtIterateStatement parseIterateStatement() {
        debug(">parseIterateStatement");
        depth++;
        try {
            CtToken token = tokenBuffer.advance();
            CtIterateStatement statement = new CtIterateStatement(token);

            CtIdentifier identifier = parseIdentifier();
            if (identifier != null) {
                statement.setIdentifier(identifier);
            }

            token = tokenBuffer.peek();
            CtKeyword keyword = CtKeyword.of(token.getLexeme());
            if (keyword == CtKeyword.SEPARATOR) {
                statement.setSeparator(parseSeparator());
            } else {
                // TODO: Is this necessary?
                // error(token, CtDiagnosticCode.UNEXPECTED_TOKEN, token);
            }
            if (tokenBuffer.peek().getType() == CtTokenType.SEMICOLON) {
                tokenBuffer.advance();
            }
            return statement;
        } finally {
            depth--;
            debug("<parseIterateStatement");
        }
    }

    private CtIfStatement parseIfStatement() {
        // TODO
        return null;
    }

    private CtSeparator parseSeparator() {
        debug(">parseSeparator");
        depth++;
        try {
            CtSeparator separator = new CtSeparator(tokenBuffer.peek());
            tokenBuffer.advance();
            CtToken token = tokenBuffer.peek();
            if (token.getType() == CtTokenType.IDENTIFIER) {
                separator.setIdentifier(parseIdentifier());
            }
            else if (token.getType() == CtTokenType.STRING) {
                separator.setString(parseString());
            }
            else {
                error(token, CtDiagnosticCode.UNEXPECTED_TOKEN, token);
                skipError();
            }
            return separator;
        } finally {
            depth--;
            debug("<parseSeparator");
        }
    }

    //
    // Declaration
    //

    private CtDeclaration parseDeclaration() {
        debug(">parseDeclaration");
        depth++;
        try {
            CtToken startToken = tokenBuffer.peek();
            CtType type = startToken.getCtType();
            CtIdentifier identifier = null;
            CtAssignmentValue assignment = null;
            tokenBuffer.advance(); // consume the type
            if (isFollowedBy(CtTokenType.EQUALS)) {
                identifier = parseIdentifier();
                consume(CtTokenType.EQUALS);
                assignment = parseValue();
                assignment.setToType(type);
            } else {
                identifier = parseIdentifier();
            }
            consume(CtTokenType.SEMICOLON);
            return new CtDeclaration(startToken, type, identifier, assignment);
        } finally {
            depth--;
            debug("<parseDeclaration");
        }
    }

    private CtAssignment parseAssignment() {
        debug(">parseAssignment");
        depth++;
        try {
            CtName name = parseName();
            consume(CtTokenType.EQUALS);
            CtAssignmentValue value = parseValue();
            consume(CtTokenType.SEMICOLON);
            return new CtAssignment(name, value);
        } finally {
            depth--;
            debug("<parseAssignment");
        }
    }

    //
    // Method
    //

    private CtMethodCall parseMethodCall() {
        debug(">parseMethod");
        depth++;
        try {
            CtToken startToken = tokenBuffer.peek();
            CtMethodCall method = new CtMethodCall(startToken);
            CtIdentifier methodName = parseIdentifier();
            consume(CtTokenType.OPEN_PAREN);
            List<CtParameter> parameters = parseParameters();
            consume(CtTokenType.CLOSE_PAREN);
            method.setName(methodName);
            method.setParameters(parameters);
            return method;
        } finally {
            depth--;
            debug("<parseMethod");
        }
    }

    public List<CtParameter> parseParameters() {
        debug(">parseParameters");
        depth++;
        try {
            List<CtParameter> list = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            CtToken paramStart = null;
            while (!tokenBuffer.isAtEnd() && tokenBuffer.peek().getType() != CtTokenType.CLOSE_PAREN) {
                CtToken token = tokenBuffer.peek();
                if (paramStart == null) {
                    paramStart = token;
                }
                if (token.getType() == CtTokenType.COMMA) {
                    list.add(parseParameter(paramStart, sb.toString()));
                    sb.setLength(0);
                    paramStart = null;
                    tokenBuffer.advance();
                } else {
                    String lexeme = token.getLexeme();
                    sb.append(lexeme);
                    tokenBuffer.advance();
                }
            }
            list.add(parseParameter(paramStart, sb.toString()));
            return list;
        } finally {
            depth--;
            debug("<parseParameters");
        }
    }

    private CtParameter parseParameter(CtToken paramStart, String text) {
        CtParameter param;
        try {
            boolean isPercentage = false;
            boolean isRelative = false;
            if (text.endsWith("%")) {
                isPercentage = true;
                text = text.substring(0, text.length() - 1);
            }
            if (text.startsWith("+") || text.startsWith("-")) {
                isRelative = true;
            }
            Number number;
            if (text.contains(".")) {
                number = Double.parseDouble(text.trim());
            } else {
                number = Integer.parseInt(text.trim());
            }
            param = new CtParameter(paramStart, number, isPercentage, isRelative);
        } catch (Exception e) {
            param = new CtParameter(paramStart, text.trim());
        }
        return param;
    }

    //
    // Values
    //

    private CtAssignmentValue parseValue() {
        debug(">parseValue");
        depth++;
        try {
            CtTokenType tokenType = tokenBuffer.peek().getType();
            if (tokenType == CtTokenType.OPEN_BRACE) {
                return parseCollection();
            } else if (tokenType == CtTokenType.OPEN_TEMPLATE) {
                return parseTemplate();
            } else {
                if (isFollowedBy(CtTokenType.OPEN_PAREN)) {
                    return parseMethodCall();
                } else {
                    return parseScalar();
                }
            }
        } finally {
            depth--;
            debug("<parseValue");
        }
    }

    private CtPaletteAssignmentValue parseCollection() {
        debug(">parseCollection");
        depth++;
        try {
            CtToken startToken = tokenBuffer.peek();
            CtPaletteAssignmentValue value = new CtPaletteAssignmentValue(startToken);
            if (tokenBuffer.peek().getType() == CtTokenType.OPEN_BRACE) {
                consume(CtTokenType.OPEN_BRACE);
                while (!tokenBuffer.isAtEnd() && tokenBuffer.peek().getType() != CtTokenType.CLOSE_BRACE) {
                    CtToken token = tokenBuffer.peek();
                    if (token.isKeyword()) {
                        CtStatement statement = parseStatement();
                        if (statement != null) {
                            value.addStatement(statement);
                        } else {
                            error(token, CtDiagnosticCode.UNEXPECTED_TOKEN, token);
                            skipError();
                        }
                    } else if (token.isIdentifier()) {
                        if (isFollowedBy(CtTokenType.EQUALS)) {
                            value.addStatement(parseAssignment());
                            continue;
                        } else {
                            error(token, CtDiagnosticCode.UNEXPECTED_TOKEN, token);
                            skipError();
                        }
                    } else if (token.isType()) {
                        value.addStatement(parseDeclaration());
                        continue;
                    } else {
                        error(token, CtDiagnosticCode.UNEXPECTED_TOKEN, token);
                        skipError();
                    }
                }
                consume(CtTokenType.CLOSE_BRACE);
            } else {

                // TODO: assign from pelette

            }

            return value;
        } finally {
            depth--;
            debug("<parseCollection");
        }
    }

    private CtTemplate parseTemplate() {
        debug(">parseTemplate");
        depth++;
        try {
            CtToken startToken = consume(CtTokenType.OPEN_TEMPLATE);
            CtTemplate template = new CtTemplate(startToken);

            while (!tokenBuffer.isAtEnd() && tokenBuffer.peek().getType() != CtTokenType.CLOSE_TEMPLATE) {
                CtToken token = tokenBuffer.peek();
                if (token.getType() == CtTokenType.OPEN_TEMPLATE) {
                    CtTemplate nested = parseTemplate();
                    template.addContent(nested);
                } else {
                    CtString string = new CtString(token, token.getLexeme());
                    // CtString string = new CtString(token, token.getLexeme().trim());
                    template.addContent(string);
                    tokenBuffer.advance();
                }
            }

            if (template.getContent().size() == 1) {
                CtToken endToken = tokenBuffer.peek();
                if (endToken.getStartLine() == startToken.getStartLine()) {
                    template.setAllOnOneLine(true);
                }
            }

            consume(CtTokenType.CLOSE_TEMPLATE);
            return template;
        } finally {
            depth--;
            debug("<parseTemplate");
        }
    }

    private CtAssignmentValue parseScalar() {
        debug(">parseScalar");
        depth++;
        try {
            CtToken startToken = tokenBuffer.peek();
            CtTokenType tokenType = startToken.getType();
            CtAssignmentValue value = new CtAssignmentValue(startToken);
            // if (isFollowedBy(CtTokenType.OPEN_PAREN)) {
            //     value.setMethodCall(parseMethodCall());
            // } else {

                if (tokenBuffer.peek().getType() == CtTokenType.OPEN_BRACE) {

                } else if (tokenType == CtTokenType.OPEN_TEMPLATE) {

                } else if (tokenType == CtTokenType.STRING) {
                    value.setString(parseString());
                } else if (tokenType == CtTokenType.HASH) {
                    value.setHexValue(parseHexColor());
                } else {
                    value.setName(parseName());
                }

            // }
            return value;
        } finally {
            depth--;
            debug("<parseScalar");
        }
    }

    private String parseHexColor() {
        debug(">parseHexColor");
        depth++;
        try {
            tokenBuffer.advance(); // consume the #
            CtToken token = tokenBuffer.advance();
            return "#" + token.getContent();
        } finally {
            depth--;
            debug("<parseHexColor");
        }
    }

    private CtString parseString() {
        debug(">parseString");
        depth++;
        try {
            CtToken token = consume(CtTokenType.STRING);
            CtString string = new CtString(token, token.getContent());
            return string;
        } finally {
            depth--;
            debug("<parseString");
        }
    }

    //
    // Identifiers
    //

    private CtName parseName() {
        debug(">parseName");
        depth++;
        try {
            CtToken startToken = tokenBuffer.peek();
            StringBuilder sb = new StringBuilder();

            CtIdentifier identifier = parseIdentifier();
            sb.append(identifier.getName());

            CtToken token = tokenBuffer.peek();
            while (token.getType() == CtTokenType.DOT) {
                consume(CtTokenType.DOT);
                sb.append(".");
                token = tokenBuffer.peek();
                if (token.getType() == CtTokenType.KEYWORD) {
                    identifier = new CtIdentifier(token);
                    tokenBuffer.advance();
                } else {
                    identifier = parseIdentifier();
                }
                sb.append(identifier.getName());
                token = tokenBuffer.peek();
            }

            return new CtName(startToken, sb.toString());
        } finally {
            depth--;
            debug("<parseName");
        }
    }

    private CtIdentifier parseIdentifier() {
        debug(">parseIdentifier");
        depth++;
        try {
            CtToken token = consume(CtTokenType.IDENTIFIER);
            return new CtIdentifier(token);
        } finally {
            depth--;
            debug("<parseIdentifier");
        }
    }

    //
    // Helpers
    //

    private CtToken consume(CtTokenType type) {
        CtToken token = tokenBuffer.advance();
        if (token.getType() != type) {
            error(token, CtDiagnosticCode.EXPECTED, type, token.getType());
        }
        return token;
    }

    private void skipError() {
        while (!tokenBuffer.isAtEnd() && tokenBuffer.peek().getType() == CtTokenType.SEMICOLON) {
            tokenBuffer.advance();
        }
        while (!tokenBuffer.isAtEnd() && tokenBuffer.peek().getType() != CtTokenType.SEMICOLON) {
            tokenBuffer.advance();
        }
    }

    private boolean isFollowedBy(CtTokenType type) {
        int ahead = 1;
        int offset = tokenBuffer.offset();
        while (offset + ahead < tokenBuffer.size()) {
            CtToken token = tokenBuffer.peekAhead(ahead);
            if (token.getType() == CtTokenType.EOF ||
                token.getType() == CtTokenType.SEMICOLON) {
                return false;
            }
            if (token.getType() == type) {
                return true;
            }
            ahead++;
        }
        return false;
    }

    //
    // Setup and Diagnostics
    //

    private void setup() {
        tokenBuffer = new PreloadedTokenBuffer();
        tokenBuffer.setTokenizer(getTokenizer());
        tokenBuffer.getTokenizer().setUri(uri);
        tokenBuffer.getTokenizer().setReporter(reporter);
        depth = 0;
    }

    protected void error(CtToken token, DiagnosticCode code, Object... details) {
        if (token instanceof CtErrorToken error) {
            code = error.getCode();
            details = error.getDetails();
        }

        reporter.errorAt(uri, token, code, details);
        if (!reporter.collectsProblems()) {
            throw new CtParserException(token, code, details);
        }
    }

    protected void warn(CtToken token, DiagnosticCode code, Object... details) {
        reporter.warnAt(token, code, details);
    }

    private void debug(String s) {
        report(Level.DEBUG, s);
    }

    protected void report(Level level, String message, Object... details) {
        // Ensure at least the current token is loaded to grab safe coordinates
        tokenBuffer.ensureBuffered(0);
        if (tokenBuffer.isAtEnd()) return;

        // Create indentation based on recursion depth
        String indent = "  ".repeat(Math.max(0, depth));

        char first = message.charAt(0);
        String output;

        if (first == '>' || first == '<') {
            output = first + TermUtils.ANSI_YELLOW + message.substring(1) + TermUtils.ANSI_RESET;
        } else {
            output = message;
        }

        if (level == Level.TRACE) {
            reporter.trace("L%3d C%3d I%3d %s%s: %s",
                tokenBuffer.peek().getStartLine(),
                tokenBuffer.peek().getStartColumn(),
                tokenBuffer.offset(),
                indent,
                output,
                tokenBuffer.upcomingTokens());
        } else {
            reporter.debug("L%3d C%3d I%3d %s%s: %s",
                tokenBuffer.peek().getStartLine(),
                tokenBuffer.peek().getStartColumn(),
                tokenBuffer.offset(),
                indent,
                output,
                tokenBuffer.upcomingTokens());
        }
    }
}

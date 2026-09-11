package io.github.qishr.cascara.lang.ct.processor;

import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import io.github.qishr.cascara.common.diagnostic.NoOpReporter;
import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.diagnostic.UnimplementedMethodException;
import io.github.qishr.cascara.common.diagnostic.code.DiagnosticCode;
import io.github.qishr.cascara.common.lang.processor.Tokenizer;
import io.github.qishr.cascara.common.lang.util.LanguageOptions;
import io.github.qishr.cascara.common.lang.util.SourceBuffer;
import io.github.qishr.cascara.common.lang.util.SourceInputStreamBuffer;
import io.github.qishr.cascara.common.lang.util.SourceStringBuffer;
import io.github.qishr.cascara.common.util.ContentType;
import io.github.qishr.cascara.common.property.Properties;
import io.github.qishr.cascara.common.util.StringUtils;
import io.github.qishr.cascara.lang.ct.diagnostic.CtDiagnosticCode;
import io.github.qishr.cascara.lang.ct.internal.CtKeyword;
import io.github.qishr.cascara.lang.ct.internal.CtType;
import io.github.qishr.cascara.lang.ct.token.CtErrorToken;
import io.github.qishr.cascara.lang.ct.token.CtToken;
import io.github.qishr.cascara.lang.ct.token.CtTokenType;

public class CtTokenizer implements Tokenizer<CtToken> {
    private Reporter reporter = new NoOpReporter();
    private SourceBuffer buffer;
    private final Deque<CtToken> pendingTokens = new ArrayDeque<>();
    private List<CtToken> tokens = new ArrayList<>();
    private boolean streamStarted;
    private boolean streamEnded;
    private URI uri;

    String debugSource;

    @Override
    public Properties getServiceProperties() {
        // TODO Auto-generated method stub
        throw new UnimplementedMethodException();
    }

    @Override
    public ContentType getContentType() {
        // TODO Auto-generated method stub
        throw new UnimplementedMethodException();
    }

    @Override
    public CtTokenizer setReporter(Reporter reporter) {
        this.reporter = reporter == null ? new NoOpReporter() : reporter;
        return this;
    }

    @Override
    public CtTokenizer setOptions(LanguageOptions<?> options) {
        // TODO Auto-generated method stub
        throw new UnimplementedMethodException();
    }

    public CtTokenizer setUri(URI uri) {
        this.uri = uri;
        return this;
    }

    @Override
    public int getOffset() {
        return buffer.offset();
    }

    // @Override
    public void open(byte[] data) {
        setup(new SourceStringBuffer().open(new String(data)));
    }

    @Override
    public void open(String text) {
        setup(new SourceStringBuffer().open(text));
    }

    @Override
    public void open(Reader reader) {
        setup(new SourceInputStreamBuffer().open(reader));
    }

    @Override
    public void open(InputStream is) {
        setup(new SourceInputStreamBuffer().open(is));
    }

    @Override
    public List<CtToken> tokenize(byte[] data) {
        return tokenize(new String(data, StandardCharsets.UTF_8));
    }

    @Override
    public List<CtToken> tokenize(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }

        open(text);

        // Drain the stream using the sequential nextToken logic
        CtToken token;
        while ((token = nextToken()) != null) {
            if (token.getType() == CtTokenType.EOF) {
                break;
            }
        }
        return this.tokens;
    }

    @Override
    public CtToken nextToken() {
        // 1. Flush any tokens queued up by structural blocks first
        if (!pendingTokens.isEmpty()) {
            return queueToken(pendingTokens.pollFirst());
        }

        if (streamEnded) {
            return null;
        }
        if (!streamStarted) {
            streamStarted = true;
            // return queueToken(new CtToken(buffer.line(), buffer.column(), buffer.offset(), YamlTokenType.STREAM_START));
        }
        // 2. Loop until we either find a token or hit the end of the input buffer
        while (!buffer.isAtEnd()) {
            buffer.startTokenWindow();

            int previousOffset = buffer.offset();
            scanToken();
            if (buffer.offset() == previousOffset) {
                error(CtDiagnosticCode.ERROR, "Unscanned characters");
                return null;
            }

            // char shouldBeNewline = buffer.peek();
            int pos = buffer.offset();

            if (!buffer.isAtEnd()) {

                // char again = buffer.charAt(pos);
                debug(StringUtils.debugString(this.debugSource, pos));

                if (buffer.peek() == '\r' || buffer.peek() == '\n') {
                    // handleNewlineAndIndentation(advance());
                    buffer.advance();
                }
            }
        }

        // 3. If scanning populated tokens, return the first one
        if (!pendingTokens.isEmpty()) {
            return queueToken(pendingTokens.pollFirst());
        }

        // 4. Handle stream wrap-up structural tokens when the underlying stream drains
        if (buffer.isAtEnd()) {
            int finalLine = buffer.line();
            int finalCol = buffer.column();
            int finalOffset = buffer.offset();

            streamEnded = true;

            CtToken eofToken = new CtToken(uri, finalLine, finalCol, finalOffset, CtTokenType.EOF);

            pendingTokens.add(eofToken);
            return queueToken(pendingTokens.pollFirst());
        }

        return null;
    }

    private void scanToken() {
        trace("scanToken");

        char c = buffer.peek();

        if (isWhitespace(c)) {
            buffer.advance();
            return;
        }
        if (c == ',') {
            addToken(CtTokenType.COMMA, c);
            buffer.advance();
            return;
        }
        if (c == '"') {
            scanString();
            return;
        }
        if (c == '.') {
            addToken(CtTokenType.DOT, c);
            buffer.advance();
            return;
        }
        if (c == ';') {
            addToken(CtTokenType.SEMICOLON, c);
            buffer.advance();
            return;
        }
        if (c == '%') {
            addToken(CtTokenType.PERCENT, c);
            buffer.advance();
            return;
        }
        if (c == '=') {
            addToken(CtTokenType.EQUALS, c);
            buffer.advance();
            return;
        }
        if (c == '#') {
            addToken(CtTokenType.HASH, c);
            buffer.advance();
            return;
        }
        if (c == '{') {
            if (buffer.peekAhead(1) == '{' && buffer.peekAhead(2) == '{') {
                scanTemplate();
            } else {
                addToken(CtTokenType.OPEN_BRACE, c);
                buffer.advance();
            }
            return;
        }
        if (c == '}') {
            // if (buffer.peekAhead(1) == '}' && buffer.peekAhead(2) == '}') {
            // } else {
                addToken(CtTokenType.CLOSE_BRACE, c);
                buffer.advance();
            // }
            return;
        }
        if (c == '(') {
            addToken(CtTokenType.OPEN_PAREN, c);
            buffer.advance();
            return;
        }
        if (c == ')') {
            addToken(CtTokenType.CLOSE_PAREN, c);
            buffer.advance();
            return;
        }
        if (c == '/' && buffer.peekAhead(1) == '/') {
            scanComment();
            return;
        }
        if (isAlpha(c) || isNumeric(c)) {
            scanText();
            return;
        }
        error(CtDiagnosticCode.UNEXPECTED_CHAR, c);
    }

    private void scanTemplate() {
        addToken(CtTokenType.OPEN_TEMPLATE, "{{{");
        buffer.advance();
        buffer.advance();
        buffer.advance();

        // setPreceededByNewLine

        StringBuilder sb = new StringBuilder();
        while (!buffer.isAtEnd() && !(
            buffer.peek() == '}' &&
            buffer.peekAhead(1) == '}' &&
            buffer.peekAhead(2) == '}'
        )) {
            char c = buffer.peek();
            if (c == '{' &&
                buffer.peekAhead(1) == '{' &&
                buffer.peekAhead(1) == '{'
            ) {
                if (!sb.isEmpty()) {
                    CtToken stringToken = new CtToken(
                        uri,
                        buffer.line(),
                        buffer.column(),
                        buffer.offset(),
                        CtTokenType.STRING,
                        sb.toString()
                    );
                    // stringToken.setPreceededByNewLine();
                    addToken(stringToken);
                    sb.setLength(0);
                }
                scanTemplate();
            } else {
                sb.append(c);
                buffer.advance();
            }
        }
        if (!sb.isEmpty()) {
            CtToken stringToken = new CtToken(
                uri,
                buffer.line(),
                buffer.column(),
                buffer.offset(),
                CtTokenType.STRING,
                sb.toString()
            );
            addToken(stringToken);
        }

        addToken(CtTokenType.CLOSE_TEMPLATE, "}}}");
        buffer.advance();
        buffer.advance();
        buffer.advance();

    }

    private void scanString() {
        int startLine = buffer.windowStartLine();
        int startColumn = buffer.windowStartColumn();
        int startOffest = buffer.windowStartOffset();

        buffer.advance(); // Consume the opening quote

        StringBuilder sb = new StringBuilder();
        while (!buffer.isAtEnd() && buffer.peek() != '"') {
            sb.append(buffer.advance());
        }
        String content = sb.toString();
        buffer.advance(); // Consume the closing quote


        CtToken token = new CtToken(
            uri,
            startLine,
            startColumn,
            startOffest,
            CtTokenType.STRING,
            content
        );
        addToken(token);
    }

    private void scanText() {
        int ahead = 0;
        char c = buffer.peek();
        boolean isNumeric = true;
        while (c != '\0') {
            c = buffer.peekAhead(ahead);
            if (!isNumeric(c)) {
                if (!isIdentifier(c)) {
                    break;
                } else {
                    isNumeric = false;
                }
            }
            ahead++;
        }
        if (isNumeric) {
            scanNumber();
        } else {
            scanIdentifier();
        }
    }

    private CtTokenType classify(String name) {
        CtKeyword keyword = CtKeyword.of(name);
        CtType declarationType = CtType.of(name);
        return (keyword == null && declarationType == null)
            ? CtTokenType.IDENTIFIER
            : keyword == null
                ? CtTokenType.DECLARATION
                : CtTokenType.KEYWORD;
    }

    private void scanIdentifier() {
        StringBuilder sb = new StringBuilder();
        while (!buffer.isAtEnd() && isIdentifier(buffer.peek())) {
            sb.append(buffer.advance());
        }
        String content = sb.toString();
        CtToken token;

        CtTokenType type = classify(content);

        if (type == CtTokenType.KEYWORD) {
            CtKeyword keyword = CtKeyword.of(content);
            token = new CtToken(
                uri,
                buffer.line(),
                buffer.column(),
                buffer.offset(),
                keyword
            );
        } else if (type == CtTokenType.DECLARATION) {
            CtType declarationType = CtType.of(content);
            token = new CtToken(
                uri,
                buffer.line(),
                buffer.column(),
                buffer.offset(),
                declarationType
            );
        } else {
            token = new CtToken(
                uri,
                buffer.line(),
                buffer.column(),
                buffer.offset(),
                type,
                content
            );
        }

        addToken(token);
    }

    private void scanNumber() {
        StringBuilder sb = new StringBuilder();
        while (!buffer.isAtEnd() && isNumeric(buffer.peek())) {
            sb.append(buffer.advance());
        }
        String content = sb.toString();
        CtToken token;

        token = new CtToken(
            uri,
            buffer.line(),
            buffer.column(),
            buffer.offset(),
            CtTokenType.IDENTIFIER,
            content
        );

        addToken(token);
    }

    private void scanComment() {
        while (!buffer.isAtEnd() && buffer.peek() != '\n') {
            buffer.advance();
        }
    }

    //
    // Helpers
    //

    /// Small interceptor ensuring that if someone runs the old tokenize() API,
    /// tokens get copied to the collection output array correctly.
    private CtToken queueToken(CtToken token) {
        tokens.add(token);
        return token;
    }

    private CtToken addToken(CtToken token) {
        debug("addToken: " + token.getType());
        if (token != null) {
            pendingTokens.add(token); // Queue it up so nextToken() can yield it!
        }
        return token;
    }

    private CtToken addToken(CtTokenType type, char lexeme) {
        return addToken(type, "" + lexeme);
    }

    private CtToken addToken(CtTokenType type, String lexeme) {
        CtToken token = new CtToken(
            uri,
            buffer.line(),
            buffer.column(),
            buffer.offset(),
            type,
            lexeme
        );
        return addToken(token);
    }

    private boolean isWhitespace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    private boolean isIdentifier(char c) {
        return isAlpha(c) || isDigit(c) || c == '_' || c == '-' || c == '%';
    }

    private boolean isNumeric(char c) {
        return isDigit(c) || c == '+' || c == '-' || c == '.' || c == '%';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    //
    //
    //

    private void setup(SourceBuffer buffer) {
        this.buffer = buffer;
        this.tokens = new ArrayList<>();
        this.streamStarted = false;
        this.streamEnded = false;
        pendingTokens.clear();

        // Handle UTF-8 BOM if present at start of stream/string
        if (buffer.peek() == '\uFEFF') {
            buffer.advance();
        }
    }

    //
    // Errors & Diagnostics
    //

    private void error(DiagnosticCode msgCode, Object... details) {
        CtErrorToken errorToken = new CtErrorToken(
            uri,
            buffer.line(),
            buffer.column(),
            buffer.offset(),
            msgCode,
            details
        );
        addToken(errorToken);
        reporter.errorAt(uri, errorToken, msgCode, details);
    }

    private void debug(String message) {
        if (!reporter.reportsTrace()) return;
        char c = buffer.peek();
        reporter.debug("S=%03d C=%03d '%s' %03d:%03d %s",
            buffer.offset(), buffer.offset(), StringUtils.visibleChar(c), buffer.line(), buffer.column(), message);
    }

    private void trace(String message) {
        if (!reporter.reportsTrace()) return;
        char c = buffer.peek();
        reporter.trace("S=%03d C=%03d '%s' %03d:%03d %s",
            buffer.offset(), buffer.offset(), StringUtils.visibleChar(c), buffer.line(), buffer.column(), message);
    }
}

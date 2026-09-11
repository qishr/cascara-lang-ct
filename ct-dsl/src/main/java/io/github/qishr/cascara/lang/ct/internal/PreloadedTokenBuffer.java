package io.github.qishr.cascara.lang.ct.internal;

import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import io.github.qishr.cascara.common.diagnostic.Diagnostic.Level;
import io.github.qishr.cascara.common.diagnostic.NoOpReporter;
import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.util.StringUtils;
import io.github.qishr.cascara.lang.ct.processor.CtTokenizer;
import io.github.qishr.cascara.lang.ct.token.CtToken;
import io.github.qishr.cascara.lang.ct.token.CtTokenType;

public class PreloadedTokenBuffer implements TokenBuffer {
    private static final int MAX_DEBUG_STRING_LENGTH = 20;

    private final List<CtToken> tokenBuffer = new ArrayList<>(256);
    private int lastNewlineOrComment;
    private CtTokenizer tokenizer = new CtTokenizer();
    private Reporter reporter = new NoOpReporter();

    private int current = 0;

    public PreloadedTokenBuffer() {

    }

    @Override
    public void setReporter(Reporter reporter) {
        this.reporter = reporter == null ? new NoOpReporter() : reporter;
    }

    @Override
    public void setTokenizer(CtTokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    @Override
    public CtTokenizer getTokenizer() {
        return tokenizer;
    }

    @Override
    public void open(String text) {
        tokenizer.open(text);
        preload(tokenizer);
    }

    @Override
    public void open(byte[] data) {
        tokenizer.open(new String(data));
        preload(tokenizer);
    }

    @Override
    public void open(Reader reader) {
        tokenizer.open(reader);
        preload(tokenizer);
    }

    @Override
    public void open(InputStream is) {
        tokenizer.open(is);
        preload(tokenizer);
    }

    //
    //
    //

    private void preload(CtTokenizer tokenizer) {
        this.tokenizer = tokenizer;
        CtToken next;
        while ((next = tokenizer.nextToken()) != null) {
            tokenBuffer.add(next);
            if (next.getType() == CtTokenType.EOF || next.getType() == CtTokenType.EOF) {
                break;
            }
        }
    }

    public void preload(List<CtToken> tokens) {
        tokenBuffer.addAll(tokens);
    }

    public void add(CtToken token) {
        tokenBuffer.add(token);
    }


    //
    //
    //

    // The highest number this is called with is 4. It's usually called with 1.
    /// Ensures that the lookahead buffer has retrieved tokens up to the requested lookahead index offset.
    public void ensureBuffered(int ahead) {
        if (tokenizer == null) return; // Running in fixed List fallback mode

        int targetIndex = current + ahead;
        while (tokenBuffer.size() <= targetIndex) {
            CtToken next = tokenizer.nextToken();
            if (next == null) break;
            tokenBuffer.add(next);
            if (next.getType() == CtTokenType.EOF || next.getType() == CtTokenType.EOF) {
                break;
            }
        }
    }

    public List<CtToken> getTokens() {
        return tokenBuffer;
    }

    public boolean isEmpty() {
        return tokenBuffer.isEmpty();
    }

    //
    //
    //

    // This is only used by the parser to check it hasn't got stuck on a token
    public int offset() {
        return current;
    }

    public boolean isAtEnd(int ahead) {
        if (current + ahead >= tokenBuffer.size()) return true;
        CtTokenType type = tokenBuffer.get(current + ahead).getType();
        return type == CtTokenType.EOF || type == CtTokenType.EOF;
    }

    public boolean isAtEnd() {
        return isAtEnd(0);
    }


    //

    public CtToken peekAhead(int ahead) {
        int targetIndex = current + ahead;
        if (targetIndex >= tokenBuffer.size()) {
            return tokenBuffer.isEmpty() ? null : tokenBuffer.get(tokenBuffer.size() - 1);
        }
        return tokenBuffer.get(targetIndex);
    }

    public CtToken peek() {
        return tokenBuffer.get(current);
    }

    //


    public int size() {
        return tokenBuffer.size();
    }

    public CtToken previous() {
        return tokenBuffer.get(current - 1);
    }

    public CtToken advance() {
        trace("advance");
        if (!isAtEnd()) current++;
        return previous();
    }

    //

    public boolean isTrailingStreamComment() {
        return current >= lastNewlineOrComment;
    }

    //
    //
    //

    //
    // Errors and Diagnostics
    //

    // private void warn(CtToken token, DiagnosticCode code, Object... details) {
    //     reporter.warnAt(token, code, details);
    // }

    // private void error(CtToken token, DiagnosticCode code, Object... details) {
    //     if (token instanceof YamlErrorToken error) {
    //         code = error.getCode();
    //         details = error.getDetails();
    //     }

    //     reporter.errorAt(token, code, details);
    //     if (!reporter.collectsProblems()) {
    //         throw new YamlParserException(token, code, details);
    //     }
    // }

    /// Log the current method name and upcoming tokens
    private void trace(String message, Object... details) {
        if (reporter == null ||
            reporter.isSilent() ||
            !reporter.getLevel().includes(Level.TRACE)) return;
        report(message, details);
    }

    // /// Log the current method name and upcoming tokens
    // private void debug(String message, Object... details) {
    //     if (reporter == null ||
    //         reporter.isSilent() ||
    //         !reporter.getLevel().includes(Level.DEBUG)) return;
    //     report(message, details);
    // }

    private void report(String message, Object... details) {
        // Ensure at least the current token is loaded to grab safe coordinates
        ensureBuffered(0);
        if (current >= tokenBuffer.size()) return;

        // Create indentation based on recursion depth
        // String indent = "  ".repeat(Math.max(0, depth));
        String indent = "";

        char first = message.charAt(0);
        String output;

        if (first == '>' || first == '<') {
            output = first + ANSI_YELLOW + message.substring(1) + ANSI_RESET;
        } else {
            output = message;
        }

        reporter.debug("L%3d C%3d I%3d %s%s: %s",
            tokenBuffer.get(current).getStartLine(),
            tokenBuffer.get(current).getStartColumn(),
            current,
            indent,
            output,
            upcomingTokens());
    }

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_WHITE = "\u001B[37m";

    // Get next 4 tokens as a string.
    public String upcomingTokens() {
        StringBuilder sb = new StringBuilder();

        // Force up to 4 lookahead tokens into the buffer safely
        ensureBuffered(4);

        int distance = Math.min(tokenBuffer.size() - current, 4);
        for (int i = 0; i < distance; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            CtToken token = tokenBuffer.get(current + i);
            sb.append(ANSI_BLUE);
            sb.append(token.getType());
            sb.append(ANSI_RESET);
            if (token.getType() == CtTokenType.IDENTIFIER ||
                token.getType() == CtTokenType.NUMBER ||
                token.getType() == CtTokenType.KEYWORD ||
                token.getType() == CtTokenType.DECLARATION ||
                token.getType() == CtTokenType.STRING
            ){
                String lexeme = StringUtils.debugString(token.getContent());
                sb.append("(");
                sb.append(ANSI_WHITE);
                if (lexeme.length() <= MAX_DEBUG_STRING_LENGTH) {
                    sb.append(lexeme);
                } else {
                    sb.append(lexeme.substring(0, MAX_DEBUG_STRING_LENGTH - 1));
                    sb.append(StringUtils.ELLIPSIS);
                }
                sb.append(ANSI_RESET);
                sb.append(")");
            }
        }
        if (tokenBuffer.size() - current > distance) {
            sb.append(", ");
            sb.append(StringUtils.ELLIPSIS);
        }
        return sb.toString();
    }


}

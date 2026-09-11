package io.github.qishr.cascara.lang.ct.token;

import java.net.URI;

import io.github.qishr.cascara.common.diagnostic.code.DiagnosticCode;

public class CtErrorToken extends CtToken {
    private final DiagnosticCode code;
    private final Object[] details;

    public CtErrorToken(URI uri, int line, int column, int offset, DiagnosticCode code, Object... details) {
        super(uri, line, column, offset, CtTokenType.ERROR);
        this.code = code;
        this.details = details;
    }

    public DiagnosticCode getCode() {
        return code;
    }

    public Object[] getDetails() {
        return details;
    }

    @Override
    public CtTokenType getType() {
        return CtTokenType.ERROR;
    }
}

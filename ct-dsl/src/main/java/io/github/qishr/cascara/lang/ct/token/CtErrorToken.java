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

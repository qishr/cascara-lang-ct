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

package io.github.qishr.cascara.lang.ct.diagnostic;

import io.github.qishr.cascara.common.diagnostic.code.DiagnosticCode;
import io.github.qishr.cascara.common.lang.diagnostic.ParserException;
import io.github.qishr.cascara.lang.ct.token.CtToken;

public class CtParserException extends ParserException {

    /// Standard constructor for parser-detected logic errors.
    public CtParserException(int line, int column, DiagnosticCode code, Object... details) {
        super(line, column, code, details);
    }

    /// Standard constructor for parser-detected logic errors.
    public CtParserException(CtToken token, DiagnosticCode code, Object... details) {
        super(token, code, details);
    }

    /// Constructor for I/O or Stream failures.
    public CtParserException(Throwable cause, DiagnosticCode code, Object... details) {
        super(cause, code, details);
    }

}

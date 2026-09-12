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

import io.github.qishr.cascara.lang.ct.token.CtToken;

public class CtInclude extends CtStatement {
    private CtString fileName;

    public CtInclude(CtToken token, CtString fileName) { //, CtIdentifier className, CtIdentifier methodName) {
        super(token);
        this.fileName = fileName;
    }

    public CtString getFileName() {
        return fileName;
    }
}

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

module cascara.lang.ct {
    requires cascara.common;
    requires cascara.common.io;

    exports io.github.qishr.cascara.lang.ct.ast;
    exports io.github.qishr.cascara.lang.ct.exec;
    exports io.github.qishr.cascara.lang.ct.diagnostic;
    exports io.github.qishr.cascara.lang.ct.processor;
    exports io.github.qishr.cascara.lang.ct.token;
    exports io.github.qishr.cascara.lang.ct.util;

    opens io.github.qishr.cascara.lang.ct.util;
    opens io.github.qishr.cascara.lang.ct.exec to cascara.common;
}

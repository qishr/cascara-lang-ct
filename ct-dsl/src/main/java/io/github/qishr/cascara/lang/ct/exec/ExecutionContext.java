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

package io.github.qishr.cascara.lang.ct.exec;

import java.util.List;
import java.util.Map;

import io.github.qishr.cascara.common.diagnostic.Diagnostic;
import io.github.qishr.cascara.lang.ct.ast.CtTemplate;
import io.github.qishr.cascara.lang.ct.internal.CtMap;

public class ExecutionContext {
    public CtMap globalVariables;

    public Map<String,CtMap> formats;
    public Map<String,CtTemplate> templates;

    public String outputFileName;
    public List<String> outputFormatsList;
    public List<Diagnostic> problems;
    public boolean verbose;
    public boolean debugParser;
}

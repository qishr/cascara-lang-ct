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

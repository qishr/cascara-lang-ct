package io.github.qishr.cascara.lang.ct.ast;

import java.util.ArrayList;
import java.util.List;

import io.github.qishr.cascara.lang.ct.token.CtToken;

public class CtMethodCall extends CtAssignmentValue {
    private CtIdentifier name;
    private List<CtParameter> parameters = new ArrayList<>();

    public CtMethodCall(CtToken token) {
        super(token);
    }

    public CtIdentifier getName() {
        return name;
    }

    public void setName(CtIdentifier name) {
        this.name = name;
    }

    public List<CtParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<CtParameter> parameters) {
        this.parameters = parameters;
    }
}

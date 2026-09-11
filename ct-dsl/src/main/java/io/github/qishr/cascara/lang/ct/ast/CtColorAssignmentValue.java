package io.github.qishr.cascara.lang.ct.ast;

import io.github.qishr.cascara.lang.ct.token.CtToken;

public class CtColorAssignmentValue extends CtAssignmentValue {
    private CtMethodCall method;
    private String hexValue;
    private CtName name;

    public CtColorAssignmentValue(CtToken token) {
        super(token);
    }

    public CtMethodCall getMethod() {
        return method;
    }

    public void setMethodCall(CtMethodCall method) {
        this.method = method;
    }

    public String getHexValue() {
        return hexValue;
    }

    public void setHexValue(String hex) {
        hexValue = hex;
    }

    public CtName getName() {
        return name;
    }

    public void setName(CtName name) {
        this.name = name;
    }
}

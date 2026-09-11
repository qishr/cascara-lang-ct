package io.github.qishr.cascara.lang.ct.ast;

public class CtAssignment extends CtStatement {
    private CtName name;
    private CtAssignmentValue assignmentValue;

    public CtAssignment(CtName name, CtAssignmentValue assignmentValue) {
        super(name.getToken());
        this.name = name;
        this.assignmentValue = assignmentValue;
    }

    public CtName getName() {
        return name;
    }

    public CtAssignmentValue getAssignmentValue() {
        return assignmentValue;
    }
}

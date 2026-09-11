package io.github.qishr.cascara.lang.ct.ast;

public class CtImport extends CtNode {
    private CtName packageName;
    private CtIdentifier className;
    private CtIdentifier methodName;

    public CtImport(CtName packageName) { //, CtIdentifier className, CtIdentifier methodName) {
        super(packageName.getToken());
        this.packageName = packageName;
        // this.className = className;
        // this.methodName = methodName;
    }

    public CtName getName() {
        return packageName;
    }
    public CtName getPackage() {
        return packageName;
    }
    public CtIdentifier getClassName() {
        return className;
    }
    public CtIdentifier getMethodName() {
        return methodName;
    }
}

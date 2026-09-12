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

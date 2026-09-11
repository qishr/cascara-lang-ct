package io.github.qishr.cascara.lang.ct.internal;

import io.github.qishr.cascara.common.annotation.Nullable;

public enum CtKeyword {
    IF("if"),
    IMPORT("import"),
    INCLUDE("include"),
    INSERT("insert"),
    ITERATE("iterate"),
    SEPARATOR("separator");
    private final String string;
    CtKeyword(String string) {
        this.string = string;
    }
    @Nullable
    public static CtKeyword of(String name) {
        if (name == null) return null;
        for (CtKeyword candidate : CtKeyword.values()) {
            if (name.equals(candidate.toString())) {
                return candidate;
            }
        }
        return null;
    }
    public String toString() { return string; }
}

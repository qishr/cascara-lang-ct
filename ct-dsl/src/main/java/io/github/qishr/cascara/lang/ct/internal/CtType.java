package io.github.qishr.cascara.lang.ct.internal;

import io.github.qishr.cascara.common.annotation.Nullable;

public enum CtType {
    COLOR(Category.SCALAR, "Color"),
    FORMAT(Category.FORMAT, "Format"),
    METHOD(null, null),
    NUMBER(Category.SCALAR, null), // Not able to declare it
    PALETTE(Category.COLLECTION, "Palette"),
    PROPERTIES(Category.COLLECTION, "Properties"),
    STRING(Category.SCALAR, "String"),
    TEMPLATE(Category.FORMAT, "Template");
    private final Category category;
    private final String string;
    CtType(Category category, String string) {
        this.category = category;
        this.string = string;
    }
    @Nullable
    public static CtType of(String name) {
        if (name == null) return null;
        for (CtType candidate : CtType.values()) {
            if (name.equals(candidate.toString())) {
                return candidate;
            }
        }
        return null;
    }
    public Category getCategory() { return category; }
    public String toString() { return string; }

    public static enum Category {
        SCALAR,
        COLLECTION,
        FORMAT
    }
}

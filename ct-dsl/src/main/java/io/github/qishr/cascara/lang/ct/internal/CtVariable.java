package io.github.qishr.cascara.lang.ct.internal;

import io.github.qishr.cascara.common.color.ColorUtils;
import io.github.qishr.cascara.common.color.RgbaColor;
import io.github.qishr.cascara.lang.ct.ast.CtNode;
import io.github.qishr.cascara.lang.ct.ast.CtString;
import io.github.qishr.cascara.lang.ct.token.CtToken;

public class CtVariable extends CtNode {
    private CtType type;
    private String name;
    private RgbaColor colorValue;
    private CtString stringValue;
    private Number number;

    public CtVariable(CtToken token, String name) {
        super(token);
        this.name = name;
    }

    public CtType getType() {
        return type;
    }

    public void setType(CtType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RgbaColor getColorValue() {
        return colorValue;
    }

    public void setColorValue(RgbaColor color) {
        this.colorValue = color;
        this.type = CtType.COLOR;
    }

    public CtString getStringValue() {
        return stringValue;
    }

    public void setStringValue(CtString string) {
        this.stringValue = string;
        this.type = CtType.STRING;
    }

    public Number getNumberValue() {
        return number;
    }

    public void setNumberValue(Number number) {
        this.number = number;
        this.type = CtType.NUMBER;
    }

    public String toString() {
        if (type == CtType.STRING) {
            return stringValue.asString();
        } else if (type == CtType.COLOR) {
            return ColorUtils.toRgbaHex(colorValue);
        } else if (type == CtType.NUMBER) {
            return number.toString();
        }
        return super.toString();
    }

    public String asString() {
        return toString();
    }
}

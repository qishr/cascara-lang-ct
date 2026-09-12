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

package io.github.qishr.cascara.lang.ct.util;

import io.github.qishr.cascara.common.color.ColorUtils;
import io.github.qishr.cascara.common.color.RgbaColor;
import io.github.qishr.cascara.common.color.HsbaColor;

public class ColorTransform {
    public static RgbaColor rgba(NumericParameter red, NumericParameter green, NumericParameter blue, NumericParameter alpha) {
        return new RgbaColor(
            red.asInteger(),
            green.asInteger(),
            blue.asInteger(),
            alpha.asDouble()
        );
    }

    public static RgbaColor rgb(NumericParameter red, NumericParameter green, NumericParameter blue) {
        return new RgbaColor(
            red.asInteger(),
            green.asInteger(),
            blue.asInteger(),
            1
        );
    }

    public static RgbaColor hsbaLerp(RgbaColor left, RgbaColor right, NumericParameter n) {
        HsbaColor leftColor = ColorUtils.toHsbaColor(left);
        HsbaColor rightColor = ColorUtils.toHsbaColor(right);
        HsbaColor color;
        if (n.isPercentage()) {
            color = ColorUtils.lerp(leftColor, rightColor, n.asDouble() / 100);
        } else {
            color = ColorUtils.lerp(leftColor, rightColor, n.asDouble());
        }
        return ColorUtils.toRgbaColor(color);
    }

    public static RgbaColor rgbaLerp(RgbaColor left, RgbaColor right, NumericParameter n) {
        RgbaColor color;
        if (n.isPercentage()) {
            color = ColorUtils.lerp(left, right, n.asDouble() / 100);
        } else {
            color = ColorUtils.lerp(left, right, n.asDouble());
        }
        return color;
    }

    public static RgbaColor adjustBlue (RgbaColor color, NumericParameter n) {
        RgbaColor result = color.duplicate();
        if (n.isPercentage()) {
            double blue = color.getBlue() + color.getBlue() * (n.asDouble() / 100);
            result.setBlue(blue);
        } else {
            result.setBlue(n.asInteger());
        }
        return result;
    }

    public static RgbaColor brightness(RgbaColor color, NumericParameter n) {
        HsbaColor hsbaColor = ColorUtils.toHsbaColor(color);
        if (n.isRelative()) {
            double v;
            if (n.isPercentage()) {
                v = hsbaColor.brightness * (n.asDouble() / 100);
            } else {
                v = n.asDouble();
            }
            hsbaColor.setBrightness(hsbaColor.brightness + v);
        } else {
            double v;
            if (n.isPercentage()) {
                v = n.asDouble() / 100;
            } else {
                v = n.asDouble();
            }
            hsbaColor.setBrightness(v);
        }
        return ColorUtils.toRgbaColor(hsbaColor);
    }

    public static RgbaColor saturation(RgbaColor color, NumericParameter n) {
        HsbaColor hsbaColor = ColorUtils.toHsbaColor(color);
        if (n.isRelative()) {
            double v;
            if (n.isPercentage()) {
                v = hsbaColor.saturation * (n.asDouble() / 100);
            } else {
                v = n.asDouble();
            }
            hsbaColor.setSaturation(hsbaColor.saturation + v);
        } else {
            double v;
            if (n.isPercentage()) {
                v = n.asDouble() / 100;
            } else {
                v = n.asDouble();
            }
            hsbaColor.setSaturation(v);
        }
        return ColorUtils.toRgbaColor(hsbaColor);
    }

    public static RgbaColor changeHue(RgbaColor color, NumericParameter n) {
        HsbaColor hsbaColor = ColorUtils.toHsbaColor(color);
        HsbaColor hsbaResult;

        if (n.isRelative()) {
            hsbaResult = hsbaColor.deriveColor(n.asInteger(), 0, 0, 0);
        } else {
            hsbaResult = hsbaColor.setHue(n.asInteger());
        }

        return ColorUtils.toRgbaColor(hsbaResult);
    }

    public static RgbaColor greyscale(RgbaColor color) {
        HsbaColor hsbaColor = ColorUtils.toHsbaColor(color);
        HsbaColor hsbaResult = hsbaColor.setSaturation(0);
        return ColorUtils.toRgbaColor(hsbaResult);
    }

    public static RgbaColor translucent(RgbaColor color) {
        return color.duplicate().setAlpha(0.65);
    }
}

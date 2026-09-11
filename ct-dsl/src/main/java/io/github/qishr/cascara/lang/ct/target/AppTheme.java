package io.github.qishr.cascara.lang.ct.target;

import java.io.PrintStream;
import java.util.Map;
import java.util.Map.Entry;

import io.github.qishr.cascara.common.color.ColorUtils;
import io.github.qishr.cascara.common.color.RgbaColor;

public class AppTheme {
    private static final String NL = "\n";
    private PrintStream ps;

    public AppTheme(PrintStream ps) {
        this.ps = ps;
    }

    public void setPalette(Map<String, RgbaColor> palette) {
        // System.out.println("AppTheme:");
        // for (Entry<String, ColorDefinition> entry : palette.entrySet()) {
        //     System.out.println("  " + entry.getKey() + " = \"" + entry.getValue() + "\"");
        // }
        dumpPalette(palette);
    }

    // TODO:
    //
    // {
    // "name": "Cascara Retro Green on Heavy Metal",
    // "type": "dark",
    // "semanticHighlighting": true,
    // "colors": {
    // ...
    // },
	// "tokenColors": [
	// ...
    // ]
    // }

    public void dumpPalette(Map<String, RgbaColor> palette) {
        StringBuilder sb = new StringBuilder();

        sb.append("    \"workbench.colorCustomizations\": {" + NL);

        for (Entry<String, RgbaColor> entry : palette.entrySet()) {
            String name = entry.getKey();
            String hex = ColorUtils.toRgbaHex(entry.getValue());
            sb.append("        \"" + name + "\": ");
            sb.append("\"" + hex + "\"," + NL);
        }

        sb.append("    }," + NL);

        ps.println(sb.toString());
    }
}

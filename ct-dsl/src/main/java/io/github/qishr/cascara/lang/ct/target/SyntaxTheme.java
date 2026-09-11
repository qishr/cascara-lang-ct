package io.github.qishr.cascara.lang.ct.target;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.github.qishr.cascara.common.color.ColorUtils;
import io.github.qishr.cascara.common.color.RgbaColor;

public class SyntaxTheme {
    private static final String NL = "\n";
    private PrintStream ps;

    public SyntaxTheme(PrintStream ps) {
        this.ps = ps;
    }

    public void setPalette(Map<String, RgbaColor> palette) {
        dumpPalette(palette);
    }

    public void dumpPalette(Map<String, RgbaColor> palette) {
        Map<String,List<String>> scopesByTokenType = scopes();
        StringBuilder sb = new StringBuilder();


        // sb.append("        \"tokenColors\": [" + NL);

        sb.append("    \"editor.tokenColorCustomizations\": {" + NL);
        sb.append("        \"textMateRules\": [" + NL);


        for (Entry<String, RgbaColor> entry : palette.entrySet()) {
            sb.append("            {" + NL);
            String name = entry.getKey();
            String hex = ColorUtils.toRgbaHex(entry.getValue());
            String fontStyle = name.equals("annotation") || name.equals("comment")
                ? "italic"
                : "";
            List<String> scopes = scopesByTokenType.get(name);
            if (scopes == null) {
                System.err.println("No scope info for " + name);
            }
            sb.append("                \"name\": \"" + name + "\"," + NL);
            sb.append("                \"settings\": {" + NL);
            if (!fontStyle.isEmpty()) {
                sb.append("                    \"fontStyle\": \"" + fontStyle + "\"," + NL);
            }
            sb.append("                    \"foreground\": \"" + hex + "\"" + NL);
            sb.append("                }," + NL);
            sb.append("                \"scope\": [" + NL);
            for (String scope : scopes) {
                sb.append("                    \"" + scope + "\"," + NL);
            }
            sb.append("                ]," + NL);
            sb.append("            }," + NL);
        }


        // sb.append("        ]," + NL);

        sb.append("        ]" + NL);
        sb.append("    }," + NL);


        ps.println(sb.toString());
    }

    private Map<String,List<String>> scopes() {
        Map<String,List<String>> scopes = new HashMap<>();
        scopes.put("annotation", List.of(
            "storage.type.annotation.java",
            "meta.annotation.java",
            "decorator"
        ));
        scopes.put("character", List.of(
            "constant.character.java"
        ));
        scopes.put("comment", List.of(
            "comment.java",
            "comment.groovy",
            "comment",
            "punctuation.definition.comment"
        ));
        scopes.put("constant", List.of(
            "constant.language.java",
            "enumMember",
            "variable.other.constant",
            "variable.other.enummember"
        ));
        scopes.put("identifier", List.of(
            "variable.other.java"
        ));
        scopes.put("keyword", List.of(
            "keyword.java",
            "keyword.groovy",
            "keyword",
            "variable.language.this.java",
            "storage.modifier.implements.java",
            "meta.class.identifier.java"
        ));
        scopes.put("modifier", List.of(
            "storage.modifier"
        ));
        scopes.put("method", List.of(
            "entity.name.function",
            "entity.name.function.java"
        ));
        scopes.put("namespace", List.of(
            "entity.name.namespace",
            "entity.name.scope-resolution",
            "namespace",
            "meta.package.java",
            "storage.modifier.package.java",
            "storage.modifier.import.java",
            "meta.import.java"
        ));
        scopes.put("number", List.of(
            "number",
            "constant.numeric",
            "constant.language",
            "support.constant",
            "constant.character",
            "constant.escape",
            "keyword.other.unit",
            "keyword.other"
        ));
        scopes.put("operator", List.of(
            "constant.other.color",
            "constant.other.symbol",
            "constant.other.key",
            "punctuation",
            "meta.tag",
            "punctuation.definition.tag",
            "punctuation.separator.inheritance.php",
            "punctuation.definition.tag.html",
            "punctuation.definition.tag.begin.html",
            "punctuation.definition.tag.end.html",
            "punctuation.section.embedded",
            "keyword.other.template",
            "keyword.other.substitution"
        ));
        scopes.put("other", List.of(
            "storage.type.primitive",
            "storage.type.primitive.java"
        ));
        scopes.put("primitive.type", List.of(
            "storage.type.primitive",
            "storage.type.primitive.java"
        ));
        scopes.put("reference.type", List.of(
            "storage.type.java",
            "entity.other.inherited-class.java",
            "entity.name.type.class.java",
            "storage.type.generic.java",
            "entity.name.type",
            "entity.name.type.class",
            "storage.type.java"
        ));
        scopes.put("regex", List.of(
            "string.regexp.java",
            "string.regexp"
        ));
        scopes.put("string", List.of(
            "string",
            "string.java",
            "string.groovy"
        ));
        scopes.put("variable", List.of(
            "variable.java",
            "variable.parameter",
            "variable.parameter.java",
            "variable.parameter.groovy",
            "entity.name.class.java",
            "entity.name.class.groovy",

            "class",
            "interface",
            "enum",
            "struct",
            "typeParameter",
            "type",

            "variable.other.property",
            "variable.other.readwrite",
            "entity.name.variable",
            "variable.other.definition.java",

            "parameter",
            "variable",
            "property",

            "meta.function-call.java"
        ));
        scopes.put("yaml.name", List.of(
            "support.type.property-name.json.comments",
            "support.type.property-name.json",
            "entity.name.tag.yaml",

            "variable.yaml",
            "keyword.other.yaml"
        ));
        scopes.put("yaml.value", List.of(
            "string.quoted.double.json.comments",
            "string.quoted.double.json",

            "string.json",
            "constant.json",
            "variable.json"
        ));
        scopes.put("yaml.punctuation", List.of(
            "punctuation.definition.json",
            "meta.brace.json"
        ));
        return scopes;
    }
}

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

package io.github.qishr.cascara.lang.ct.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import io.github.qishr.cascara.common.data.TextualTable;
import io.github.qishr.cascara.common.util.StringUtils;
import io.github.qishr.cascara.common.util.TermUtils;
import io.github.qishr.cascara.lang.ct.token.CtErrorToken;
import io.github.qishr.cascara.lang.ct.token.CtToken;
import io.github.qishr.cascara.lang.ct.token.CtTokenType;

public class DebugUtils {
    public static void dumpTokens(Writer writer, List<CtToken> tokens, int maxColumnWidth) {
        try {
			writer.write("\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // System.out.println("------------TOKENS-----------");
        TextualTable table = new TextualTable()
            .setStyle(TextualTable.Style.ROUNDED)
            .setMaxColumnWidth(maxColumnWidth)
            .setBorderColor(TermUtils.ANSI_WHITE)
            .addColumn("#")
            .addColumn("Token")
            .addColumn("Location")
            .addColumn("Lexeme")
            .addColumn("Content");

        int tokenIdent = 0;

        for (int i = 0; i < tokens.size(); i++) {
            CtToken t = tokens.get(i);
            switch(t.getType()) {
                case ERROR:
                    CtErrorToken et = (CtErrorToken)t;
                    table.addRow(
                        String.format("%2d", i),
                        t.getType().toString(),
                        String.format("L:%-3d C:%-3d", t.getStartLine(), t.getStartColumn()),
                        "", et.getCode().getMessage()
                    );
                    break;
                case KEYWORD, IDENTIFIER, DECLARATION, STRING, COMMENT:
                    table.addRow(
                        String.format("%2d", i),
                        "  ".repeat(tokenIdent) + t.getType().toString(),
                        String.format("L:%-3d C:%-3d", t.getStartLine(), t.getStartColumn()),
                        StringUtils.debugString(t.getLexeme()),
                        StringUtils.debugString(t.getContent())
                    );
                    break;
                default:
                    if (t.getType() == CtTokenType.CLOSE_TEMPLATE && tokenIdent > 0) {
                        tokenIdent--;
                    }
                    table.addRow(
                        String.format("%2d", i),
                        "  ".repeat(tokenIdent) + t.getType().toString(),
                        String.format("L:%-3d C:%-3d", t.getStartLine(), t.getStartColumn()), "", ""
                    );
                    if (t.getType() == CtTokenType.OPEN_TEMPLATE) {
                        tokenIdent++;
                    }
            }
        }

        try {
            table.render(writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

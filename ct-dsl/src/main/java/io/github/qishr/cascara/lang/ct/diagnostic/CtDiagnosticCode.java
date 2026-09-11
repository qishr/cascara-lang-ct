package io.github.qishr.cascara.lang.ct.diagnostic;

import io.github.qishr.cascara.common.diagnostic.code.DiagnosticCode;

public enum CtDiagnosticCode implements DiagnosticCode {

    ERROR("CTDSL-101", "{0}"),
    WARN("CTDSL-102", "{0}"),
    INFO("CTDSL-102", "{0}"),

    UNEXPECTED_CHAR("CTDSL-111", "Unexpected character {0}"),
    UNEXPECTED_TOKEN("CTDSL-112", "Unexpected token {0}"),
    UNKNOWN("CTDSL-113", "Unknown {0}"),
    EXPECTED("CTDSL-114", "Expected {0} but was {1}"),

    UNEXPECTED_NODE("CTDSL-201", "Unexpected node {0}"),

    UNDEFINED_VARIABLE("CTDSL-201", "Variable '{0}' is undefined"),
    UNDEFINED_FORMAT("CTDSL-202", "Format '{0}' is undefined"),
    UNDEFINED_COLOR("CTDSL-203", "Color '{0}' is undefined"),
    UNDEFINED_PROPERTY("CTDSL-204", "Property '{0}' is undefined"),
    UNDEFINED_TEMPLATE("CTDSL-205", "Template '{0}' is undefined"),
    NESTED_COLLECTION("CTDSL-206", "Collections must not be nested"),
    INVALID_ESCAPE("CTDSL-207", "Invalid escape character '{0}'"),
    UNDECLARED_TEMPLATE("CTDSL-208", "No template declated in format '{0}'");

    private final String code;
    private final String message;

    CtDiagnosticCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override public String getCode() { return code; }
    @Override public String getMessage() { return message; }
}
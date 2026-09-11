package io.github.qishr.cascara.lang.ct.exec;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.qishr.cascara.common.color.ColorUtils;
import io.github.qishr.cascara.common.color.RgbaColor;
import io.github.qishr.cascara.common.diagnostic.Diagnostic.Level;
import io.github.qishr.cascara.common.diagnostic.LocalizableIOException;
import io.github.qishr.cascara.common.diagnostic.NoOpReporter;
import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.diagnostic.SilentCollectingReporter;
import io.github.qishr.cascara.common.diagnostic.StandardReporter;
import io.github.qishr.cascara.common.diagnostic.code.DiagnosticCode;
import io.github.qishr.cascara.common.diagnostic.code.GenericDiagnosticCode;
import io.github.qishr.cascara.common.lang.diagnostic.LangDiagnosticCode;
import io.github.qishr.cascara.lang.ct.ast.CtAssignment;
import io.github.qishr.cascara.lang.ct.ast.CtAssignmentValue;
import io.github.qishr.cascara.lang.ct.ast.CtDeclaration;
import io.github.qishr.cascara.lang.ct.ast.CtDocument;
import io.github.qishr.cascara.lang.ct.ast.CtIdentifier;
import io.github.qishr.cascara.lang.ct.ast.CtIfStatement;
import io.github.qishr.cascara.lang.ct.ast.CtImport;
import io.github.qishr.cascara.lang.ct.ast.CtInclude;
import io.github.qishr.cascara.lang.ct.ast.CtInsertStatement;
import io.github.qishr.cascara.lang.ct.ast.CtIterateStatement;
import io.github.qishr.cascara.lang.ct.ast.CtMethodCall;
import io.github.qishr.cascara.lang.ct.ast.CtName;
import io.github.qishr.cascara.lang.ct.ast.CtNode;
import io.github.qishr.cascara.lang.ct.ast.CtPaletteAssignmentValue;
import io.github.qishr.cascara.lang.ct.ast.CtParameter;
import io.github.qishr.cascara.lang.ct.ast.CtSeparator;
import io.github.qishr.cascara.lang.ct.ast.CtSingleNameStatement;
import io.github.qishr.cascara.lang.ct.ast.CtStatement;
import io.github.qishr.cascara.lang.ct.ast.CtString;
import io.github.qishr.cascara.lang.ct.ast.CtTemplate;
import io.github.qishr.cascara.lang.ct.diagnostic.CtDiagnosticCode;
import io.github.qishr.cascara.lang.ct.internal.CtMap;
import io.github.qishr.cascara.lang.ct.internal.CtType;
import io.github.qishr.cascara.lang.ct.internal.CtVariable;
import io.github.qishr.cascara.lang.ct.internal.CtType.Category;
import io.github.qishr.cascara.lang.ct.processor.CtAstParser;
import io.github.qishr.cascara.lang.ct.util.NumericParameter;

public class CtExecutionEngine {
    private CtDocument doc;
    private Reporter reporter = new StandardReporter();

    private URI uri;
    private boolean isEntryPoint;
    private ExecutionContext context;
    private Map<String, Method> jvmMethods = new HashMap<>();
    private PrintStream outputStream;

    private CtTemplate parentTemplate;

    public CtExecutionEngine(
        ExecutionContext context,
        URI uri,
        boolean isEntryPoint,
        Reporter reporter,
        PrintStream outputStream,
        CtTemplate parentTemplate
    ) {
        this.context = context;
        this.uri = uri;
        this.isEntryPoint = isEntryPoint;
        this.reporter = reporter;
        this.outputStream = outputStream;
        this.parentTemplate = parentTemplate;
    }

    public CtExecutionEngine setReporter(Reporter reporter) {
        this.reporter = reporter == null ? new NoOpReporter() : reporter;
        return this;
    }

    public Integer execute(CtDocument doc) {
        this.doc = doc;
        resolveImports();
        int status = processStatements(null, doc.getStatements(), parentTemplate);
        if (isEntryPoint) {
            generateOutput();
        }
        return status;
    }

    private void generateOutput() {
        for (String outputFormat : context.outputFormatsList) {
            CtMap format = context.formats.get(outputFormat);
            if (format == null) {
                error(CtDiagnosticCode.UNDEFINED_FORMAT, outputFormat);
                continue;
            }
            CtVariable templateVar = format.get("template");
            if (templateVar == null) {
                error(format, CtDiagnosticCode.UNDECLARED_TEMPLATE, outputFormat);
                continue;
            }
            String templateName = templateVar.asString();
            CtTemplate template = context.templates.get(templateName);
            if (template == null) {
                error(templateVar, CtDiagnosticCode.UNDEFINED_TEMPLATE, templateVar);
                continue;
            }

            Path outputFilename = outputPathForFormat(format);
            if (outputFilename == null) {
                outputStream = System.out;
                String output = processTemplate(template);
                System.out.println(output);
            } else {
                if (context.verbose) {
                    reporter.info(GenericDiagnosticCode.INFO, "Modified output path: " + outputFilename);
                }
                try {
                    outputStream = new PrintStream(outputFilename.toString());
                    String output = processTemplate(template);
                    outputStream.println(output);
                    outputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Path outputPathForFormat(CtMap format) {
        if (context.outputFileName == null || context.outputFileName.isBlank()) {
            return null;
        }
        Path outputPath = Path.of(context.outputFileName);
        Path fname = outputPath.getFileName();
        String fnameStr = fname.toString();
        int dot = fnameStr.indexOf(".");
        if (dot == -1) {
            CtVariable suffixVar = format.get("suffix");
            if (suffixVar != null) {
                String suffix = suffixVar.asString();
                outputPath = outputPath.getParent().resolve(fnameStr + suffix);
            }
        }
        return outputPath;
    }

    private Integer processStatements(CtMap assignmentTarget, List<CtStatement> statements, CtTemplate parentTemplate) {
        for (CtStatement s : statements) {

            if (context.verbose) {
                debug("Line " + s.getStartLine());
            }

            CtName name = null;
            CtAssignmentValue value = null;
            boolean isFormatDeclaration = false;

            if (s instanceof CtInclude inc) {
                processInclude(inc);
            } else if (s instanceof CtDeclaration declaration) {
                name = declaration.getIdentifier();
                value = declaration.getAssignment();
                isFormatDeclaration = processDeclaration(assignmentTarget, declaration);
            } else if (s instanceof CtAssignment assignment) {
                name = assignment.getName();
                value = assignment.getAssignmentValue();
            } else if (s instanceof CtInsertStatement insertStatement) {
                processInsertStatement(insertStatement);
            } else if (s instanceof CtIterateStatement iterateStatement) {
                processIterateStatement(iterateStatement, parentTemplate);
            } else if (s instanceof CtIfStatement ifStatement) {
                processIfStatement(ifStatement);
            } else if (s instanceof CtSingleNameStatement nameStatement) {
                processNameStatement(nameStatement);
            } else {
                error(s, CtDiagnosticCode.ERROR, "Unexpected statement: " + s);
                return 1;
            }
            if (value != null) {
                processAssignment(assignmentTarget, name, value, isFormatDeclaration);
            }
        }
        return 0;
    }

    private void processNameStatement(CtSingleNameStatement nameStatement) {
        CtName name = nameStatement.getName();
        String paletteId = "";

        if (parentTemplate != null) {
            CtIdentifier id = parentTemplate.getIdentifier();
            if (id == null) {
                debug("Debug");
            } else {
                paletteId = id.asString();
                name = new CtName(name.getToken(), paletteId + "." + name.asString());
            }
        }
        RgbaColor color = getColor(nameStatement, null, name.asString(), false);
        if (color == null) {
            error(nameStatement, CtDiagnosticCode.UNDEFINED_COLOR, name.asString());
        } else {
            String hex = ColorUtils.toRgbHex(color);
            outputStream.print(hex);
        }
    }

    private void processInsertStatement(CtInsertStatement insertStatement) {
        CtSeparator separatorNode = insertStatement.getSeparator();
        String separatorString = processEscapes(insertStatement, getSeparatorString(separatorNode));

        int n = 0;
        for (CtIdentifier templateId : insertStatement.getIdentifiers()) {
            CtTemplate template = context.templates.get(templateId.asString());
            if (template == null) {
                error(insertStatement, CtDiagnosticCode.ERROR, "Unknown template: " + templateId);
                continue;
            }
            if (n > 0) {
                outputStream.print(separatorString);
            }
            String output = processTemplate(template, null, null, null);
            outputStream.print(output);
            n++;
        }
    }

    private void processIterateStatement(CtIterateStatement iterateStatement, CtTemplate parentTemplate) {
        CtSeparator separatorNode = iterateStatement.getSeparator();
        String separatorString = processEscapes(iterateStatement, getSeparatorString(separatorNode));

        CtIdentifier parentTemplateId = parentTemplate.getIdentifier();
        if (parentTemplateId == null) {
            error(iterateStatement, CtDiagnosticCode.ERROR, "Missing template ID: " + parentTemplate);
            return;
        }

        String paletteId = parentTemplateId.asString();
        CtMap palette = getPalette(iterateStatement, paletteId, false);
        if (palette == null) {
            warn(iterateStatement, CtDiagnosticCode.ERROR, "Unknown palette: " + paletteId);
            return;
        }

        int n = 0;
        for (CtVariable variable : palette) {
            String name = variable.getName();
            if  (variable.getType() == CtType.COLOR) {
                String value = ColorUtils.toRgbaHex(variable.getColorValue());

                String templateId = processVariables(iterateStatement, iterateStatement.getIdentifier().asString(), name, value);
                CtTemplate template = context.templates.get(templateId);
                if (template == null) {
                    warn(iterateStatement, CtDiagnosticCode.ERROR, "Unknown template: " + templateId);
                    continue;
                }

                String output = processTemplate(template, name, value, parentTemplate);

                if (n > 0) {
                    outputStream.print(separatorString);
                }
                outputStream.print(output);
                n++;
            }
        }
    }

    private void processIfStatement(CtIfStatement ifStatement) {
        error(ifStatement, CtDiagnosticCode.ERROR, "If statement not implemented");
    }

    private String getSeparatorString(CtSeparator separatorNode) {
        if (separatorNode != null) {
            if (separatorNode.getIdentifier() != null) {
                CtTemplate separatorTemplate = context.templates.get(separatorNode.getIdentifier().asString());
                if (separatorTemplate == null) {
                    error(separatorNode, CtDiagnosticCode.ERROR, "Unknown template: " + separatorNode.getIdentifier());
                    return "";
                }
                return processTemplate(separatorTemplate, null, null, null);
            } else if (separatorNode.getString() != null) {
                return separatorNode.getString().asString();
            } else {
                error(separatorNode, CtDiagnosticCode.ERROR, "No separator");
                return "";
            }
        }
        return "";
    }

    private boolean processDeclaration(CtMap variableContext, CtDeclaration declaration) {
        if (variableContext == null) {
            variableContext = context.globalVariables;
        }

        CtType type = declaration.getCtType();
        CtIdentifier identifier = declaration.getIdentifier();

        String name = identifier.asString();
        CtVariable variable = variableContext.get(name);
        if (variable != null) {
            error(declaration, CtDiagnosticCode.ERROR, "Variable redeclared: " + name);
        }
        if (type.getCategory() == Category.COLLECTION && variableContext != context.globalVariables) {
            error(declaration, CtDiagnosticCode.NESTED_COLLECTION);
        }

        if (type == CtType.FORMAT) {
            CtMap format = new CtMap(declaration.getToken(), name);
            format.setType(CtType.FORMAT);
            context.formats.put(name, format);
            return true;
        } else if (type == CtType.TEMPLATE) {
            CtTemplate template = (CtTemplate)declaration.getAssignment();
            template.setIdentifier(identifier);
            template.setUri(uri);
            if (template.getIdentifier() == null) {
                error(declaration, CtDiagnosticCode.ERROR, "Template has no identifier");
                return false;
            }
            context.templates.put(name, template);
            return false;
        }
        else if (type == CtType.PALETTE) {
            variable = new CtMap(declaration.getToken(), name);
            variable.setType(CtType.PALETTE);
        }
        else if (type== CtType.COLOR) {
            variable = new CtVariable(declaration.getToken(), name);
            RgbaColor color = new RgbaColor(0, 0, 0, 0);
            variable.setColorValue(color);
        }
        else if (type == CtType.STRING) {
            variable = new CtVariable(declaration.getToken(), name);
            variable.setType(CtType.STRING);
        }
        else if (type == CtType.PROPERTIES) {
            variable = new CtMap(declaration.getToken(), name);
            variable.setType(CtType.PROPERTIES);
        }
        else {
            error(declaration, CtDiagnosticCode.ERROR, "Failed to create variable: " + name);
        }

        if (variable != null) {
            variableContext.set(name, variable);
        }
        return false;
    }

    private void processAssignment(CtMap parentCollection, CtName name, CtAssignmentValue valueSource, boolean isFormatDeclaration) {
        CtVariable target = getVariable(valueSource, null, name.asString(), false);

        if (valueSource instanceof CtPaletteAssignmentValue paletteAssignmentSource) {
            if (isFormatDeclaration) {
                String formatName = name.asString();
                CtMap format = new CtMap(valueSource.getToken(), formatName);
                processPaletteAssignmentFromValue(format, paletteAssignmentSource);
                context.formats.put(formatName, format);
            } else {
                CtMap to = getPalette(name, name.asString(), true);
                if (paletteAssignmentSource.getMethod() != null) {
                    CtMethodCall methodCall = paletteAssignmentSource.getMethod();
                    processPaletteAssignmentFromMethodCall(to, methodCall);
                } else {
                    processPaletteAssignmentFromValue(to, paletteAssignmentSource);
                }
            }
        }
        else if (valueSource instanceof CtMethodCall methodCall) {
            // TODO: Do this based on category (eg scalar, collection) instead of type
            CtType targetType;
            if (target == null) {
                targetType = CtType.COLOR; // Assume it's a color if it's undeclared
            } else {
                targetType = target.getType();
            }

            if (targetType == CtType.PALETTE) {
                CtMap to = getPalette(name, name.asString(), true);
                processPaletteAssignmentFromMethodCall(to, methodCall);
            }
            else if (targetType == CtType.COLOR) {
                processColorAssignmentFromMethodCall(parentCollection, name, methodCall);
            }
            else {
                // ERROR
            }
        }
        else if (valueSource instanceof CtTemplate) {
            // We don't process templates here
            // debug("CtTemplate");
        }
        else if (valueSource.getName() != null) {
            processColorAssignmentFromColor(parentCollection, name, valueSource.getName());
        }
        else if (valueSource.getHexValue() != null) {
            processColorAssignmentFromHex(parentCollection, name, valueSource.getHexValue());
        }
        else if (valueSource.getString() != null) {
            CtVariable assignee = getVariable(name, parentCollection, name.asString(), true);
            assignee.setStringValue(valueSource.getString());
        }
        else {
            error(name, CtDiagnosticCode.ERROR, "Unable to process assignment value");
        }
    }

    private void processColorAssignmentFromHex(CtMap parentCollection, CtName toName, String hex) {
        RgbaColor to = getColor(toName, parentCollection, toName.asString(), true);
        to.setHexColor(hex);
    }

    private void processColorAssignmentFromColor(CtMap parentCollection, CtName toName, CtName fromName) {
        RgbaColor from = getColor(toName, null, fromName.asString(), false);
        RgbaColor to = getColor(toName, parentCollection, toName.asString(), true);
        copyRgbaColor(from, to);
    }

    private void processColorAssignmentFromMethodCall(CtMap parentCollection, CtName toName, CtMethodCall methodCall) {
        RgbaColor from = processColorMethodCall(methodCall);
        if (from == null) return;
        RgbaColor to = getColor(toName, parentCollection, toName.asString(),  true);
        if (!(to instanceof RgbaColor)) {
            error(methodCall, CtDiagnosticCode.ERROR, "Wrong return type: " +
                (to == null
                    ? "null"
                    : to.getClass().getSimpleName())
            );
        }
        copyRgbaColor(from, to);
    }

    private void processPaletteAssignmentFromValue(CtMap parentCollection, CtPaletteAssignmentValue paletteAssignment) {
        List<CtStatement> statements = paletteAssignment.getStatements();
        processStatements(parentCollection, statements, null);
    }

    private void processPaletteAssignmentFromMethodCall(CtMap to, CtMethodCall methodCall) {
        CtMap from = processPaletteMethodCall(methodCall);
        copyPalette(from, to);
    }

    private RgbaColor processColorMethodCall(CtMethodCall methodCall) {
        List<CtParameter> parameters = methodCall.getParameters();
        Object[] paramArray = new Object[parameters.size()];
        int n = 0;
        for (CtParameter paramNode : parameters) {
            if (paramNode.isNumber()) {
                paramArray[n] = new NumericParameter(
                    paramNode.getNumber(),
                    paramNode.isRelative(),
                    paramNode.isPercentage()
                );
            } else {
                paramArray[n] = getColor(methodCall, null, paramNode.getName(), false);
            }
            n++;
        }

        Object result = executeJvmMethod(methodCall, paramArray);
        if (result instanceof RgbaColor definition) {
            if (definition.green > 1 || definition.red > 1 || definition.blue > 1) {
                error(methodCall, GenericDiagnosticCode.ERROR, "Invalid color");
            }
            return definition;
        }
        return null;
    }

    private Object executeJvmMethod(CtMethodCall methodCall, Object[] paramArray) {
        CtName methodName = methodCall.getName();
        Method jvmMethod = jvmMethods.get(methodName.asString());
        if (jvmMethod == null) {
            error(methodCall, CtDiagnosticCode.ERROR, "Unknown method: " + methodName);
            return null;
        }
        return executeJvmMethod(null, jvmMethod, paramArray, methodCall);
    }

    private Object executeJvmMethod(Object jvmInstance, Method jvmMethod, Object[] paramArray, CtNode callSite) {
        Object result = null;
        try {
            result = jvmMethod.invoke(jvmInstance, paramArray);
        } catch (IllegalAccessException e) {
            error(callSite, e, LangDiagnosticCode.FIELD_NOT_ACCESSIBLE, jvmMethod.getName());
            return null;
        } catch (InvocationTargetException e) {
            error(callSite, e, LangDiagnosticCode.INVOCATION_TARGET_EXCEPTION, jvmMethod.getName());
            return null;
        } catch (IllegalArgumentException e) {
            error(callSite, e, LangDiagnosticCode.ILLEGAL_ARGUMENT_EXCEPTION, jvmMethod.getName());
            return null;
        }
        return result;
    }

    private CtMap processPaletteMethodCall(CtMethodCall methodCall) {
        CtParameter paletteParam = methodCall.getParameters().getFirst();
        String inputPaletteName = paletteParam.getName();
        CtMap inputPalette = getPalette(methodCall, inputPaletteName, false);


        CtMap to = new CtMap(methodCall.getToken(), null);


        for (CtVariable sourceVariable : inputPalette) {
            // String colorName = entry.getKey();
            String name = sourceVariable.getName();
            List<CtParameter> colorParameters = new ArrayList<>();
            for (int i = 0; i < methodCall.getParameters().size(); i++) {
                if (i == 0) {


                    String nameString = inputPaletteName + "." + name;
                    // CtName name = new CtName(null, nameString);
                    CtParameter colorParameter = new CtParameter(null, nameString);


                    colorParameters.add(colorParameter);
                } else {
                    colorParameters.add(methodCall.getParameters().get(i));
                }
            }
            CtMethodCall colorMethodCall = new CtMethodCall(null);
            colorMethodCall.setName(methodCall.getName());
            colorMethodCall.setParameters(colorParameters);
            RgbaColor outputColor = processColorMethodCall(colorMethodCall);


            CtVariable variable = new CtVariable(methodCall.getToken(), name);
            variable.setColorValue(outputColor);


            to.set(name, variable);
        }
        return to;
    }

    private String processTemplate(CtTemplate template) {
        return processTemplate(template, null, null, null);
    }

    private String processTemplate(CtTemplate template, String name, String value, CtTemplate parentTemplate) {
        if (template.isAllOnOneLine()) {
            return processTemplateCode(template, name, value, parentTemplate);
        }
        StringBuilder sb = new StringBuilder();
        for (CtNode node : template.getContent()) {
            if (node instanceof CtString string) {
                String templateString = string.getValue();
                if (!templateString.isBlank()) {

                    // This isn't entirely right - it still strips
                    // some explicit separator newlines
                    //
                    // If it has both leading and trailing newline, remove one
                    if (templateString.startsWith("\n")) {
                        templateString = templateString.stripTrailing();
                    }

                    // TODO: Need to be more exact than this locationMarker. Take
                    // position within string into account in both of the following calls
                    String output = processVariables(template, templateString, name, value);
                    output = processEscapes(template, output);

                    sb.append(output);
                }
            } else if (node instanceof CtTemplate nestedTemplate) {
                nestedTemplate.setUri(template.getUri());
                String output = processTemplate(nestedTemplate, name, value, template);
                sb.append(output);
            } else {
                error(node, CtDiagnosticCode.UNEXPECTED_NODE, node);
                return "";
            }
        }
        return sb.toString();
    }

    private String processEscapes(CtNode locationMarker, String string) {
        if (-1 == string.indexOf("\\")) {
            return string;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            char next = i + 1 < string.length() ? string.charAt(i + 1) : '\0';
            if (c == '\\') {
                if (next == '\\') {
                    sb.append("\\");
                } else {
                    i++;
                    if (next == 'n') {
                        sb.append("\n");
                    } else {
                        sb.append(c);
                        warn(locationMarker, CtDiagnosticCode.INVALID_ESCAPE, next);
                    }
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private String processVariables(CtNode locationMarker, String string, String name, String value) {
        if (-1 == string.indexOf("%")) {
            return string;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            char next = i + 1 < string.length() ? string.charAt(i + 1) : '\0';
            if (c == '%') {
                if (next == '%') {
                    sb.append('%');
                } else if (i + 2 < string.length()) {
                    String sub = string.substring(i + 1);
                    int p = sub.indexOf('%');
                    if (p > -1) {
                        String variableName = sub.substring(0, p);

                        if (variableName.equals("name")) {
                            sb.append(name);
                        } else if (variableName.equals("value")) {
                            sb.append(value);
                        } else {
                            CtVariable variable = getVariable(locationMarker, null, variableName, false);
                            if (variable != null) {
                                sb.append(variable.toString());
                            } else {
                                variable = getVariable(locationMarker, null, variableName, false);
                                warn(locationMarker, CtDiagnosticCode.UNDEFINED_VARIABLE, variableName);
                            }
                        }

                        i+=p+1;
                        continue;
                    }
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private String processTemplateCode(CtTemplate template, String name, String value, CtTemplate parentTemplate) {
        if (template.getContent().isEmpty()) {
            warn(CtDiagnosticCode.WARN, "Nested template has no code");
            return "";
        }

        String code = template.getContent().getFirst().asString().trim();

        // If it has no trailing semicolon, add one to keep the parser happy
        if (!code.endsWith(";")) {
            code = code + ";";
        }

        debug("processTemplate: " + code);

        // Set up a new reporter to collect errors from the template
        // and set their location to the correct line number
        SilentCollectingReporter reporter = new SilentCollectingReporter()
            .setAnsiColoringEnabled(true)
            .setDiagnosticConsumer(p -> {
                if (p.getLevel().isProblem()) {
                    p.setUri(template.getUri());
                    p.setLine(p.getLine() + template.getStartLine() - 1);
                    context.problems.add(p);
                } else if (context.verbose) {
                    System.out.println(CtRunner.formatMessage(p));
                }
            });

        if (context.verbose) {
            reporter.setLevel(Level.TRACE)
                    .setStackTraceEnabled(true);
        }

        CtAstParser parser = new CtAstParser()
            .setReporter(reporter);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteArrayOutputStream);

        reporter.debug("Parsing template code");
        CtNode root = parser.parse(code);
        if (root instanceof CtDocument nestedDoc) {
            CtExecutionEngine engine = new CtExecutionEngine(context, template.getUri(), false, reporter, printStream, parentTemplate);
            engine.execute(nestedDoc);
        }

        String output = byteArrayOutputStream.toString();
        printStream.close();

        return output;
    }

    private void processInclude(CtInclude inc) {
        URI includeUri = uri.resolve(inc.getFileName().getValue());
        CtRunner runner = new CtRunner();
        try {
			runner.run(
                context,
                includeUri,
                false,
                outputStream,
                null
            );
		} catch (LocalizableIOException e) {
            error(inc, CtDiagnosticCode.ERROR, "Unable to include file: " + e.getMessage());
		}
    }

    //
    // Helpers
    //

    private CtVariable getVariable(CtNode locationMarker, CtMap variableContext, String name, boolean createIfMissing) {
        if (name == null) {
            error(locationMarker, GenericDiagnosticCode.UNEXPECTED_NULL_PARAMETER, "name", "getVariable");
        }
        String identifier = null;
        CtMap map = variableContext == null ? context.globalVariables : variableContext;;
        CtVariable current = null;
        if (name.contains(".") && variableContext == null) {
            String[] parts = name.split("\\.", -1);
            int i = 0;
            while (i < parts.length) {
                current = map.get(parts[i]);
                if (current instanceof CtMap currentMap) {
                    map = currentMap;
                    i++;
                } else {
                    identifier = parts[i];
                    break;
                }
            }
            if (i == parts.length - 1) {
                // The whole name has been resolved
            } else if (i == parts.length) {
                // A map inside a map - currently unsupported
            } else {
                // the last part possibly refers to a property of a color or similar
                if (current != null) {

                    String propertyName = parts[i+1];
                    CtVariable property = getVariableProperty(current, propertyName);
                    if (property == null) {
                        error(locationMarker, CtDiagnosticCode.UNDEFINED_PROPERTY, propertyName);
                    }
                    return property;

                }
            }
        } else {
            current = map.get(name);
        }
        if (current == null) {
            if (createIfMissing && map != null) {
                if (identifier == null) {
                    identifier = name;
                }
                current = new CtVariable(locationMarker.getToken(), identifier);
                map.set(identifier, current);
            } else {
                if (createIfMissing) {
                    debug("Debug");
                }
                // DO NOT report an error here.
                // If this was called from a template, the template reports a warning.
                return null;
            }
        }
        return current;
    }

    private CtVariable getVariableProperty(CtVariable variable, String propertyName) {
        CtVariable property = new CtVariable(variable.getToken(), propertyName);
        if (variable.getType() == CtType.COLOR) {
            RgbaColor color = variable.getColorValue();
            if (propertyName.equals("red")) {
                property.setNumberValue(color.red);
                return property;
            }
            else if (propertyName.equals("green")) {
                property.setNumberValue(color.green);
                return property;
            }
            else if (propertyName.equals("blue")) {
                property.setNumberValue(color.blue);
                return property;
            }
            else if (propertyName.equals("alpha")) {
                property.setNumberValue(color.alpha);
                return property;
            }
            else {
                error(variable, CtDiagnosticCode.ERROR, "Property " + propertyName + " does not exist for type Color");
                return null;
            }
        }
        error(variable, CtDiagnosticCode.ERROR, "Property " + propertyName + " does not exist for variable " + variable.getName());
        return null;
    }

    private RgbaColor getColor(CtNode locationMarker, CtMap variableContext, String name, boolean createIfMissing) {
        CtVariable variable = getVariable(locationMarker, variableContext, name, createIfMissing);
        RgbaColor color = null;
        if (variable == null) {
            if (createIfMissing) {
                variable = new CtVariable(locationMarker.getToken(), name);
                if (variableContext == null) {
                    variableContext = context.globalVariables;
                }
                variableContext.set(name, variable);
            } else {
                error(locationMarker, CtDiagnosticCode.UNDEFINED_COLOR, name);
                return null;
            }
        } else if (variable.getType() != null && variable.getType() != CtType.COLOR) {
            error(locationMarker, CtDiagnosticCode.ERROR, "Variable " + name + " is not a color");
            return null;
        }

        color = variable.getColorValue();
        if (color == null) {
            color = new RgbaColor(0,0,0,1);
            variable.setColorValue(color);
        }
        return color;
    }

    private CtMap getPalette(CtNode locationMarker, String name, boolean createIfMissing) {
        CtVariable variable = getVariable(locationMarker, null, name, false);
        if (variable == null) {
            if (createIfMissing) {
                variable = new CtMap(locationMarker.getToken(), name);
                context.globalVariables.set(name, variable);
            } else {
                return null;
            }
        }
        if (variable instanceof CtMap palette) {
            return palette;
        }
        error(locationMarker, CtDiagnosticCode.ERROR, "Variable " + name + " is not a palette");
        return null;
    }

    private void copyRgbaColor(RgbaColor from, RgbaColor to) {
        ColorUtils.copy(from, to);
    }

    private void copyPalette(CtMap from, CtMap to) {
        to.clear();
        for (CtVariable variable : from) {
            to.set(variable.getName(), variable);
        }
    }

    private void resolveImports() {

        List<CtImport> imports = doc.getImports();
        for (CtImport i : imports) {
            String importName = i.getName().asString();
            int lastDot = importName.lastIndexOf(".");
            String typeName = importName.substring(0, lastDot);
            try {
            Class<?> jvmType = loadClass(typeName);
            if (jvmType == null) {
                error(i, CtDiagnosticCode.ERROR, "Unknown class " + typeName + " in import");
                continue;
            }
            String methodName = importName.substring(lastDot + 1);
            List<Method> methods = getMethodsByName(jvmType, methodName);
            if (methods.isEmpty()) {
                error(i, CtDiagnosticCode.ERROR, "Unknown method " + methodName + "in import");
                continue;
            }
            Method method = methods.getFirst();
            jvmMethods.put(methodName, method);
            } catch (Exception e) {
                error(i, CtDiagnosticCode.ERROR, "Unexpected error:" + e.getMessage());
            }
        }
    }

    private Class<?> loadClass(String name) {
        // 1. Try context class loader (set by Gradle worker threads)
        try {
            ClassLoader ccl = Thread.currentThread().getContextClassLoader();
            if (ccl != null) {
                return Class.forName(name, true, ccl);
            }
        } catch (ClassNotFoundException ignored) {}

        // 2. Try the ClassLoader that loaded this ExecutionEngine class
        try {
            return Class.forName(name, true, getClass().getClassLoader());
        } catch (ClassNotFoundException ignored) {}

        // 3. System fallback
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static List<Method> getMethodsByName(Class<?> clazz, String name) {
        List<Method> methods = new ArrayList<>();
        for (Method method : clazz.getMethods()){
            if(method.getName().equals(name)){
                methods.add(method);
            }
        }
        for (Method method : clazz.getDeclaredMethods()){
            if(method.getName().equals(name)){
                methods.add(method);
            }
        }
        return methods;
    }

    //
    // Diagnostics
    //

    private void error(CtNode node, Throwable t, DiagnosticCode code, Object... details) {
        if (node.getToken() == null) {
            reporter.error(t, code, details);
        } else {
            reporter.errorAt(uri, node.getToken(), t, code, details);
        }
    }

    private void error(CtNode node, DiagnosticCode code, Object... details) {
        if (node.getToken() == null) {
            reporter.error(code, details);
        } else {
            reporter.errorAt(getNodeUri(node), node.getToken(), code, details);
        }
    }

    private void error(DiagnosticCode code, Object... details) {
        reporter.error(code, details);
    }

    private void warn(CtNode node, DiagnosticCode code, Object... details) {
        if (node.getToken() == null) {
            reporter.warn(code, details);
        } else {
            reporter.warnAt(getNodeUri(node) , node.getToken(), code, details);
        }
    }

    private URI getNodeUri(CtNode node) {
        if (node == null) return null;
        URI nodeUri = node.getToken().getUri();
        if (node instanceof CtTemplate template && template.getUri() != null) {
            nodeUri = template.getUri();
        }
        return nodeUri;
    }

    private void warn(DiagnosticCode code, Object... details) {
        reporter.warn(code, details);
    }

    private void debug(String s, Object... details) {
        reporter.debug(s, details);
    }
}

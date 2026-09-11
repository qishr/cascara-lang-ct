package io.github.qishr.cascara.lang.ct.exec;

import java.io.InputStream;
import java.io.PrintStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import io.github.qishr.cascara.common.diagnostic.Diagnostic;
import io.github.qishr.cascara.common.diagnostic.Diagnostic.Level;
import io.github.qishr.cascara.common.diagnostic.LocalizableIOException;
import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.diagnostic.SilentCollectingReporter;
import io.github.qishr.cascara.common.io.IOUtils;
import io.github.qishr.cascara.common.io.provider.ResResourceProvider;
import io.github.qishr.cascara.common.util.CommandLine;
import io.github.qishr.cascara.common.util.CommandLine.Command;
import io.github.qishr.cascara.common.util.CommandLine.Option;
import io.github.qishr.cascara.common.util.CommandLine.Parameters;
import io.github.qishr.cascara.common.util.UriScheme;
import io.github.qishr.cascara.lang.ct.ast.CtDocument;
import io.github.qishr.cascara.lang.ct.ast.CtNode;
import io.github.qishr.cascara.lang.ct.ast.CtTemplate;
import io.github.qishr.cascara.lang.ct.internal.CtMap;
import io.github.qishr.cascara.lang.ct.processor.CtAstParser;

@Command(name = "ct", mixinStandardHelpOptions = true, version = "ct 0.1",
         description = "Executes a CT script, sending output to STDOUT or a specified file.")
public class CtRunner implements Callable<Integer> {
    @Parameters(index = "0", description = "The CT script to execute.")
    private String inFile;

    @Option(names = {"-o", "--output"}, description = "Output file name")
    private String outFile;

    @Option(names = {"-f", "--format"}, description = "Output format[s]")
    private String outputFormats;

    @Option(names = {"-v", "--verbose"}, description = "Debug output")
    private boolean verbose = false;

    public CtRunner() {
    }

    public static void main(String... args) {
        int exitCode = execute(args);
        System.exit(exitCode);
    }

    public static int execute(String... args) {
        return new CommandLine(new CtRunner()).execute(args);
    }

    @Override
    public Integer call() throws LocalizableIOException {
        // The following call is neccessary when running in Graalvm.
        // Normally it happens automatically.
        IOUtils.setResourceProvider(UriScheme.RES, new ResResourceProvider(CtRunner.class));

        ExecutionContext context = new ExecutionContext();

        context.outputFormatsList = parseStringList(outputFormats);
        if (context.outputFormatsList.isEmpty()) {
            System.err.println("No output format(s) specified");
            return 1;
        }

        URI uri = URI.create(inFile);
        context.globalVariables = new CtMap(null, null);
        context.outputFileName = outFile;
        context.formats = new HashMap<>();
        context.templates = new HashMap<>();
        context.problems = new ArrayList<>();
        context.verbose = verbose;

        if (verbose) {
            System.out.println("Input file : " + inFile);
            System.out.println("Output file: " + outFile);
            System.out.println("Format(s)  : " + outputFormats);
        }

        run(context, uri, true, null, null);

        if (!context.problems.isEmpty()) {
            if (reportProblems(context.problems)) {
                return 1;
            }
        }

        return 0;
    }

    public Integer run(
        ExecutionContext context,
        URI uri,
        boolean isTopLevel,
        PrintStream outputStream,
        CtTemplate parentTemplate
    ) throws LocalizableIOException {

        URI normalizedUri = IOUtils.normalizeUri(uri);

        Reporter reporter = setupReporter(uri, context);

        InputStream source = IOUtils.getContentAsStream(normalizedUri);

        CtAstParser parser = new CtAstParser()
            .setReporter(reporter)
            .setUri(normalizedUri);

        reporter.debug("Parsing " + normalizedUri);
        CtNode root = parser.parse(source);

        if (!(root instanceof CtDocument doc)) {
            System.err.println("Root should be a CtDocument");
            return 1;
        }

        try {
            CtExecutionEngine engine = new CtExecutionEngine(context, normalizedUri, isTopLevel, reporter, outputStream, parentTemplate).setReporter(reporter);
            return engine.execute(doc);
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    private Reporter setupReporter(URI uri, ExecutionContext context) {
        SilentCollectingReporter reporter = new SilentCollectingReporter()
            .setAnsiColoringEnabled(true)
            .setDiagnosticConsumer(p -> {
                if (p.getLevel().isProblem()) {
                    context.problems.add(p);
                } else if (context.verbose || p.getLevel() == Level.INFO) {
                    System.out.println(formatMessage(p));
                }
            });

        if (context.debugParser) {
            reporter.setLevel(Level.TRACE)
                    .setStackTraceEnabled(true);
        }
        return reporter;
    }

    private List<String> parseStringList(String formatsString) {
        List<String> outputFormatsList = new ArrayList<>();
        if (formatsString != null) {
            String[] formats = formatsString.split(",");
            for (String s : formats) {
                if (!s.isBlank()) {
                    outputFormatsList.add(s.trim());
                }
            }
        }
        return outputFormatsList;
    }

    /// @return true if errors were encountered.
    private boolean reportProblems(List<Diagnostic> problems) {
        int errors = 0;
        int warnings = 0;
        for (Diagnostic problem : problems) {
            System.err.println(formatMessage(problem));
            if (problem.getLevel() == Level.ERROR) {
                errors++;
            }
            if (problem.getLevel() == Level.WARN) {
                warnings++;
            }
        }
        System.err.println(
            String.format(
                "%d errors, %d warnings", errors, warnings)
        );
        return errors > 0;
    }

    public static String formatMessage(Diagnostic message) {
        if (message.getUri() == null || message.getLine() < 1) {
            return String.format(
                "[%s] %s",
                message.getLevel().getLogPrefix(),
                message.getMessage()
            );
        } else {
            String path = UriScheme.of(message.getUri()) == UriScheme.FILE
                ? Path.of(message.getUri()).toString()
                : message.getUri().toString();
            return String.format(
                "[%s] %s at %s:%d",
                message.getLevel().getLogPrefix(),
                message.getMessage(),
                path,
                message.getLine()
            );
        }
    }
}

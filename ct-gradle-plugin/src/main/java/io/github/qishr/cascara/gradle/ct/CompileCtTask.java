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

package io.github.qishr.cascara.gradle.ct;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;

import io.github.qishr.cascara.lang.ct.exec.CtRunner;

public abstract class CompileCtTask extends DefaultTask {

    @InputFile
    @PathSensitive(PathSensitivity.RELATIVE)
    public abstract RegularFileProperty getInputFile();

    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    public abstract DirectoryProperty getSourceDir();


    @OutputFile
    public abstract RegularFileProperty getOutputFile();

    @Input
    public abstract Property<String> getFormat();

    @Input
    @Optional
    public abstract Property<Boolean> getVerbose();

    @TaskAction
    public void compile() {
        File input = getInputFile().get().getAsFile();
        File output = getOutputFile().get().getAsFile();
        String formatName = getFormat().get();

        // Ensure destination directory exists
        File outputDir = output.getParentFile();
        if (outputDir != null && !outputDir.exists()) {
            outputDir.mkdirs();
        }

        List<String> args = new ArrayList<>();
        args.add("-o");
        args.add(output.getAbsolutePath());

        if (getFormat().isPresent()) {
            args.add("-f");
            args.add(getFormat().get());
        }

        // Check if verbose flag is set to true
        if (Boolean.TRUE.equals(getVerbose().getOrNull())) {
            args.add("-v");
        }

        args.add(input.getAbsolutePath());

        int exitCode = CtRunner.execute(args.toArray(new String[0]));

        if (exitCode != 0) {
            throw new RuntimeException("CT compilation failed for format '" + formatName + "' with exit code: " + exitCode);
        }
    }
}
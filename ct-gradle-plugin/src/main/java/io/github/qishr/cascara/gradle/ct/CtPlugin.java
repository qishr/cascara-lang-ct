package io.github.qishr.cascara.gradle.ct;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import io.github.qishr.cascara.common.util.StringUtils;

public class CtPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        CtExtension ext = project.getExtensions()
            .create("ct", CtExtension.class);

        project.afterEvaluate(p -> {
            registerCtTasks(project, ext);

            project.getTasks().named("build").configure(t -> {
                t.dependsOn("compileAllCt");
            });
        });
    }

    private void registerCtTasks(Project project, CtExtension ext) {
        // ext.getFormats() returns the NamedDomainObjectContainer<CtFormat>
        ext.getFormats().all(formatSpec -> {
            String formatName = formatSpec.getName();

            // formatSpec.getConfigs() returns the inner NamedDomainObjectContainer<CtCompilerConfig>
            formatSpec.getConfigs().all(compilerConfig -> {
                String themeName = compilerConfig.getName();

                // e.g., compileVscodeRetroAmberOnBrightBeige
                String taskName = "compile"
                    + StringUtils.toPascalCase(formatName)
                    + StringUtils.toPascalCase(themeName);

                project.getTasks().register(taskName, CompileCtTask.class, t -> {
                    // Entry file
                    t.getInputFile().set(
                        ext.getSourceDir().file(compilerConfig.getEntry())
                    );

                    // Whole CT source tree
                    t.getSourceDir().set(ext.getSourceDir());

                    // Format string passed to runner (-f flag)
                    t.getFormat().set(formatName);

                    t.getVerbose().set(compilerConfig.getVerbose());

                    // Output file location
                    if (compilerConfig.getOutput().isPresent()) {
                        t.getOutputFile().set(
                            project.getLayout().getProjectDirectory().file(compilerConfig.getOutput().get())
                        );
                    } else {
                        // Uses kebab-case theme name: <buildDir>/themes/<formatName>/<theme-name>
                        String fileName = StringUtils.toKebabCase(themeName);
                        t.getOutputFile().set(
                            project.getLayout().getBuildDirectory()
                                .file("themes/" + formatName + "/" + fileName)
                        );
                    }
                });
            });
        });

        // Aggregate task
        project.getTasks().register("compileAllCt", t -> {
            t.dependsOn(project.getTasks().withType(CompileCtTask.class));
        });
    }
}
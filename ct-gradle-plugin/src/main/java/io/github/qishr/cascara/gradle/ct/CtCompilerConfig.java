package io.github.qishr.cascara.gradle.ct;

import javax.inject.Inject;

import org.gradle.api.Named;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Optional;

public abstract class CtCompilerConfig implements Named {
    public abstract Property<String> getEntry();
    public abstract Property<String> getOutput();

    @Optional
    public abstract Property<Boolean> getVerbose();

    @Inject
    public CtCompilerConfig() {}

    @Override
    public abstract String getName();
}
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
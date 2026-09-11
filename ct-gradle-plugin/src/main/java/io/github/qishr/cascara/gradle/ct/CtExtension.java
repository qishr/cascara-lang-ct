package io.github.qishr.cascara.gradle.ct;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;

public abstract class CtExtension {

    public abstract DirectoryProperty getSourceDir();

    private final NamedDomainObjectContainer<CtFormat> formats;

    @Inject
    public CtExtension(ObjectFactory objects, Project project) {
        // Create container for formats using ObjectFactory
        this.formats = objects.domainObjectContainer(
            CtFormat.class,
            formatName -> objects.newInstance(CtFormat.class, formatName)
        );

        // Default sourceDir
        getSourceDir().convention(
            project.getLayout().getProjectDirectory().dir("src/main/ct")
        );
    }

    public NamedDomainObjectContainer<CtFormat> getFormats() {
        return formats;
    }

    public void formats(Action<? super NamedDomainObjectContainer<CtFormat>> action) {
        action.execute(formats);
    }
}
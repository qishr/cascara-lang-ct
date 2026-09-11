package io.github.qishr.cascara.gradle.ct;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.MissingMethodException;
import org.gradle.api.Action;
import org.gradle.api.Named;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.model.ObjectFactory;
import org.gradle.util.internal.ConfigureUtil;

import javax.inject.Inject;

public abstract class CtFormat extends GroovyObjectSupport implements Named {

    private final String name;
    private final NamedDomainObjectContainer<CtCompilerConfig> configs;

    @Inject
    public CtFormat(String name, ObjectFactory objects) {
        this.name = name;
        this.configs = objects.domainObjectContainer(CtCompilerConfig.class);
    }

    @Override
    public String getName() {
        return name;
    }

    public NamedDomainObjectContainer<CtCompilerConfig> getConfigs() {
        return configs;
    }

    public void themes(Action<? super NamedDomainObjectContainer<CtCompilerConfig>> action) {
        action.execute(configs);
    }

    // Intercepts arbitrary theme names inside the format block in Groovy DSL:
    // e.g. retroAmberOnBrightBeige { entry = "..." }
    public Object methodMissing(String name, Object args) {
        Object[] argsArray = (Object[]) args;
        if (argsArray.length == 1 && argsArray[0] instanceof Closure) {
            CtCompilerConfig theme = configs.maybeCreate(name);
            return ConfigureUtil.configure((Closure<?>) argsArray[0], theme);
        }
        throw new MissingMethodException(name, getClass(), argsArray);
    }
}
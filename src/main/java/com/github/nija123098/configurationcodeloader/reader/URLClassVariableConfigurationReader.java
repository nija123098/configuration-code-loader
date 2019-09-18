package com.github.nija123098.configurationcodeloader.reader;

import com.github.nija123098.configurationcodeloader.util.ConfigurationCodeLoaderException;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.function.Predicate;

/**
 * A {@link ConfigurationReader} instance for reading configuration values
 * from member fields of instances or static fields of classes from a class URL.
 *
 * @param <C> the base type of any variables produced by this class.
 *            Only guaranteed as long as the {@link Class} configured as the source and {@link Predicate<Field>}
 *            only results in fields who's variable values only are this type.
 */
public class URLClassVariableConfigurationReader<C> extends ChangingVariableConfigurationReader<C> {

    /**
     * Read the configuration data from a class according to {@link URLClassLoader} parameters.
     *
     * @param url       the URL to look at for the {@link URLClassLoader}.
     * @param className the class name to load the class for.
     */
    protected URLClassVariableConfigurationReader(URL url, String className) {
        this(url, className, null);
    }

    /**
     * Read the configuration data from a class according to {@link URLClassLoader} parameters.
     *
     * @param url         the URL to look at for the {@link URLClassLoader}.
     * @param className   the class name to load the class for.
     * @param fieldFilter the filter to decide if a field should be used to provide configuration data.
     */
    protected URLClassVariableConfigurationReader(URL url, String className, Predicate<Field> fieldFilter) {
        super(() -> {
            try {
                // since all references to the loader and class will be GCed the class should be unloaded
                return new URLClassLoader(new URL[]{url}).loadClass(className);
            } catch (ClassNotFoundException e) {
                throw new ConfigurationCodeLoaderException("Class: \"" + className + "\" not found in URL: \"" + url + "\"", e);
            }
        }, fieldFilter);
    }
}

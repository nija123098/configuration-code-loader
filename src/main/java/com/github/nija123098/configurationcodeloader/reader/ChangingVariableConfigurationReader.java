package com.github.nija123098.configurationcodeloader.reader;

import com.github.nija123098.configurationcodeloader.util.ConfigurationResults;

import java.lang.reflect.Field;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Like {@link ReloadRequiredConfigurationReader} and
 * {@link VariableConfigurationReader} combined
 * in order to read changing class definitions.
 *
 * @param <C> the base type of any configuration values produced by this class.
 *            Only guaranteed as long as the {@link VariableConfigurationReader}
 *            only provides configuration values of this type.
 */
public class ChangingVariableConfigurationReader<C> extends ReloadRequiredConfigurationReader<C> {
    private final Supplier<Class<?>> classSupplier;
    private final Predicate<Field> fieldFilter;

    /**
     * Read changing class definitions, as provided by the {@link Supplier<Class<?>>}.
     *
     * @param classSupplier a {@link Supplier<Class>} to provide class instances
     *                      to read and interpret according to {@link VariableConfigurationReader}.
     * @param fieldFilter   the filter to decide if a field should be used to provide configuration data.
     */
    protected ChangingVariableConfigurationReader(Supplier<Class<?>> classSupplier, Predicate<Field> fieldFilter) {
        this.classSupplier = classSupplier;
        this.fieldFilter = fieldFilter;
    }

    /**
     * Read changing class definitions, as provided by the {@link Supplier<Class<?>>}.
     *
     * @param classSupplier a {@link Supplier<Class>} to provide class instances
     *                      to read and interpret according to {@link VariableConfigurationReader}.
     */
    public ChangingVariableConfigurationReader(Supplier<Class<?>> classSupplier) {
        this(classSupplier, null);
    }

    @Override
    public ConfigurationResults<C> readValues() {
        return new VariableConfigurationReader<C>(this.classSupplier.get(), null, this.fieldFilter).readValues();
    }
}

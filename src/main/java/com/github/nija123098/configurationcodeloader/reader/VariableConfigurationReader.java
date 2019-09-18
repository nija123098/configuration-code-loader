package com.github.nija123098.configurationcodeloader.reader;

import com.github.nija123098.configurationcodeloader.util.ConfigurationCodeLoaderException;
import com.github.nija123098.configurationcodeloader.util.ConfigurationResults;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A {@link ConfigurationReader} instance for reading configuration values
 * from member fields of instances or static fields of classes.
 * <p>
 * This class if built without concept of unsetting configuration values
 * as a class definition's fields are constant over the lifetime of the definition.
 *
 * @param <C> the base type of any variables produced by this class.
 *            Only guaranteed as long as the {@link Class} configured as the source and {@link Predicate<Field>}
 *            only results in fields who's variable values only are this type.
 */
public class VariableConfigurationReader<C> extends ReloadRequiredConfigurationReader<C> {

    /**
     * A dedicated annotation to put on {@link Field}s
     * in order to indicate that they should be used as configuration data.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Configuration {
    }

    /**
     * The default {@link Predicate<Field>} to decide what fields should be used for getting configuration data.
     * <p>
     * Filters for any fields with the {@link Configuration} annotation.
     */
    private static final Predicate<Field> DEFAULT_FIELD_FILTER = field -> field.isAnnotationPresent(Configuration.class);

    /**
     * The object to get the value from if a member field,
     * null if reading should occur from a static field.
     */
    private final Object sourceObject;

    /**
     * The list of fields to read.
     */
    private final List<Field> fields;

    /**
     * Read configuration data from the provided {@link Class}
     * using the {@link VariableConfigurationReader#DEFAULT_FIELD_FILTER}.
     *
     * @param clazz the class to read configuration data from.
     */
    public VariableConfigurationReader(Class<?> clazz) {
        this(clazz, null);
    }

    /**
     * Read configuration data from the provided variable from the type {@link Class}
     * using the {@link VariableConfigurationReader#DEFAULT_FIELD_FILTER}.
     *
     * @param clazz        the class to read configuration data from.
     * @param sourceObject the object to get associated values from member fields.
     * @param <S>          the type of the provided class.
     */
    public <S> VariableConfigurationReader(Class<S> clazz, S sourceObject) {
        this(clazz, sourceObject, null);
    }

    /**
     * Read configuration data from the provided variable from the type {@link Class}
     * using the provided {@link Predicate<Field>} to filter which fields should provide configuration data.
     *
     * @param clazz        the class to read configuration data from.
     * @param sourceObject the object to get associated values from member fields.
     * @param fieldFilter  the filter to decide if a field should be used to provide configuration data.
     * @param <S>          the type of the provided class.
     */
    public <S> VariableConfigurationReader(Class<S> clazz, S sourceObject, Predicate<Field> fieldFilter) {
        this.sourceObject = sourceObject;
        this.fields = Stream.of(clazz.getDeclaredFields())
                .filter(fieldFilter == null ? DEFAULT_FIELD_FILTER : fieldFilter)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")// The specifier of the class must guarantee that variables are of type C
    @Override
    public ConfigurationResults<C> readValues() {
        ConfigurationResults<C> map = new ConfigurationResults<>(this.fields.size() + 1, 1);
        this.fields.forEach(field -> {
            try {
                map.put(field.getName(), (Optional<C>) Optional.ofNullable(field.get(this.sourceObject)));
            } catch (IllegalAccessException e) {
                throw new ConfigurationCodeLoaderException("Configuration variable not accessible: \"" + field.getDeclaringClass() + "#" + field.getName() + "\"", e);
            }
        });
        return map;
    }
}

package com.github.nija123098.configurationcodeloader;

import com.github.nija123098.configurationcodeloader.reader.ConfigurationReader;
import com.github.nija123098.configurationcodeloader.reader.VariableConfigurationReader;
import com.github.nija123098.configurationcodeloader.util.ConfigurationResults;
import org.junit.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;

public class ConfigurationCodeLoaderTest {
    private static final Object OBJECT_A = new Object(), OBJECT_B = new Object();
    @VariableConfigurationReader.Configuration
    public static Object VALUE = OBJECT_A;

    @Test
    public <C> void test() {
        ConfigurationCodeLoader<C> loader = new ConfigurationCodeLoader<>();
        ConfigurationReader<C> reader = new VariableConfigurationReader<>(ConfigurationCodeLoaderTest.class);
        loader.addConfigurationReader(reader);
        AtomicReference<Optional<C>> result = new AtomicReference<>();
        Consumer<ConfigurationResults<C>> resultsConsumer = objectConfigurationResults -> result.set(objectConfigurationResults.get("VALUE"));
        loader.registerListener(resultsConsumer);
        reader.startProviding();
        assertEquals(OBJECT_A, VALUE);// Check that it didn't effect it.  It really, really shouldn't.
        assertEquals(OBJECT_A, result.get().get());

        loader.removeConfigurationReader(reader);
        VALUE = OBJECT_B;
        reader.reload();
        assertEquals(OBJECT_A, result.get().get());
    }
}

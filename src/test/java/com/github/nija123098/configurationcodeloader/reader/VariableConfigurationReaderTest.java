package com.github.nija123098.configurationcodeloader.reader;

import com.github.nija123098.configurationcodeloader.util.ConfigurationCodeLoaderException;
import com.github.nija123098.configurationcodeloader.util.ConfigurationResults;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class VariableConfigurationReaderTest {

    @VariableConfigurationReader.Configuration
    public static Object OBJECT = new Object();

    @Test
    public void successTest() {
        final VariableConfigurationReader<?> reader = new VariableConfigurationReader<>(VariableConfigurationReaderTest.class);
        final AtomicReference<ConfigurationResults<?>> reference = new AtomicReference<>();
        reader.registerListener(reference::set);
        reader.startProviding();
        assertTrue(reference.get().get("OBJECT").isPresent());
        assertEquals(OBJECT, reference.get().get("OBJECT").get());
        OBJECT = new Object();
        reader.reload();
        assertTrue(reference.get().get("OBJECT").isPresent());
        assertEquals(OBJECT, reference.get().get("OBJECT").get());
    }

    @Test
    public void failTest() {
        final VariableConfigurationReader<?> reader = new VariableConfigurationReader<>(InvalidLoad.class);
        try {
            reader.startProviding();
            fail("Did not throw an exception failing to load the configuration");
        } catch (Exception e) {
            assertTrue(e instanceof ConfigurationCodeLoaderException);
        }
    }

    private static class InvalidLoad {
        @VariableConfigurationReader.Configuration
        private static Object OBJECT = new Object();
    }
}

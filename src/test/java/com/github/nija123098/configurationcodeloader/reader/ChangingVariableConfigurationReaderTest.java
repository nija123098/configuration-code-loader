package com.github.nija123098.configurationcodeloader.reader;

import org.junit.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class ChangingVariableConfigurationReaderTest {

    private static final Object OBJECT_A = new Object(), OBJECT_B = new Object(), OBJECT_D = new Object();

    @Test
    public void test() {
        AtomicReference<Class<?>> targetClass = new AtomicReference<>();
        ChangingVariableConfigurationReader<Object> reader = new ChangingVariableConfigurationReader<>(targetClass::get);
        AtomicReference<Optional<Object>> valueObject = new AtomicReference<>();
        reader.registerListener(objectConfigurationResults -> valueObject.set(objectConfigurationResults.get("OBJECT")));

        targetClass.set(ClassA.class);
        reader.startProviding();
        assertEquals(OBJECT_A, valueObject.get().get());

        targetClass.set(ClassB.class);
        reader.reload();
        assertEquals(OBJECT_B, valueObject.get().get());

        targetClass.set(ClassC.class);
        reader.reload();
        assertFalse(valueObject.get().isPresent());// The value is unset.

        targetClass.set(ClassD.class);
        reader.reload();
        assertEquals(OBJECT_D, valueObject.get().get());

        targetClass.set(Class2D.class);
        reader.reload();
        assertNull(valueObject.get());// The value has not changed.

        targetClass.set(ClassE.class);
        reader.stopProviding();
        assertNull(valueObject.get());// The value stays the same value.
    }

    public static class ClassA {
        @VariableConfigurationReader.Configuration
        public static final Object OBJECT = OBJECT_A;
    }

    public static class ClassB {
        @VariableConfigurationReader.Configuration
        public static final Object OBJECT = OBJECT_B;
    }

    public static class ClassC {
    }

    public static class ClassD {
        @VariableConfigurationReader.Configuration
        public static final Object OBJECT = OBJECT_D;
    }

    public static class Class2D {
        @VariableConfigurationReader.Configuration
        public static final Object OBJECT = OBJECT_D;
    }

    public static class ClassE {
        @VariableConfigurationReader.Configuration
        public static final Object OBJECT = OBJECT_D;
    }
}

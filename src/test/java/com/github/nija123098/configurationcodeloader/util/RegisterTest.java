package com.github.nija123098.configurationcodeloader.util;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;

public class RegisterTest {
    private static final Object A = new Object(),
            B = new Object(),
            C = new Object(),
            D = new Object(),
            E = new Object();

    @Test
    public void test() {
        final Register<Object> register = new Register<>();
        register.submit(A);

        final AtomicReference<Object> firstListenerReference = new AtomicReference<>();
        final Consumer<Object> firstListener = firstListenerReference::set;
        register.registerListener(firstListener);

        final AtomicReference<Object> secondListenerReference = new AtomicReference<>();
        final Consumer<Object> secondListener = secondListenerReference::set;
        register.registerListener(secondListener);

        register.submit(B);

        assertEquals(B, firstListenerReference.get());
        assertEquals(B, secondListenerReference.get());

        register.deregisterListener(firstListener);

        register.submit(C);

        assertEquals(B, firstListenerReference.get());
        assertEquals(C, secondListenerReference.get());

        register.deregisterListener(secondListener);

        register.submit(D);

        assertEquals(B, firstListenerReference.get());
        assertEquals(C, secondListenerReference.get());

        register.registerListener(firstListener);
        register.registerListener(secondListener);

        register.submit(E);

        assertEquals(E, firstListenerReference.get());
        assertEquals(E, secondListenerReference.get());
    }
}

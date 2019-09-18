package com.github.nija123098.configurationcodeloader.reader;

import com.github.nija123098.configurationcodeloader.util.ConfigurationCodeLoaderException;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class URLClassVariableConfigurationReaderTest {

    public static File makeTemporaryDirectory(String name) {
        try {
            File file = File.createTempFile(name, ".d");
            file.delete();
            file.mkdir();
            return file;
        } catch (IOException e) {
            throw new RuntimeException("Unable to make temporary file.", e);
        }
    }

    @BeforeClass
    public static void javacCheck() {
        try {
            Process process = Runtime.getRuntime().exec("javac -version");
            process.waitFor(10, TimeUnit.SECONDS);
            Assume.assumeTrue(0 == process.exitValue());
        } catch (IOException | InterruptedException e) {
            Assume.assumeTrue(false);
        }
    }

    @Test
    public void test() throws IOException, InterruptedException {
        File dir = null, innerDir = null, src = null, out = null;
        try {
            dir = makeTemporaryDirectory("configuration-code-loader-test");
            src = new File(dir, "TestClass.java").getAbsoluteFile();
            out = new File(src.getParentFile(), "TestClass.class").getAbsoluteFile();
            System.out.println(out);
            Files.write(src.toPath(),
                    Arrays.asList("package test;",
                            "public class TestClass {",
                            "   public static final Integer KEY = 5;",
                            "}"));
            Process process = new ProcessBuilder("javac", src.toString()).start();
            if (!process.waitFor(10, TimeUnit.SECONDS)) throw new RuntimeException("javac process not exited");
            if (process.exitValue() != 0) {
                throw new ConfigurationCodeLoaderException("javac exited with code \"" + process.exitValue() + "\"");
            }
            {
                innerDir = new File(out.getParentFile(), "test");
                innerDir.mkdir();
                File mov = new File(innerDir, "TestClass.class").getAbsoluteFile();
                Files.move(out.toPath(), mov.toPath());
                out = mov;
            }

            ConfigurationReader<?> reader = new URLClassVariableConfigurationReader<>(dir.toURI().toURL(), "test.TestClass", field -> field.getName().equals("KEY"));

            AtomicReference<Optional> reference = new AtomicReference<>();
            reader.registerListener(configurationResults -> reference.set(configurationResults.get("KEY")));
            reader.startProviding();

            src.delete();
            out.delete();
            innerDir.delete();
            dir.delete();

            assertEquals(5, reference.get().get());
        } catch (Exception e) {
            if (src != null && src.exists()) src.delete();
            if (out != null && out.exists()) out.delete();
            if (innerDir != null && innerDir.exists()) innerDir.delete();
            if (dir != null && dir.exists()) dir.delete();
            throw e;
        }
    }

    @Test
    public void noClassTest() throws IOException {
        File file = File.createTempFile("test", ".txt");
        try {
            new URLClassVariableConfigurationReader(file.toURI().toURL(), "test.NonExistant").startProviding();
            fail();
        } catch (Exception e) {
        }
        file.delete();
    }
}

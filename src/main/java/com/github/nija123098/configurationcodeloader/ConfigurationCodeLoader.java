package com.github.nija123098.configurationcodeloader;

import com.github.nija123098.configurationcodeloader.reader.ConfigurationReader;
import com.github.nija123098.configurationcodeloader.util.ConfigurationResults;
import com.github.nija123098.configurationcodeloader.util.Register;

import java.util.function.Consumer;

/**
 * Main class for the configuration code loader library.
 * <p>
 * Construct an instance with none or more {@link ConfigurationReader}s as sources for configuration.
 * Register listeners for updates to the configurations with {@link ConfigurationCodeLoader#registerListener(Consumer)}
 * to allow listening from all added {@link ConfigurationReader<C>} instances in a single call.
 */
public class ConfigurationCodeLoader<C> extends Register<ConfigurationResults<C>> {
    /**
     * The instance to register as a listener so registration management
     * for added {@link ConfigurationResults} is trivial.
     */
    private final Consumer<ConfigurationResults<C>> instanceListener = this::submit;

    public ConfigurationCodeLoader() {
    }

    /**
     * Add a {@link ConfigurationReader<C>} to add
     * listening results to this instance's own distirbution.
     *
     * @param reader the reader to add.
     */
    public void addConfigurationReader(ConfigurationReader<C> reader) {
        reader.registerListener(this.instanceListener);
    }

    /**
     * Remove a {@link ConfigurationReader<C>} to remove
     * listening results from this instance's own distirbution.
     *
     * @param reader the reader to remove.
     */
    public void removeConfigurationReader(ConfigurationReader<C> reader) {
        reader.deregisterListener(this.instanceListener);
    }
}

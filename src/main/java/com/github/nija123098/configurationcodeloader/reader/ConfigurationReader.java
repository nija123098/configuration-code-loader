package com.github.nija123098.configurationcodeloader.reader;

import com.github.nija123098.configurationcodeloader.util.ConfigurationResults;
import com.github.nija123098.configurationcodeloader.util.Register;

/**
 * A base class for configuration getting objects to provide some structure on how they should work.
 *
 * @param <C> the base type of any configuration values produced by this class.
 */
public abstract class ConfigurationReader<C> extends Register<ConfigurationResults<C>> {
    /**
     * Start reading the configuration data and transmit it to the listeners.
     * <p>
     * Once started if the configuration is changed the changed
     * configuration data should be submitted to listeners.
     * It is acceptable to resubmit configurations that have not changed,
     * but it is recommended that the reader try to prevent this.
     * <p>
     * Ignore in the case of already being started.
     */
    public abstract void startProviding();

    /**
     * Hint that the reader should check for updates to the configuration.
     * <p>
     * This may not be necessary if the class is capable of listening to changes it's self.
     * This ability may depend on the source's ability to provide that functionality.
     */
    public void reload() {
    }

    /**
     * Stop continuous reading of configuration data and release resources relating to it.
     * <p>
     * Startup may occur again once this is called, and should work properly.
     */
    public abstract void stopProviding();
}

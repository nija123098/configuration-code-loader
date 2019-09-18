package com.github.nija123098.configurationcodeloader.reader;

import com.github.nija123098.configurationcodeloader.util.ConfigurationResults;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link ConfigurationReader} for taking the difference between the current and a new reading
 * of configuration in order to only notify of actually updated configurations.
 *
 * @param <C> the base type of any configuration values produced by this class.
 */
public abstract class ReloadRequiredConfigurationReader<C> extends ConfigurationReader<C> {
    /**
     * The configuration values of the previous read
     * in order to determine the difference between the previous and a new read.
     */
    private final ConfigurationResults<C> configurations = new ConfigurationResults<>();

    /**
     * If this instance is running.
     */
    private volatile boolean started = false;

    @Override
    public synchronized void startProviding() {
        if (!this.started) {
            this.started = true;
            this.submit(this.readValues());
        }
    }

    @Override
    public synchronized void reload() {
        if (this.started) {
            ConfigurationResults<C> current = this.readValues();
            ConfigurationResults<C> resultMap = new ConfigurationResults<>(current.size() + this.configurations.size() + 1, 1);
            current.forEach((name, value) -> {// Iterate through all current values and compare with previous ones.
                Optional<C> previous = this.configurations.remove(name);
                if (Objects.equals(value, previous)) {
                    return;
                }
                resultMap.put(name, value);
            });
            this.configurations.forEach((s, configurationValueContainer) -> resultMap.put(s, Optional.empty()));
            this.configurations.clear();
            this.configurations.putAll(current);
            this.submit(resultMap);
        }
    }

    @Override
    public synchronized void stopProviding() {
        this.started = false;
    }

    /**
     * Preform a read of the current values from the source of this value.
     *
     * @return the configuration data parsed in it's entirety.
     */
    public abstract ConfigurationResults<C> readValues();
}

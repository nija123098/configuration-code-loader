package com.github.nija123098.configurationcodeloader.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConfigurationResults<C> extends HashMap<String, Optional<C>> {
    public ConfigurationResults(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public ConfigurationResults(int initialCapacity) {
        super(initialCapacity);
    }

    public ConfigurationResults() {
        super();
    }

    public ConfigurationResults(Map<? extends String, ? extends Optional<C>> m) {
        super(m);
    }
}

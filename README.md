# Configuration Code Loader
Load configuration files that describe code to load and use as configured objects.

## Motivation
Configuration most often comes as a key-value list of strings or in a map format like YAML.
Configuration Code Loader loads code as configuration to provide a more direct path between configuration and code.
This is most often useful when different implementations of a single interface, something extremely useful in early development and testing.

## How To Use
Class level documentation can be found in Javadocs in the code.
This will explain the code structure on a higher level.

### Register
Register is a utility class which has two main features, submitting values, and registering consumers that are invoked when a submission occurs.
Both ConfigurationCodeLoader and ConfigurationReader instances inherit from Register, both for listening to ConfigurationResults submitted by the relevant instance.

### ConfigurationResults
ConfigurationResults are Map<String, Optional<?>> instances that occur to notify of changes to variables.
A single configuration is represented by the String,Optional<?> pairing.
The string represents the name of the configuration.
The contents of the Optional represent the value of the configuration, with the Optional being empty (null contents) representing an unset configuration.
If a difference between null and unset is required, the configuration value should be set in a wrapper, where the null value of the configuration would represent unset and the contents of the wrapper when set would represent the value, including null.

### ConfigurationCodeLoader
ConfigurationCodeLoader is a container for configuration data and should generally be used for managing multiple ConfigurationReaders.
It enables listening for configuration data updates from multiple sources through a single object.

### ConfigurationReader
ConfigurationReaders are providers of configuration information in the form of Optionals containing objects to a ConfigurationCodeLoader.
The configuration values are provided in the form of String to ? maps where ? is the type specified by the type parameter of the reader, which is generally just Object.
ConfigurationReader instances require setup, which generally occur at the constructor.
They must then be started up, generally after being added to a ConfigurationCodeLoader, in order to start delivering configuration values.
ConfigurationReaders should be capable of reloading, either constantly by detecting the change that would cause the configuration data to change, or by being called to reload.


### Example
```Java
import com.github.nija123098.configurationcodeloader.ConfigurationCodeLoader;
import com.github.nija123098.configurationcodeloader.reader.VariableConfigurationReader;

class Example {
    public static final Object GREETING = "Hello";
    public static void main(String[] args) {
        // Initialize a ConfigurationCodeLoader
        ConfigurationCodeLoader<?> loader = new ConfigurationCodeLoader<>();
        // Register a listener to look for updates
        loader.registerConfigurationListener(configurationMap -> System.out.println("Listener: " + configurationMap.get("GREETING").getValue()));
        // Register a listener to look for updates
        
        // Chose a ConfigurationReader type.  For this example a single VariableConfigurationReader will be used.  Use of 
        VariableConfigurationReader<?> reader = new VariableConfigurationReader<>(Example.class);
        // Add the reader to the loader.
        loader.addConfigurationReader(reader);
        // Start the reader.  When this occurs the listener above will consume the changed configuration.  In this case it will print the value of GREETING to standard output.
        reader.startProviding();
        // Configuration values can also be gotten from the loader directly.
        System.out.println("Get: " + loader.getConfig("GREETING").getValue());
    }
}
```

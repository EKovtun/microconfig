package io.microconfig.properties.resolver.placeholder;

import io.microconfig.environments.Component;
import io.microconfig.properties.Property;

import java.util.Optional;

public interface PlaceholderResolveStrategy {
    Optional<Property> resolve(Component component, String propertyKey, String environment);
}
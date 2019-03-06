package pluginapi;

import io.microconfig.commands.buildconfig.factory.ConfigType;
import io.microconfig.commands.buildconfig.factory.MicroconfigFactory;
import io.microconfig.commands.buildconfig.factory.StandardConfigType;
import io.microconfig.configs.Property;
import io.microconfig.configs.resolver.EnvComponent;
import io.microconfig.configs.resolver.PropertyResolver;
import io.microconfig.configs.resolver.PropertyResolverHolder;
import io.microconfig.utils.MicronconfigTestFactory;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.microconfig.configs.Property.parse;
import static io.microconfig.configs.PropertySource.fileSource;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Stream.of;

public class PluginApiTestIT {
    private static final String ERROR_VALUE = "ERROR";

    public void placeholderValues() {
        System.out.println(valueToEnvs("${ports@p1} ${ports@p2}", new File("source.yaml")));
    }

    public Map<String, List<String>> valueToEnvs(String lineWithPlaceholders, File currentFile) {
        MicroconfigFactory factory = MicronconfigTestFactory.getFactory();
        PropertyResolver propertyResolver = ((PropertyResolverHolder) factory
                .newConfigProvider(configTypeFor(currentFile)))
                .getResolver();

        Property property = parse(lineWithPlaceholders, "", fileSource(currentFile, -1, false));
        Set<String> envs = factory.getEnvironmentProvider().getEnvironmentNames();

        return envs.stream()
                .collect(groupingBy(env -> resolve(property.withNewEnv(env), propertyResolver)));
    }

    private String resolve(Property p, PropertyResolver propertyResolver) {
        try {
            return propertyResolver.resolve(p, new EnvComponent(p.getSource().getComponent(), p.getEnvContext()));
        } catch (RuntimeException e) {
            return ERROR_VALUE;
        }
    }

    private ConfigType configTypeFor(File file) {
        String ext = fileExtension(file.getName());
        return of(StandardConfigType.values())
                .filter(ct -> ct.getConfigExtensions().stream().anyMatch(e -> e.equals(ext)))
                .map(StandardConfigType::type)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Can't find ConfigType for extension " + ext));
    }

    private static String fileExtension(String currentFileName) {
        int lastDot = currentFileName.lastIndexOf('.');
        if (lastDot >= 0) return currentFileName.substring(lastDot);

        throw new IllegalArgumentException();
    }
}

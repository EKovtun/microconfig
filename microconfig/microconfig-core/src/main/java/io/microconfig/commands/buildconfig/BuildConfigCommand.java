package io.microconfig.commands.buildconfig;

import io.microconfig.commands.Command;
import io.microconfig.commands.CommandContext;
import io.microconfig.core.environments.Component;
import io.microconfig.core.environments.EnvironmentProvider;
import io.microconfig.core.properties.ConfigProvider;
import io.microconfig.core.properties.Property;
import io.microconfig.core.properties.resolver.EnvComponent;
import io.microconfig.core.properties.serializer.ConfigSerializer;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.microconfig.utils.Logger.info;
import static io.microconfig.utils.Logger.logLineBreak;

@RequiredArgsConstructor
public class BuildConfigCommand implements Command {
    private final EnvironmentProvider environmentProvider;
    private final ConfigProvider configProvider;
    private final ConfigSerializer configSerializer;

    private final BuildConfigPostProcessor postProcessor;

    @Override
    public void execute(CommandContext context) {
        List<Component> componentsToBuild = context.components(environmentProvider);

        int processedComponents = componentsToBuild.parallelStream()
                .mapToInt(component -> processComponent(component, context.env()))
                .sum();

        if (processedComponents > 0) {
            logLineBreak();
        }
    }

    private int processComponent(Component component, String env) {
        Map<String, Property> properties = configProvider.getProperties(component, env);
        Optional<File> outputFile = configSerializer.serialize(component.getName(), env, properties.values());

        outputFile.ifPresent(f -> {
            postProcessor.process(new EnvComponent(component, env), properties, configProvider, f);
            info("Generated " + f.getName() + " for " + component.getName() + " [" + env + "]");
        });

        return outputFile.isPresent() ? 1 : 0;
    }
}
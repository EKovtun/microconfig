package io.microconfig.factory;

import io.microconfig.commands.Command;
import io.microconfig.commands.buildconfig.BuildConfigPostProcessor;
import io.microconfig.commands.buildconfig.features.templates.CopyTemplatesPostProcessor;
import io.microconfig.commands.buildconfig.features.templates.CopyTemplatesServiceImpl;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.List;

import static io.microconfig.commands.Command.composite;

@RequiredArgsConstructor
public class BuildConfigCommandFactory {
    private final ConfigsTypeProvider configsTypeProvider;

    public Command newCommand(File rootDir, File destinationComponentDir) {
        List<ConfigType> configTypes = configsTypeProvider.getConfigTypes(rootDir);
        BuildConfigPostProcessor postProcessor = copyTemplatesPostProcessor();

        MicroconfigFactory factory = MicroconfigFactory.init(rootDir, destinationComponentDir);
        return composite(
                configTypes.stream()
                        .map(type -> factory.newBuildCommand(type, postProcessor))
                        .toArray(Command[]::new)
        );
    }

    private BuildConfigPostProcessor copyTemplatesPostProcessor() {
        return new CopyTemplatesPostProcessor(new CopyTemplatesServiceImpl());
    }
}
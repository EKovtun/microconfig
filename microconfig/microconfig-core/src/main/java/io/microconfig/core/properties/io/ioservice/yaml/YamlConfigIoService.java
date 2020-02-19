package io.microconfig.core.properties.io.ioservice.yaml;

import io.microconfig.core.properties.io.ioservice.ConfigIoService;
import io.microconfig.core.properties.io.ioservice.ConfigReader;
import io.microconfig.core.properties.io.ioservice.ConfigWriter;
import io.microconfig.utils.reader.FilesReader;
import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public class YamlConfigIoService implements ConfigIoService {
    private final FilesReader fileReader;

    @Override
    public ConfigReader read(File file) {
        return new YamlReader(file, fileReader);
    }

    @Override
    public ConfigWriter writeTo(File file) {
        return new YamlWriter(file);
    }
}
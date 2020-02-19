package io.microconfig.core.environments.filebased;

import java.io.File;
import java.util.List;

public interface EnvironmentParserSelector {
    EnvironmentParser selectParser(File envFile);

    List<String> supportedFormats();
}

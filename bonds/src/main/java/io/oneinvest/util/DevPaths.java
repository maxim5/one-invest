package io.oneinvest.util;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;

public class DevPaths {
    public static final String PROJECT_HOME = locateProjectHome();
    public static final String BONDS_HOME = PROJECT_HOME + "bonds/";
    public static final String BONDS_RESOURCES = BONDS_HOME + "src/main/resources/";

    private static @NotNull String locateProjectHome() {
        Path workingDir = Path.of(".").toAbsolutePath();
        while (workingDir != null && !Files.exists(workingDir.resolve(".infra"))) {
            workingDir = workingDir.getParent();
        }
        return (workingDir != null ? workingDir.toString() : ".") + "/";
    }
}

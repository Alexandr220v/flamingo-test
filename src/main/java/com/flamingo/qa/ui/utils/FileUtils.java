package com.flamingo.qa.ui.utils;

import java.nio.file.Path;

public class FileUtils {

    public static Path getUploadFilePath(String fileName) {
        return Path.of("src", "test", "resources", fileName).toAbsolutePath();
    }
}

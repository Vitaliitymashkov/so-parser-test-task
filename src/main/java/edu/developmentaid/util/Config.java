package edu.developmentaid.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public final class Config {

    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
    public static final String CONFIG_PROPERTIES_FILE_NAME = "src\\main\\resources\\config.properties";

    public static String getProperty(String name, String defaultValue) {
        try {
            FileReader reader = new FileReader(CONFIG_PROPERTIES_FILE_NAME);
            Properties p = new Properties();
            p.load(reader);
            return p.getProperty(name);
        } catch (IOException e) {
            LOGGER.error("Failed to read {} from {}.", name, CONFIG_PROPERTIES_FILE_NAME);
            return defaultValue;
        }
    }
}

package com.example.qanewsservice.autotests.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class JasyptConfig {

    private final Properties properties = new Properties();
    private final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();

    public JasyptConfig(String env, String password) {
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setPassword(password);

        String filename = env + ".properties";
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(filename)) {
            if (input == null) {
                throw new IllegalStateException("Config file not found: " + filename);
            }
            properties.load(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read config file: " + filename, exception);
        }
    }

    public String get(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            value = properties.getProperty(key);
        }
        return decryptIfNeeded(value);
    }

    public String getRequired(String key) {
        String value = get(key);
        if (isBlank(value)) {
            throw new IllegalStateException("Required config property is empty: " + key);
        }
        return value;
    }

    public int getInt(String key, int defaultValue) {
        String value = get(key);
        return isBlank(value) ? defaultValue : Integer.parseInt(value);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        return isBlank(value) ? defaultValue : Boolean.parseBoolean(value);
    }

    private String decryptIfNeeded(String value) {
        if (value != null && value.startsWith("ENC(") && value.endsWith(")")) {
            return encryptor.decrypt(value.substring(4, value.length() - 1));
        }
        return value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}

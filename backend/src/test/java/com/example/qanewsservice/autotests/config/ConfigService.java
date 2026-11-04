package com.example.qanewsservice.autotests.config;

public final class ConfigService {

    private static volatile JasyptConfig config;

    private ConfigService() {
    }

    public static JasyptConfig getConfig() {
        if (config == null) {
            synchronized (ConfigService.class) {
                if (config == null) {
                    String env = System.getProperty("env", "dev");
                    String password = System.getProperty("jasyptEncryptorPassword", "test");
                    config = new JasyptConfig(env, password);
                }
            }
        }
        return config;
    }

    public static String get(String key) {
        return getConfig().get(key);
    }

    public static String getRequired(String key) {
        return getConfig().getRequired(key);
    }
}

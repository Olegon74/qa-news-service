package com.example.qanewsservice.autotests.ui.config;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.example.qanewsservice.autotests.config.ConfigService;
import com.example.qanewsservice.autotests.config.JasyptConfig;
import io.qameta.allure.selenide.AllureSelenide;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.MutableCapabilities;

public abstract class BaseConfiguration {

    private static final AtomicBoolean ALLURE_LISTENER_INSTALLED = new AtomicBoolean(false);

    protected JasyptConfig config;

    @BeforeEach
    protected void configureSelenide() {
        config = ConfigService.getConfig();

        Configuration.baseUrl = config.getRequired("ui.base_url");
        String browser = defaultIfBlank(config.get("browser"), "chrome");
        Configuration.browser = "opera".equalsIgnoreCase(browser)
                ? OperaWebDriverProvider.class.getName() : browser;
        Configuration.browserSize = defaultIfBlank(config.get("browser_size"), "1920x1080");
        Configuration.timeout = config.getInt("timeout", 15000);
        Configuration.headless = config.getBoolean("headless", true);
        Configuration.screenshots = true;
        Configuration.savePageSource = true;

        String remoteUrl = config.get("remote_url");
        Configuration.remote = isBlank(remoteUrl) ? null : remoteUrl;

        String browserVersion = config.get("browser_version");
        Configuration.browserVersion = isBlank(browserVersion) ? null : browserVersion;

        configureRemoteCapabilities(remoteUrl);

        if (ALLURE_LISTENER_INSTALLED.compareAndSet(false, true)) {
            SelenideLogger.addListener("allure",
                    new AllureSelenide().screenshots(true).savePageSource(true));
        }
    }

    @AfterEach
    protected void closeWebDriver() {
        Selenide.clearBrowserLocalStorage();
        Selenide.closeWebDriver();
    }

    private void configureRemoteCapabilities(String remoteUrl) {
        MutableCapabilities capabilities = new MutableCapabilities();
        if (!isBlank(remoteUrl)) {
            if ("opera".equalsIgnoreCase(config.get("browser"))) {
                capabilities.setCapability("browserName", "opera");
            }
            MutableCapabilities selenoidOptions = new MutableCapabilities();
            selenoidOptions.setCapability("enableVNC", config.getBoolean("selenoid.enable_vnc", true));
            selenoidOptions.setCapability("enableLog", config.getBoolean("selenoid.enable_log", true));
            selenoidOptions.setCapability("enableVideo", config.getBoolean("selenoid.enable_video", false));
            selenoidOptions.setCapability("screenResolution", Configuration.browserSize + "x24");
            setCapabilityIfNotBlank(selenoidOptions, "sessionTimeout", config.get("selenoid.session_timeout"));
            setCapabilityIfNotBlank(selenoidOptions, "name", config.get("selenoid.session_name"));
            capabilities.setCapability("selenoid:options", selenoidOptions);
        }
        Configuration.browserCapabilities = capabilities;
    }

    private void setCapabilityIfNotBlank(MutableCapabilities capabilities, String name, String value) {
        if (!isBlank(value)) {
            capabilities.setCapability(name, value);
        }
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return isBlank(value) ? defaultValue : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}

package com.example.qanewsservice.autotests.ui.config;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverProvider;
import java.net.MalformedURLException;
import java.net.URI;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class OperaWebDriverProvider implements WebDriverProvider {

    @Override
    public WebDriver createDriver(Capabilities capabilities) {
        if (Configuration.remote == null || Configuration.remote.isBlank()) {
            throw new IllegalStateException("Opera tests require Selenoid remote_url");
        }

        MutableCapabilities operaCapabilities = new MutableCapabilities(capabilities);
        operaCapabilities.setCapability("browserName", "opera");
        try {
            return new RemoteWebDriver(URI.create(Configuration.remote).toURL(), operaCapabilities);
        } catch (MalformedURLException exception) {
            throw new IllegalArgumentException("Invalid Selenoid remote_url: " + Configuration.remote, exception);
        }
    }
}

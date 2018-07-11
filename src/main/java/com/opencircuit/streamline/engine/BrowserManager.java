package com.opencircuit.streamline.engine;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

class BrowserManager {

    static WebDriver driver;
    static String screenshotDirectory;
    private final DateTimeManager dateTimeManager = new DateTimeManager();

    void invokeBrowser(String browserName) {

        try {

            createDriverInstance(browserName);
            setDefaultDriverProperties();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createDriverInstance(String browserName) {

        switch (browserName) {

            case "Firefox 32-Bit":
                createFirefoxDriverInstance("\\32-bit");
                break;

            case "Firefox 64-Bit":
                createFirefoxDriverInstance("\\64-bit");
                break;

            case "Internet Explorer 32-Bit":
                createInternetExplorerDriverInstance("\\32-bit");
                break;

            case "Internet Explorer 64-Bit":
                createInternetExplorerDriverInstance("\\64-bit");
                break;

            default:
                createChromeDriverInstance();
                break;
        }
    }

    private void createFirefoxDriverInstance(String subFolderPath) {

        String driversDirectory = System.getProperty("user.dir") + "\\Resources\\Drivers";
        String driverPath = driversDirectory + subFolderPath + "\\geckodriver.exe";
        System.setProperty("webdriver.gecko.driver", driverPath);
        driver = new FirefoxDriver();
    }

    private void createInternetExplorerDriverInstance(String subFolderPath) {

        String driversDirectory = System.getProperty("user.dir") + "\\Resources\\Drivers";
        String driverPath = driversDirectory + subFolderPath + "\\IEDriverServer.exe";
        System.setProperty("webdriver.ie.driver", driverPath);
        driver = new InternetExplorerDriver();
    }

    private void createChromeDriverInstance() {

        String driverPath = System.getProperty("user.dir") + "\\Resources\\Drivers\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", driverPath);
        driver = new ChromeDriver();
    }

    private void setDefaultDriverProperties() {

        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        driver.manage().window().maximize();
    }

    boolean navigateToUrl(String url) {

        try {

            driver.navigate().to(url);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    boolean switchToWindow(String windowTitle) {

        String parentWindowHandle = driver.getWindowHandle();

        try {

            boolean switchSuccessful = switchToWindowAndCheckTitleMatch(windowTitle);
            switchToParentWindowIfWindowSwitchFailed(parentWindowHandle, switchSuccessful);
            return switchSuccessful;

        } catch (Exception e) {

            switchToParentWindowIfWindowSwitchFailed(parentWindowHandle, false);
            return false;
        }
    }

    private boolean switchToWindowAndCheckTitleMatch(String windowTitle) {

        for (String windowHandle : driver.getWindowHandles()) {

            driver.switchTo().window(windowHandle);
            if (driver.getTitle().equalsIgnoreCase(windowTitle)) {
                return true;
            }
        }

        return false;
    }

    private void switchToParentWindowIfWindowSwitchFailed(String parentWindowHandle, boolean switchSuccessful) {

        if (!switchSuccessful) {
            driver.switchTo().window(parentWindowHandle);
        }
    }

    boolean closeWindow() {

        try {

            driver.close();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    boolean switchToFrameByName(String frameName) {

        try {

            driver.switchTo().frame(frameName);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    boolean switchToFrameByIndex(String frameIndex) {

        try {

            driver.switchTo().frame(Integer.parseInt(frameIndex));
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    boolean switchToDefaultContent() {

        try {

            driver.switchTo().defaultContent();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    String takeScreenshot(String screenshotName) {

        try {

            String fullScreenshotName = screenshotName + "-" + dateTimeManager.getCurrentDateTime();
            takeSpecifiedScreenshot(fullScreenshotName);
            return fullScreenshotName;

        } catch (Exception e) {
            return "";
        }
    }

    private void takeSpecifiedScreenshot(String fullScreenshotName) throws IOException {

        String screenshotPath = screenshotDirectory + fullScreenshotName  + ".png";
        File screenshot = ((TakesScreenshot) BrowserManager.driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(screenshot, new File(screenshotPath));
    }

    void quitBrowser() {

        try {
            driver.quit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package com.opencircuit.streamline.engine;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

class BrowserDriver {

    //******************************************************************************************************************
    //*     Setup - Do Not Alter
    //******************************************************************************************************************

    private Common common = new Common();
    private WebDriver driver;

    private String screenshotDirectory;
    private String identifierType;
    private String identifierValue;
    private String dataValue;
    private String screenshotName;

    BrowserDriver(String screenshotDirectory) {

        this.screenshotDirectory = screenshotDirectory;
        this.identifierType = "";
        this.identifierValue = "";
        this.dataValue = "";
    }

    //******************************************************************************************************************
    //*     Private Methods
    //******************************************************************************************************************

    private String pageReadyState() {

        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (String) js.executeScript("return document.readyState");
    }

    private WebElement retrieveWebElement() {

        verifyPageLoadIsComplete("1");
        WebElement element = null;
        String locatorType = identifierType.toLowerCase();

        try {

            switch (locatorType) {

                case "id":
                    element = driver.findElement(By.id(identifierValue));
                    break;

                case "name":
                    element = driver.findElement(By.name(identifierValue));
                    break;

                case "link-text":
                    element = driver.findElement(By.linkText(identifierValue));
                    break;

                case "partial-link-text":
                    element = driver.findElement(By.partialLinkText(identifierValue));
                    break;

                case "tag-name":
                    element = driver.findElement(By.tagName(identifierValue));
                    break;

                case "class-name":
                    element = driver.findElement(By.className(identifierValue));
                    break;

                case "css-selector":
                    element = driver.findElement(By.cssSelector(identifierValue));
                    break;

                case "xpath":
                    element = driver.findElement(By.xpath(identifierValue));
                    break;

                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return element;
    }

    private List<WebElement> retrieveWebElements() {

        verifyPageLoadIsComplete("1");
        List<WebElement> elements = null;
        String locatorType = identifierType.toLowerCase();

        try {

            switch (locatorType) {

                case "id":
                    elements = driver.findElements(By.id(identifierValue));
                    break;

                case "name":
                    elements = driver.findElements(By.name(identifierValue));
                    break;

                case "link-text":
                    elements = driver.findElements(By.linkText(identifierValue));
                    break;

                case "partial-link-text":
                    elements = driver.findElements(By.partialLinkText(identifierValue));
                    break;

                case "tag-name":
                    elements = driver.findElements(By.tagName(identifierValue));
                    break;

                case "class-name":
                    elements = driver.findElements(By.className(identifierValue));
                    break;

                case "css-selector":
                    elements = driver.findElements(By.cssSelector(identifierValue));
                    break;

                case "xpath":
                    elements = driver.findElements(By.xpath(identifierValue));
                    break;

                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return elements;
    }

    //******************************************************************************************************************
    //*     Protected Methods
    //******************************************************************************************************************

    void invokeBrowser(String browserName, String driversDirectory) {

        try {

            if (browserName.equalsIgnoreCase("Chrome")) {

                String driverPath = driversDirectory + "chromedriver.exe";
                System.setProperty("webdriver.chrome.driver", driverPath);
                driver = new ChromeDriver();

            } else if (browserName.equalsIgnoreCase("Firefox 32-Bit")) {

                String driverPath = driversDirectory + "32-bit\\geckodriver.exe";
                System.setProperty("webdriver.gecko.driver", driverPath);
                driver = new FirefoxDriver();

            } else if (browserName.equalsIgnoreCase("Firefox 64-Bit")) {

                String driverPath = driversDirectory + "64-bit\\geckodriver.exe";
                System.setProperty("webdriver.gecko.driver", driverPath);
                driver = new FirefoxDriver();

            } else if (browserName.equalsIgnoreCase("Internet Explorer 32-Bit")) {

                String driverPath = driversDirectory + "32-bit\\IEDriverServer.exe";
                System.setProperty("webdriver.ie.driver", driverPath);
                driver = new InternetExplorerDriver();

            } else if (browserName.equalsIgnoreCase("Internet Explorer 64-Bit")) {

                String driverPath = driversDirectory + "64-bit\\IEDriverServer.exe";
                System.setProperty("webdriver.ie.driver", driverPath);
                driver = new InternetExplorerDriver();
            }

            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            driver.manage().window().maximize();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean navigateToUrl() {

        boolean stepSuccessful = false;

        try {

            driver.navigate().to(dataValue);
            stepSuccessful = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stepSuccessful;
    }

    boolean switchToWindow() {

        boolean stepSuccessful = false;
        String parentWindowHandle = driver.getWindowHandle();

        try {

            for (String windowHandle : driver.getWindowHandles()) {

                driver.switchTo().window(windowHandle);
                String windowTitle = driver.getTitle();

                if (windowTitle.equalsIgnoreCase(dataValue)) {
                    stepSuccessful = true;
                    break;
                }
            }

            if (!stepSuccessful) {
                driver.switchTo().window(parentWindowHandle);
            }

        } catch (Exception e) {

            driver.switchTo().window(parentWindowHandle);
            e.printStackTrace();
        }

        return stepSuccessful;
    }

    boolean closeWindow() {

        boolean stepSuccessful = false;

        try {

            driver.close();
            stepSuccessful = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stepSuccessful;
    }

    boolean switchToFrame() {

        boolean stepSuccessful = false;

        try {

            driver.switchTo().defaultContent();
            driver.switchTo().frame(dataValue);
            stepSuccessful = true;

        } catch (Exception e) {

            driver.switchTo().defaultContent();
            e.printStackTrace();
        }

        return stepSuccessful;
    }

    boolean typeText() {

        boolean stepSuccessful = false;

        try {

            WebElement element = retrieveWebElement();
            if (element == null) { return false; }

            element.sendKeys(dataValue);
            stepSuccessful = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stepSuccessful;
    }

    boolean clearText() {

        boolean stepSuccessful = false;

        try {

            WebElement element = retrieveWebElement();
            if (element == null) { return false; }

            element.clear();
            stepSuccessful = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stepSuccessful;
    }

    boolean matchTextboxText() {

        boolean stepSuccessful = false;

        try {

            WebElement element = retrieveWebElement();
            if (element == null) { return false; }

            String value = element.getAttribute("value");
            stepSuccessful = value.equalsIgnoreCase(dataValue);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stepSuccessful;
    }

    boolean selectByVisibleText() {

        boolean stepSuccessful = false;

        try {

            WebElement element = retrieveWebElement();
            if (element == null) { return false; }

            Select dropdown = new Select(element);
            dropdown.selectByVisibleText(dataValue);
            stepSuccessful = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stepSuccessful;
    }

    boolean selectByValue() {

        boolean stepSuccessful = false;

        try {

            WebElement element = retrieveWebElement();
            if (element == null) { return false; }

            Select dropdown = new Select(element);
            dropdown.selectByValue(dataValue);
            stepSuccessful = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stepSuccessful;
    }

    boolean selectByIndex() {

        boolean stepSuccessful = false;

        try {

            int selectionIndex = Integer.parseInt(dataValue);
            WebElement element = retrieveWebElement();
            if (element == null) { return false; }

            Select dropdown = new Select(element);
            dropdown.selectByIndex(selectionIndex);
            stepSuccessful = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stepSuccessful;
    }

    boolean selectRadioButtonByValue() {

        boolean stepSuccessful = false;

        try {

            List<WebElement> elements = retrieveWebElements();
            if (elements == null) { return false; }

            for(WebElement element : elements) {

                String elementValue = element.getAttribute("value");

                if (elementValue.equalsIgnoreCase(dataValue)) {
                    element.click();
                    stepSuccessful = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stepSuccessful;
    }

    boolean clickElement() {

        boolean stepSuccessful = false;

        try {

            WebElement element = retrieveWebElement();
            if (element == null) { return false; }

            element.click();
            stepSuccessful = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stepSuccessful;
    }

    boolean matchTextInElement() {

        boolean stepSuccessful = false;

        try {

            WebElement element = retrieveWebElement();
            if (element == null) { return false; }

            String text = element.getText();
            stepSuccessful = text.equalsIgnoreCase(dataValue);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stepSuccessful;
    }

    boolean verifyElementIsDisplayed() {

        boolean stepSuccessful = false;

        try {

            int waitTime = Integer.parseInt(dataValue);

            for (int seconds = 0; seconds < waitTime; seconds++) {

                WebElement element = retrieveWebElement();
                if (element != null) { stepSuccessful = element.isDisplayed(); }
                if (stepSuccessful) { break; }
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stepSuccessful;
    }

    boolean verifyElementIsEnabled() {

        boolean stepSuccessful = false;

        try {

            int waitTime = Integer.parseInt(dataValue);

            for (int seconds = 0; seconds < waitTime; seconds++) {

                WebElement element = retrieveWebElement();
                if (element != null) { stepSuccessful = element.isEnabled(); }
                if (stepSuccessful) { break; }
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stepSuccessful;
    }

    boolean verifyElementIsSelected() {

        boolean stepSuccessful = false;

        try {

            int waitTime = Integer.parseInt(dataValue);

            for (int seconds = 0; seconds < waitTime; seconds++) {

                WebElement element = retrieveWebElement();
                if (element != null) { stepSuccessful = element.isSelected(); }
                if (stepSuccessful) { break; }
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stepSuccessful;
    }

    boolean verifyElementIsNotDisplayed() {

        boolean stepSuccessful = false;

        try {

            WebElement element = retrieveWebElement();
            if (element == null) { return true; }

            boolean isDisplayed = element.isDisplayed();
            if (!isDisplayed) { stepSuccessful = true; }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stepSuccessful;
    }

    boolean verifyElementIsNotEnabled() {

        boolean stepSuccessful = false;

        try {

            WebElement element = retrieveWebElement();
            if (element == null) { return true; }

            boolean isEnabled = element.isEnabled();
            if (!isEnabled) { stepSuccessful = true; }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stepSuccessful;
    }

    boolean verifyElementIsNotSelected() {

        boolean stepSuccessful = false;

        try {

            WebElement element = retrieveWebElement();
            if (element == null) { return true; }

            boolean isSelected = element.isSelected();
            if (!isSelected) { stepSuccessful = true; }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stepSuccessful;
    }

    boolean verifyPageLoadIsComplete(String waitTimeInSeconds) {

        boolean loadComplete = false;

        try {

            String status = "complete";
            int waitTime = Integer.parseInt(waitTimeInSeconds) * 10;

            for (int i = 0; i < waitTime; i++) {

                loadComplete = pageReadyState().equalsIgnoreCase(status);
                if (loadComplete) { break; }
                Thread.sleep(100);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return loadComplete;
    }

    boolean findTextDisplayedOnPage() {

        boolean stepSuccessful = false;

        try {

            String bodyText = driver.findElement(By.tagName("body")).getText();

            if (bodyText.contains(dataValue)) {
                stepSuccessful = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stepSuccessful;
    }

    boolean takeScreenShot() {

        boolean stepSuccessful = false;

        try {

            String dateTimeStamp = common.getCurrentDateTime();
            screenshotName = dataValue + "-" + dateTimeStamp;
            String screenshotPath = screenshotDirectory + screenshotName  + ".png";

            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, new File(screenshotPath));
            stepSuccessful = true;

        } catch (Exception e) {
            System.out.println("---> Could not take screenshot.");
        }

        return stepSuccessful;
    }

    boolean acceptAlert() {

        boolean stepSuccessful = false;

        try {

            String windowHandle = driver.getWindowHandle();
            driver.switchTo().alert().accept();
            driver.switchTo().window(windowHandle);
            stepSuccessful = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stepSuccessful;
    }

    boolean dismissAlert() {

        boolean stepSuccessful = false;

        try {

            String windowHandle = driver.getWindowHandle();
            driver.switchTo().alert().dismiss();
            driver.switchTo().window(windowHandle);
            stepSuccessful = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stepSuccessful;
    }

    boolean matchAlertText() {

        boolean stepSuccessful = false;

        try {

            String windowHandle = driver.getWindowHandle();
            String text = driver.switchTo().alert().getText();
            driver.switchTo().window(windowHandle);
            stepSuccessful = text.equalsIgnoreCase(dataValue);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stepSuccessful;
    }

    boolean pressKey(String key) {

        boolean stepSuccessful = false;

        try {

            WebElement element = retrieveWebElement();
            if (element == null) { return true; }

            switch (key) {

                case "Press Enter":
                    element.sendKeys(Keys.ENTER);
                    stepSuccessful = true;
                    break;

                case "Press Tab":
                    element.sendKeys(Keys.TAB);
                    stepSuccessful = true;
                    break;

                case "Press Shift":
                    element.sendKeys(Keys.SHIFT);
                    stepSuccessful = true;
                    break;

                case "Press Space":
                    element.sendKeys(Keys.SPACE);
                    stepSuccessful = true;
                    break;

                case "Press Backspace":
                    element.sendKeys(Keys.BACK_SPACE);
                    stepSuccessful = true;
                    break;

                case "Press Pause":
                    element.sendKeys(Keys.PAUSE);
                    stepSuccessful = true;
                    break;

                case "Press Insert":
                    element.sendKeys(Keys.INSERT);
                    stepSuccessful = true;
                    break;

                case "Press Home":
                    element.sendKeys(Keys.HOME);
                    stepSuccessful = true;
                    break;

                case "Press Delete":
                    element.sendKeys(Keys.DELETE);
                    stepSuccessful = true;
                    break;

                case "Press End":
                    element.sendKeys(Keys.END);
                    stepSuccessful = true;
                    break;

                case "Press Escape":
                    element.sendKeys(Keys.ESCAPE);
                    stepSuccessful = true;
                    break;

                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stepSuccessful;
    }

    void quitBrowser() {

        try {
            driver.quit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //******************************************************************************************************************
    //*     Set Parameters
    //******************************************************************************************************************

    void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    void setIdentifierValue(String identifierValue) {
        this.identifierValue = identifierValue;
    }

    void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    String getScreenshotName() {
        return screenshotName;
    }
}

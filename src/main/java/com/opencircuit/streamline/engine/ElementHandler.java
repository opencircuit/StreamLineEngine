package com.opencircuit.streamline.engine;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

class ElementHandler {

    private final ThreadManager threadManager = new ThreadManager();
    private String identifierType;
    private String identifierValue;

    void setIdentifierType(String identifierType) {

        this.identifierType = identifierType;
    }

    void setIdentifierValue(String identifierValue) {

        this.identifierValue = identifierValue;
    }

    boolean typeText(String dataValue) {

        try {

            WebElement element = getWebElement();
            element.sendKeys(dataValue);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    boolean clearText() {

        try {

            WebElement element = getWebElement();
            element.clear();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    boolean matchTextboxText(String dataValue) {

        try {

            WebElement element = getWebElement();
            String value = element.getAttribute("value");
            return value.equalsIgnoreCase(dataValue);

        } catch (Exception e) {
            return false;
        }
    }

    boolean selectDropdownValueByVisibleText(String dataValue) {

        try {

            Select dropdown = getDropdown();
            dropdown.selectByVisibleText(dataValue);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    boolean selectDropdownValue(String dataValue) {

        try {

            Select dropdown = getDropdown();
            dropdown.selectByValue(dataValue);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    boolean selectDropdownValueByIndex(String dataValue) {

        try {

            Select dropdown = getDropdown();
            dropdown.selectByIndex(Integer.parseInt(dataValue));
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private Select getDropdown() {

        WebElement element = getWebElement();
        return new Select(element);
    }

    boolean selectRadioButtonByValue(String dataValue) {

        try {

            List<WebElement> elements = getWebElementsList();
            return selectRadioButtonByValueLoop(elements, dataValue);

        } catch (Exception e) {
            return false;
        }
    }

    private boolean selectRadioButtonByValueLoop(List<WebElement> elements, String dataValue) {

        for(WebElement element : elements) {

            if (element.getAttribute("value").equalsIgnoreCase(dataValue)) {
                element.click();
                return true;
            }
        }

        return false;
    }

    boolean selectRadioButtonByIndex(String dataValue) {

        try {

            List<WebElement> elements = getWebElementsList();
            elements.get(Integer.parseInt(dataValue)).click();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    boolean clickElement() {

        try {

            WebElement element = getWebElement();
            element.click();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    boolean matchTextInElement(String dataValue) {

        try {

            WebElement element = getWebElement();
            String text = element.getText();
            return text.equalsIgnoreCase(dataValue);

        } catch (Exception e) {
            return false;
        }
    }

    boolean verifyElementState(String state, String waitTime) {

        int seconds = threadManager.convertStringToInteger(waitTime);
        return verifyElementStateLoop(state, seconds);
    }

    private boolean verifyElementStateLoop(String state, int seconds) {

        for (int second = 0; second < seconds; second++) {

            if (verifySpecificElementState(state)) { return true; }
            else { threadManager.sleepThreadForSpecifiedSeconds(1); }
        }

        return false;
    }

    private boolean verifySpecificElementState(String state) {

        if (state.equalsIgnoreCase("Displayed")) {
            return verifyElementIsDisplayed();
        } else if (state.equalsIgnoreCase("Enabled")) {
            return verifyElementIsEnabled();
        } else {
            return verifyElementIsSelected();
        }
    }

    private boolean verifyElementIsDisplayed() {

        try {
            return getWebElement().isDisplayed();
        } catch(Exception e) {
            return false;
        }
    }

    private boolean verifyElementIsEnabled() {

        try {
            return getWebElement().isEnabled();
        } catch(Exception e) {
            return false;
        }
    }

    private boolean verifyElementIsSelected() {

        try {
            return getWebElement().isSelected();
        } catch(Exception e) {
            return false;
        }
    }

    boolean verifyElementIsNotDisplayed() {

        try {
            return !getWebElement().isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    boolean verifyElementIsNotEnabled() {

        try {
            return !getWebElement().isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    boolean verifyElementIsNotSelected() {

        try {
            return !getWebElement().isSelected();
        } catch (Exception e) {
            return false;
        }
    }

    boolean verifyPageLoadIsComplete(String waitTime) {

        int seconds = threadManager.convertStringToInteger(waitTime);
        return verifyPageLoadIsCompleteLoop(seconds);
    }

    private boolean verifyPageLoadIsCompleteLoop(int seconds) {

        for (int second = 0; second < seconds; second++) {

            if (pageLoadIsComplete()) { return true; }
            threadManager.sleepThreadForSpecifiedSeconds(1);
        }

        return false;
    }

    private boolean pageLoadIsComplete() {

        JavascriptExecutor js = (JavascriptExecutor) BrowserManager.driver;
        String readyState = (String) js.executeScript("return document.readyState");
        return readyState.equalsIgnoreCase("complete");
    }

    boolean findText(String text, String waitTime) {

        int seconds = threadManager.convertStringToInteger(waitTime);
        return findTextLoop(text, seconds);
    }

    private boolean findTextLoop(String text, int seconds) {

        for (int second = 0; second < seconds; second++) {

            if (findTextDisplayedOnPage(text)) { return true; }
            threadManager.sleepThreadForSpecifiedMilliseconds("100");
        }

        return false;
    }

    private boolean findTextDisplayedOnPage(String text) {

        try {
            return BrowserManager.driver.findElement(By.tagName("body")).getText().contains(text);
        } catch (Exception e) {
            return false;
        }
    }

    boolean acceptAlert() {

        try {

            String windowHandle = BrowserManager.driver.getWindowHandle();
            BrowserManager.driver.switchTo().alert().accept();
            BrowserManager.driver.switchTo().window(windowHandle);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    boolean dismissAlert() {

        try {

            String windowHandle = BrowserManager.driver.getWindowHandle();
            BrowserManager.driver.switchTo().alert().dismiss();
            BrowserManager.driver.switchTo().window(windowHandle);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    boolean matchAlertText(String dataValue) {

        try {

            String windowHandle = BrowserManager.driver.getWindowHandle();
            String text = BrowserManager.driver.switchTo().alert().getText();
            BrowserManager.driver.switchTo().window(windowHandle);
            return text.equalsIgnoreCase(dataValue);

        } catch (Exception e) {
            return false;
        }
    }

    boolean pressKey(String key) {

        try {

            WebElement element = getWebElement();
            if (element == null) { return false; }
            return simulateKeyPress(key, element);

        } catch (Exception e) {
            return false;
        }
    }

    private boolean simulateKeyPress(String key, WebElement element) {

        boolean keyPressed;

        switch (key) {

            case "Press Enter":
                element.sendKeys(Keys.ENTER);
                keyPressed = true;
                break;

            case "Press Tab":
                element.sendKeys(Keys.TAB);
                keyPressed = true;
                break;

            case "Press Shift":
                element.sendKeys(Keys.SHIFT);
                keyPressed = true;
                break;

            case "Press Space":
                element.sendKeys(Keys.SPACE);
                keyPressed = true;
                break;

            case "Press Backspace":
                element.sendKeys(Keys.BACK_SPACE);
                keyPressed = true;
                break;

            case "Press Pause":
                element.sendKeys(Keys.PAUSE);
                keyPressed = true;
                break;

            case "Press Insert":
                element.sendKeys(Keys.INSERT);
                keyPressed = true;
                break;

            case "Press Home":
                element.sendKeys(Keys.HOME);
                keyPressed = true;
                break;

            case "Press Delete":
                element.sendKeys(Keys.DELETE);
                keyPressed = true;
                break;

            case "Press End":
                element.sendKeys(Keys.END);
                keyPressed = true;
                break;

            case "Press Escape":
                element.sendKeys(Keys.ESCAPE);
                keyPressed = true;
                break;

            default:
                keyPressed = false;
                break;
        }

        return keyPressed;
    }

    private WebElement getWebElement() {

        WebElement element;

        try {

            switch (identifierType.toLowerCase()) {

                case "id":
                    element = BrowserManager.driver.findElement(By.id(identifierValue));
                    break;

                case "name":
                    element = BrowserManager.driver.findElement(By.name(identifierValue));
                    break;

                case "link-text":
                    element = BrowserManager.driver.findElement(By.linkText(identifierValue));
                    break;

                case "partial-link-text":
                    element = BrowserManager.driver.findElement(By.partialLinkText(identifierValue));
                    break;

                case "tag-name":
                    element = BrowserManager.driver.findElement(By.tagName(identifierValue));
                    break;

                case "class-name":
                    element = BrowserManager.driver.findElement(By.className(identifierValue));
                    break;

                case "css-selector":
                    element = BrowserManager.driver.findElement(By.cssSelector(identifierValue));
                    break;

                case "xpath":
                    element = BrowserManager.driver.findElement(By.xpath(identifierValue));
                    break;

                default:
                    element = null;
                    break;
            }

        } catch (Exception e) {
            element = null;
        }

        return element;
    }

    private List<WebElement> getWebElementsList() {

        List<WebElement> elements;

        try {

            switch (identifierType.toLowerCase()) {

                case "id":
                    elements = BrowserManager.driver.findElements(By.id(identifierValue));
                    break;

                case "name":
                    elements = BrowserManager.driver.findElements(By.name(identifierValue));
                    break;

                case "link-text":
                    elements = BrowserManager.driver.findElements(By.linkText(identifierValue));
                    break;

                case "partial-link-text":
                    elements = BrowserManager.driver.findElements(By.partialLinkText(identifierValue));
                    break;

                case "tag-name":
                    elements = BrowserManager.driver.findElements(By.tagName(identifierValue));
                    break;

                case "class-name":
                    elements = BrowserManager.driver.findElements(By.className(identifierValue));
                    break;

                case "css-selector":
                    elements = BrowserManager.driver.findElements(By.cssSelector(identifierValue));
                    break;

                case "xpath":
                    elements = BrowserManager.driver.findElements(By.xpath(identifierValue));
                    break;

                default:
                    elements = null;
                    break;
            }

        } catch (Exception e) {
            elements = null;
        }

        return elements;
    }
}
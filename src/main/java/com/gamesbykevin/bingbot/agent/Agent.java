package com.gamesbykevin.bingbot.agent;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.List;

import static com.gamesbykevin.bingbot.agent.AgentHelper.*;
import static com.gamesbykevin.bingbot.util.LogFile.displayMessage;

public abstract class Agent {

    private static final String DRIVER_PROPERTY = "webdriver.chrome.driver";

    //our bing login
    public static String BING_LOGIN_USERNAME;
    public static String BING_LOGIN_PASSWORD;

    //where we start
    public static String BING_HOMEPAGE;

    //where we perform our search
    public static String BING_SEARCH_URL;

    //how many searches do we perform before resting
    public static int BING_SEARCH_LIMIT;

    //where is the google chrome selenium driver
    public static String CHROME_DRIVER_LOCATION;

    //our user agent for mobile browsing
    public static String USER_AGENT_MOBILE;

    //our rewards page with extra bonus point options
    public static String BING_REWARDS_PAGE;

    //our object used to interact with the web pages
    private WebDriver driver;

    /**
     * Create our agent
     * @param mobile Is this a desktop browser or mobile browser?
     */
    public Agent(boolean mobile) {

        displayMessage("*******************");
        displayMessage("Launching browser with driver: " + CHROME_DRIVER_LOCATION);
        System.setProperty(DRIVER_PROPERTY, CHROME_DRIVER_LOCATION);

        //create our new chrome options
        ChromeOptions options = new ChromeOptions();

        //spoof the browser we are using when testing mobile
        if (mobile)
            options.addArguments("--user-agent=" + USER_AGENT_MOBILE);

        //add headless so the browser can run in the background without a gui
        options.addArguments("--headless");

        //needed to start chrome without errors
        options.addArguments("--no-sandbox");

        //create our driver with the specified options
        this.driver = new ChromeDriver(options);

        //wait a moment
        pause();
    }

    /**
     * Navigate to the login so we can begin entering our credentials
     */
    public abstract void navigateToLogin();

    /**
     * Get our bing points
     * @return The total bing points acquired
     */
    public abstract int getPoints();

    public void openWebPage(String url, String message) {

        displayMessage(message);

        try {

            //load the bing homepage
            getDriver().get(url);

        } catch (Exception e) {
            displayMessage(e);
        }

        //wait a moment
        pause();
    }

    public void openHomePage() {
        openWebPage(BING_HOMEPAGE, "Opening homepage");
        pause();
    }

    private void openBingRewardsPage() {
        openWebPage(BING_REWARDS_PAGE, "Opening bing rewards page");
    }

    public void performSearch() {

        //create our url search string
        final String url = String.format(BING_SEARCH_URL, getRandomWord());

        //open page with custom data
        openWebPage(url, "Search url: " + url);
    }

    public void clickingExtraRewardLinks() {

        //open bing rewards page
        openBingRewardsPage();

        //locate elements by anchor tag
        By anchorTag = By.tagName("a");

        //get total elements found for us to check
        List<WebElement> elements = getDriver().findElements(anchorTag);

        //how many links do we have
        int count = elements.size();
        displayMessage("We found " + count + " potential extra reward links");

        //loop through each element
        for (int index = 0; index < count; index++) {

            try {

                //open bing rewards page
                openBingRewardsPage();

                //get a list of all the elements on the page
                elements = getDriver().findElements(anchorTag);

                //were we successful clicking the link
                boolean result = false;

                //get the current list of tabs
                ArrayList<String> tabs1 = new ArrayList<>(getDriver().getWindowHandles());

                //continue to check the links until we are able to click one successfully
                while (!result) {

                    //checking each link
                    displayMessage("Checking " + index + " of " + count);

                    WebElement element = null;

                    //get the current element in the list as long as we are in range
                    if (index < elements.size()) {
                        element = elements.get(index);
                    } else {
                        break;
                    }

                    //attempt to click the link
                    result = clickBonusLink(element);

                    //recycle
                    element = null;

                    //if we aren't successful go to the next element
                    if (!result)
                        index++;
                }

                //get the current list of tabs
                ArrayList<String> tabs2 = new ArrayList<>(getDriver().getWindowHandles());

                //if a new tab was opened, close it
                if (tabs1.size() != tabs2.size()) {

                    displayMessage("New tab was opened, so we will close it");

                    //switch to the new tab
                    getDriver().switchTo().window(tabs2.get(tabs2.size() - 1));

                    //close the tab
                    getDriver().close();

                    //move back to the other tab
                    getDriver().switchTo().window(tabs2.get(0));
                }

            } catch (Exception e) {
                displayMessage(e);
            }
        }

        elements.clear();
        elements = null;
    }

    private boolean clickBonusLink(WebElement element) {

        //if null we can't click it
        if (element == null)
            return false;

        //if not displayed we can't click it
        if (!element.isDisplayed())
            return false;

        //click links with words
        if (element.getText() == null || element.getText().trim().length() < 1)
            return false;

        //don't click remove goal link
        if (element.getText().toUpperCase().contains("REMOVE") ||
                element.getText().toUpperCase().contains("REDEEM"))
            return false;

        //get the populated value for the class of this web element
        String value = element.getAttribute("class");

        //all links we want to click have specific values in the class attribute
        if (value == null || value.trim().length() < 1)
            return false;
        if (!value.toLowerCase().contains("c-call-to-action"))
            return false;
        if (!value.toLowerCase().contains("c-glyph"))
            return false;
        if (!value.toLowerCase().contains("f-lightweight"))
            return false;

        //notify user
        displayMessage("Clicking link: " + element.getText());

        //we know the element is good now, so we can click it
        element.click();

        //sleep for a short time
        pause();

        //we have success
        return true;
    }

    public void enterLogin() {

        displayMessage("Entering login");

        //obtain our text field
        WebElement element = getWebElement(getDriver(), By.id("i0116"));

        //enter our user name
        element.sendKeys(BING_LOGIN_USERNAME);

        //click the "Next" button
        clickLink(getDriver(), By.id("idSIButton9"), "Clicking \"Next\"");
    }

    public void enterPassword() {

        displayMessage("Entering password");

        //find the password text field
        WebElement element = getWebElement(getDriver(), By.id("i0118"));

        //enter our password
        element.sendKeys(BING_LOGIN_PASSWORD);

        //click the "Sign In" button
        clickLink(getDriver(), By.id("idSIButton9"), "Clicking \"Sign In\"");
    }

    protected int parsePoints(WebElement element) {

        int points = 0;

        if (element != null) {

            //continue to check until we have found our points
            while (element.getText().length() < 1 || element.getText().equalsIgnoreCase("0")) {

                try {
                    displayMessage("Checking...");
                    pause();
                } catch (Exception e) {
                    displayMessage(e);
                }
            }

            try {

                //obtain our points
                points = Integer.parseInt(element.getText());

                //display points total
                displayMessage("Points: " + points);

            } catch (Exception e) {
                displayMessage(e);
            }
        }

        return points;
    }

    protected WebDriver getDriver() {
        return this.driver;
    }

    public void recycle() {

        if (this.driver != null) {

            try {
                displayMessage("Closing browser");
                this.driver.quit();
            } catch (Exception e) {
                displayMessage(e);
            }

            this.driver = null;
        }
    }
}
package com.gamesbykevin.bingbot.agent;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;

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

        //get total elements found for us to click
        int count = getDriver().findElements(anchorTag).size();
        displayMessage("We found " + count + " potential extra reward links");

        //loop through each element
        for (int i = 0; i < count; i++) {

            try {

                //get a list of tabs
                ArrayList<String> tabs1 = new ArrayList<>(getDriver().getWindowHandles());

                //get the current element in the list
                WebElement element = getDriver().findElements(anchorTag).get(i);

                //display progress
                displayMessage("Checking link " + (i+1) + " of " + count);

                //we can't click the link if it isn't displayed
                if (element.isDisplayed()) {

                    //don't click remove goal link
                    if (element.getText() != null && element.getText().equalsIgnoreCase("REMOVE GOAL"))
                        continue;

                    //get the populated value for the class of this web element
                    String value = element.getAttribute("class");

                    //all links we want to click have specific values in the class attribute
                    if (value == null || value.trim().length() < 1 || !value.contains("c-call-to-action"))
                        continue;

                    //display progress
                    displayMessage("Clicking link with text \"" + element.getText() + "\"");

                    //click the element
                    element.click();

                    //wait a moment
                    pause();

                    //get a list of tabs
                    ArrayList<String> tabs2 = new ArrayList<>(getDriver().getWindowHandles());

                    //if there is a new tab we need to close it and switch back to the rewards page
                    if (tabs2.size() > tabs1.size()) {

                        //close the new tab
                        getDriver().switchTo().window(tabs2.get(1));
                        getDriver().close();

                        //then we switch back to our first tab
                        displayMessage("Switching tabs");
                        getDriver().switchTo().window(tabs2.get(0));

                    } else {

                        //since no tabs was opened we need to open the bing rewards page again
                        openBingRewardsPage();
                    }
                }

            } catch (Exception e) {
                displayMessage(e);
            }
        }
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

    public void closeBrowser() {
        displayMessage("Closing browser");
        getDriver().quit();
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
                this.driver.quit();
            } catch (Exception e) {
                displayMessage(e);
            }

            this.driver = null;
        }
    }
}
package com.gamesbykevin.bingbot.agent;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.List;

import static com.gamesbykevin.bingbot.Main.displayMessage;
import static com.gamesbykevin.bingbot.agent.AgentHelper.*;

public class Agent {

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

    //spoof our user agent for mobile
    private ChromeOptions options;

    /**
     * Default constructor
     */
    public Agent() {

        //do anything here?
    }

    public ChromeOptions getOptions(boolean mobile) {

        //create our new chrome options
        this.options = new ChromeOptions();

        //spoof the browser we are using when testing mobile
        if (mobile)
            this.options.addArguments("--user-agent=" + USER_AGENT_MOBILE);

        //add headless so the browser can run in the background without a gui
        this.options.addArguments("--headless");

        //needed to start chrome without errors
        this.options.addArguments("--no-sandbox");

        //return our object
        return this.options;
    }

    public void createDriver(final boolean mobile) {

        displayMessage("*******************");
        displayMessage("Launching browser with driver: " + CHROME_DRIVER_LOCATION);
        System.setProperty(DRIVER_PROPERTY, CHROME_DRIVER_LOCATION);

        //create our driver with the specified options
        this.driver = new ChromeDriver(getOptions(mobile));

        //wait a moment
        pause();
    }

    public void openHomePage() {

        displayMessage("Opening homepage");

        //load the bing homepage
        getDriver().get(BING_HOMEPAGE);

        //wait a moment
        pause();
    }

    public void clickCloseBingAppPromo() {
        clickLink(getDriver(), By.cssSelector(".closeIcon.rms_img"), "Clicking close promo link", true);
    }

    private void openBingRewardsPage() {

        displayMessage("Opening bing rewards page");

        //load the bing homepage
        getDriver().get(BING_REWARDS_PAGE);

        //wait a moment
        pause();
    }

    public void clickingExtraRewardLinks() {

        //open bing rewards page
        openBingRewardsPage();

        //get list of elements to click
        List<WebElement> elements = getWebElements(getDriver(), By.cssSelector(".rewards-card-container"));
        //List<WebElement> elements = getWebElements(By.cssSelector(".ng-scope.c-call-to-action.c-glyph.f-lightweight"));
        //List<WebElement> elements = getWebElements(By.className("rewards-card-container"));

        displayMessage("We found " + elements.size() + " potential extra reward links");

        //loop through each element
        for (int i = 0; i < elements.size(); i++) {

            try {

                //wait a moment
                pause();

                //get a list of tabs
                ArrayList<String> tabs1 = new ArrayList<>(getDriver().getWindowHandles());

                //get the list of all the elements
                elements = getWebElements(getDriver(), By.cssSelector(".rewards-card-container"));

                //get the current element
                WebElement element = elements.get(i);

                //click the link
                if (element.isDisplayed()) {

                    //click the element
                    element.click();

                    //wait a moment
                    pause();

                    //get a list of tabs
                    ArrayList<String> tabs2 = new ArrayList<>(getDriver().getWindowHandles());

                    //if there are more tabs, a new one was opened
                    if (tabs2.size() > tabs1.size()) {

                        //our bing rewards page should always be the first tab
                        getDriver().switchTo().window(tabs2.get(0));

                    } else {

                        //go back a page
                        getDriver().navigate().back();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void clickHamburgerMenuMobile() {
        clickLink(getDriver(), By.id("mHamburger"), "Clicking hamburger menu");
    }

    public void clickSigninMobile() {
        clickLink(getDriver(), By.id("hb_a"), "Clicking sign in link");
    }

    public void clickConnect() {
        clickLink(getDriver(), By.className("b_toggle"), "Clicking connect", true);
    }

    public void clickLogin() {
        clickLink(getDriver(), By.className("id_button"), "Clicking login");
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

    public int getPoints(final boolean mobile) {

        //for mobile we need to expand the hamburger menu to obtain the points
        if (mobile)
            clickHamburgerMenuMobile();

        displayMessage("Obtaining points");

        //wait a moment
        pause();

        //obtain the element containing our points
        WebElement element;

        //mobile has a different id to obtain points
        if (mobile) {
            element = getWebElement(getDriver(), By.id("fly_id_rc"));
        } else {
            element = getWebElement(getDriver(), By.id("id_rc"));
        }

        //continue to check until we have found our points
        while (element.getText().length() < 1 || element.getText().equalsIgnoreCase("0")) {

            try {
                displayMessage("Checking...");
                pause();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int points = 0;

        try {

            //obtain our points
            points = Integer.parseInt(element.getText());

            //display points total
            displayMessage("Points: " + points);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //return the points found
        return points;
    }

    public void performSearch() {

        displayMessage("Performing search");

        //create our url search string
        final String url = String.format(BING_SEARCH_URL, getRandomWord());

        //display our created url
        displayMessage("Search url: " + url);

        //perform the search
        getDriver().get(url);

        //wait a moment
        pause();
    }

    public void closeBrowser() {
        displayMessage("Closing browser");
        getDriver().quit();
    }

    private WebDriver getDriver() {
        return this.driver;
    }

    public void recycle() {

        if (this.options != null)
            this.options = null;

        if (this.driver != null) {

            try {
                this.driver.quit();
            } catch (Exception e) {
                e.printStackTrace();
            }

            this.driver = null;
        }
    }
}
package com.gamesbykevin.bingbot.agent;

import org.openqa.selenium.By;

import static com.gamesbykevin.bingbot.agent.AgentHelper.clickLink;
import static com.gamesbykevin.bingbot.agent.AgentHelper.getWebElement;
import static com.gamesbykevin.bingbot.agent.AgentHelper.pause;
import static com.gamesbykevin.bingbot.util.LogFile.displayMessage;

public class MobileAgent extends Agent {

    private static final String ID_CONTAINER_POINTS = "fly_id_rc";

    public MobileAgent() {
        super(true);
    }

    @Override
    public void navigateToLogin() {

        //close the bing app promo
        clickLink(getDriver(), By.cssSelector(".closeIcon.rms_img"), "Clicking close promo link");

        //select the hamburger menu
        clickHamburgerMenuMobile();

        //click on "sign in"
        clickLink(getDriver(), By.id("hb_a"), "Clicking sign in link");
    }

    private void clickHamburgerMenuMobile() {
        clickLink(getDriver(), By.id("mHamburger"), "Clicking hamburger menu");
    }

    @Override
    public int getPoints() {

        displayMessage("Obtaining points");

        //open the home page where our points are located
        openHomePage();

        //expand the hamburger menu to obtain the points
        clickHamburgerMenuMobile();

        //wait a moment
        pause();

        //return the points found
        return parsePoints(getWebElement(getDriver(), By.id(ID_CONTAINER_POINTS)));
    }
}
package com.gamesbykevin.bingbot.agent;

import org.openqa.selenium.By;

import static com.gamesbykevin.bingbot.agent.AgentHelper.clickLink;
import static com.gamesbykevin.bingbot.agent.AgentHelper.getWebElement;
import static com.gamesbykevin.bingbot.agent.AgentHelper.pause;
import static com.gamesbykevin.bingbot.util.LogFile.displayMessage;

public class DesktopAgent extends Agent {

    private static final String ID_CONTAINER_POINTS = "id_rc";

    public DesktopAgent() {
        super(false);
    }

    @Override
    public void navigateToLogin() {

        //click the login button
        clickLink(getDriver(), By.className("id_button"), "Clicking login");

        //select the account we want to login as
        clickLink(getDriver(), By.className("b_toggle"), "Clicking connect");
    }

    @Override
    public int getPoints() {

        displayMessage("Obtaining points");

        //open the home page where our points are located
        openHomePage();

        //return the points found
        return parsePoints(getWebElement(getDriver(), By.id(ID_CONTAINER_POINTS)));
    }
}
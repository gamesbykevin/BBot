package com.gamesbykevin.bingbot;

import com.gamesbykevin.bingbot.agent.Agent;
import com.gamesbykevin.bingbot.agent.DesktopAgent;
import com.gamesbykevin.bingbot.agent.MobileAgent;

import static com.gamesbykevin.bingbot.util.LogFile.displayMessage;

public class MainHelper {

    protected static void runBonusProgram() {

        displayMessage("Checking for extra reward point links...");

        //create a new agent
        Agent agent = new DesktopAgent();

        //login
        login(agent);

        //check for extra points
        agent.clickingExtraRewardLinks();

        //close the browser
        agent.closeBrowser();

        //clean up variables
        agent.recycle();
        agent = null;
    }

    protected static int runSearchProgram(final boolean mobile) {

        //number of points found
        int result = 0;

        //how we will navigate the web
        Agent agent;

        //create a new agent for browsing
        if (mobile) {
            agent = new MobileAgent();
        } else {
            agent = new DesktopAgent();
        }

        //login
        login(agent);

        //load the home page
        agent.openHomePage();

        //perform the desired number of searches
        for (int i = 1; i <= Agent.BING_SEARCH_LIMIT; i++) {

            displayMessage("Searching... #" + i);

            //perform the search
            agent.performSearch();

            //open the home page
            agent.openHomePage();

            //retrieve our points
            result = agent.getPoints();
        }

        //close the browser
        agent.closeBrowser();

        //clean up our variables
        agent.recycle();
        agent = null;

        //return our result
        return result;
    }

    private static void login(Agent agent) {

        //load the home page
        agent.openHomePage();

        //go to the login page to enter credentials
        agent.navigateToLogin();

        //enter our login
        agent.enterLogin();

        //enter our password
        agent.enterPassword();
    }
}
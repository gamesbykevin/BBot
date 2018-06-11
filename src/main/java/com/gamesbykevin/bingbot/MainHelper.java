package com.gamesbykevin.bingbot;

import com.gamesbykevin.bingbot.agent.Agent;
import com.gamesbykevin.bingbot.agent.DesktopAgent;
import com.gamesbykevin.bingbot.agent.MobileAgent;

import static com.gamesbykevin.bingbot.util.LogFile.displayMessage;

public class MainHelper {

    protected static int getPoints() {

        int points = 0;

        //how we will navigate the web
        Agent agent = null;

        try {

            displayMessage("Checking total points ...");

            //create a new agent
            agent = new DesktopAgent();

            //login
            login(agent);

            //get our points
            points = agent.getPoints();

            //clean up our objects
            if (agent != null)
                agent.recycle();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            agent = null;
        }

        //return our points
        return points;
    }

    protected static void runBonusProgram() {

        //how we will navigate the web
        Agent agent = null;

        try {

            displayMessage("Checking for extra reward point links...");

            //create a new agent
            agent = new DesktopAgent();

            //login
            login(agent);

            //check for extra points
            agent.clickingExtraRewardLinks();

        } catch (Exception e) {

            e.printStackTrace();

        }

        //clean up our objects
        if (agent != null)
            agent.recycle();

        agent = null;
    }

    protected static void runSearchProgram(final boolean mobile) {

        //how we will navigate the web
        Agent agent = null;

        try {

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

                //load the home page
                agent.openHomePage();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //clean up our objects
        if (agent != null)
            agent.recycle();

        agent = null;
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
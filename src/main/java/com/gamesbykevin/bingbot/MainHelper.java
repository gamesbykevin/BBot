package com.gamesbykevin.bingbot;

import com.gamesbykevin.bingbot.agent.Agent;
import com.gamesbykevin.bingbot.agent.DesktopAgent;
import com.gamesbykevin.bingbot.agent.MobileAgent;

import static com.gamesbykevin.bingbot.util.LogFile.displayMessage;

public class MainHelper {

    protected static int runBonusProgram() {

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

        //clean up our objects and return our points
        return cleanup(agent);
    }

    protected static int runSearchProgram(final boolean mobile) {

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

        //clean up our objects and return our points
        return cleanup(agent);
    }

    private static int cleanup(Agent agent) {

        int result = 0;

        //clean up
        try {

            if (agent != null) {

                //retrieve our points before we recycle
                result = agent.getPoints();

                //recycle object(s)
                agent.recycle();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //recycle
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
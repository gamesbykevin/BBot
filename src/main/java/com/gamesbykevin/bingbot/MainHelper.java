package com.gamesbykevin.bingbot;

import com.gamesbykevin.bingbot.agent.Agent;

import static com.gamesbykevin.bingbot.util.LogFile.displayMessage;

public class MainHelper {

    protected static int runProgram(final boolean mobile) {

        //number of points found
        int result = 0;

        //create a new agent for browsing
        Agent agent = new Agent(mobile);

        //load the home page
        agent.openHomePage();

        //mobile login is slightly different
        if (mobile) {

            //close the bing app promo
            agent.clickCloseBingAppPromo();

            //select the hamburger menu
            agent.clickHamburgerMenuMobile();

            //click on "sign in"
            agent.clickSigninMobile();

        } else {

            //click the login button
            agent.clickLogin();

            //select the account we want to login as
            agent.clickConnect();
        }

        //enter our login
        agent.enterLogin();

        //enter our password
        agent.enterPassword();

        if (!mobile) {

            displayMessage("Checking for extra reward point links...");

            //check for extra points
            agent.clickingExtraRewardLinks();
        }

        //load the home page
        agent.openHomePage();

        //perform the desired number of searches
        for (int i = 0; i < Agent.BING_SEARCH_LIMIT; i++) {

            displayMessage("Searching... " + (i+1));

            //perform the search
            agent.performSearch();

            //open the home page
            agent.openHomePage();

            //retrieve our points
            result = agent.getPoints(mobile);
        }

        //close the browser
        agent.closeBrowser();

        //clean up our variables
        agent.recycle();
        agent = null;

        //return our result
        return result;
    }
}
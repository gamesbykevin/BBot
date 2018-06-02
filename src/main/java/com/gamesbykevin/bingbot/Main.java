package com.gamesbykevin.bingbot;

import com.gamesbykevin.bingbot.agent.Agent;
import com.gamesbykevin.bingbot.util.Email;
import com.gamesbykevin.bingbot.util.PropertyUtil;

import java.util.concurrent.TimeUnit;

public class Main extends Thread {

    //our browser agent
    private Agent agent;

    //how long do we sleep our thread
    public static final long THREAD_DELAY = 1000L;

    //how long does the bot sleep (in minutes);
    public static long SLEEP_BOT;

    //how many milliseconds per minute
    private static final long MILLIS_PER_MINUTE = (1000 * 60);

    public static void main(String[] args) {

        try {

            //load our properties
            displayMessage("Loading properties: " + PropertyUtil.PROPERTY_FILE);
            PropertyUtil.loadProperties();

            //start our thread
            Main main = new Main();
            main.start();

            //process has begun
            displayMessage("Bingbot started...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Main() {
        this.agent = new Agent();
    }

    public Agent getAgent() {
        return this.agent;
    }

    @Override
    public void run() {

        //we need to keep track of the previous time the bot has run
        long previous = System.currentTimeMillis() - (MILLIS_PER_MINUTE * SLEEP_BOT);

        while (true) {

            try {

                //milliseconds remaining until we can run again
                final long remaining = (MILLIS_PER_MINUTE * SLEEP_BOT) - (System.currentTimeMillis() - previous);

                //if there is no time remaining we can run again
                if (remaining <= 0) {

                    //run our program in our typical chrome browser
                    runProgram(false);

                    //run our program one again, but spoofing a mobile browser
                    final int points = runProgram(true);

                    //store the new time since our last successful run
                    previous = System.currentTimeMillis();

                    //send email that we are done
                    Email.send("Bing bot completed", "Points: " + points);

                } else {
                    displayMessage("Bot sleeping will run again in " + TimeUnit.MILLISECONDS.toSeconds(remaining) + " seconds.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

            try {
                //sleep for a short time
                Thread.sleep(THREAD_DELAY);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private int runProgram(final boolean mobile) {

        //number of points found
        int result = 0;

        //create a new web driver for browsing
        getAgent().createDriver(mobile);

        //load the home page
        getAgent().openHomePage();

        //mobile login is slightly different
        if (mobile) {

            //close the bing app promo
            getAgent().clickCloseBingAppPromo();

            //select the hamburger menu
            getAgent().clickHamburgerMenuMobile();

            //click on "sign in"
            getAgent().clickSigninMobile();

        } else {

            //click the login button
            getAgent().clickLogin();

            //select the account we want to login as
            getAgent().clickConnect();
        }

        //enter our login
        getAgent().enterLogin();

        //enter our password
        getAgent().enterPassword();

        if (!mobile) {

            displayMessage("Checking for extra reward point links...");

            //check for extra points
            getAgent().clickingExtraRewardLinks();
        }

        //load the home page
        getAgent().openHomePage();

        //perform the desired number of searches
        for (int i = 0; i < Agent.BING_SEARCH_LIMIT; i++) {

            displayMessage("Searching... " + (i+1));

            //perform the search
            getAgent().performSearch();

            //open the home page
            getAgent().openHomePage();

            //retrieve our points
            result = getAgent().getPoints(mobile);
        }

        //close the browser
        getAgent().closeBrowser();

        //clean up our variables
        getAgent().recycle();

        //return our result
        return result;
    }

    public static void displayMessage(final String message) {
        System.out.println(message);
    }
}
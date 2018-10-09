package com.gamesbykevin.bingbot.util;

import com.gamesbykevin.bingbot.Main;
import com.gamesbykevin.bingbot.agent.Agent;
import com.gamesbykevin.bingbot.agent.AgentHelper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.gamesbykevin.bingbot.util.LogFile.displayMessage;

public class Properties {

    //the name / location of our property file
    public static final String PROPERTY_FILE = "application.properties";

    //property object to access our properties
    private static java.util.Properties PROPERTIES;

    //are we debugging (true if running in IDE, false otherwise)
    public static boolean DEBUG = true;

    public static java.util.Properties getProperties() {

        if (PROPERTIES == null) {

            PROPERTIES = new java.util.Properties();

            try {

                try {

                    //call this when you create an executable .jar and run in production (place the property file in the same directory)
                    PROPERTIES.load(new FileInputStream(PROPERTY_FILE));

                    //if success we aren't debugging
                    DEBUG = false;

                } catch (FileNotFoundException e) {

                    //if file is not found, look in this project for the property file
                    PROPERTIES.load(Main.class.getClassLoader().getResourceAsStream(PROPERTY_FILE));

                    //if file doesn't exist, we are debugging
                    DEBUG = true;
                }

            } catch(Exception ex) {

                //display error message
                ex.printStackTrace();

                //exit app if we can't load a property file
                System.exit(10);
            }
        }

        return PROPERTIES;
    }

    public static void load() {

        //grab the email address from our config
        Email.EMAIL_NOTIFICATION_ADDRESS = getProperties().getProperty("emailNotification");

        //our gmail login we need so we have an smtp server to send emails
        Email.GMAIL_SMTP_USERNAME = getProperties().getProperty("gmailUsername");
        Email.GMAIL_SMTP_PASSWORD = getProperties().getProperty("gmailPassword");

        //our bing login credentials so our account is credited
        Agent.BING_LOGIN_USERNAME = getProperties().getProperty("bingLoginUsername");
        Agent.BING_LOGIN_PASSWORD = getProperties().getProperty("bingLoginPassword");

        //where do we start at
        Agent.BING_HOMEPAGE = getProperties().getProperty("bingHomepage");

        //where do we perform the search
        Agent.BING_SEARCH_URL = getProperties().getProperty("bingSearchUrl");

        //page where we can earn additional points
        Agent.BING_REWARDS_PAGE = getProperties().getProperty("bingRewardsPage");

        //what user agent do we want to use
        Agent.USER_AGENT_MOBILE = getProperties().getProperty("chromUserAgentMobile");

        //minimum time we wait to perform our next action
        AgentHelper.PAUSE_DELAY_MIN = Long.parseLong(getProperties().getProperty("pauseDelayMin"));

        //additional time on the minimum delay
        AgentHelper.PAUSE_DELAY_RANGE = Long.parseLong(getProperties().getProperty("pauseDelayRange"));

        //how many searches do we perform before resting
        Agent.BING_SEARCH_LIMIT = Integer.parseInt(getProperties().getProperty("bingSearchLimit"));

        //we need to find the location of the driver so we can interact with the web pages
        if (DEBUG) {
            displayMessage("Loading chrome driver (windows)");
            Agent.CHROME_DRIVER_LOCATION = getProperties().getProperty("chromeDriverLocationWindows");
        } else {
            displayMessage("Loading chrome driver (linux)");
            Agent.CHROME_DRIVER_LOCATION = getProperties().getProperty("chromeDriverLocationLinux");
        }

        //we won't allow any time less than 1 second
        if (AgentHelper.PAUSE_DELAY_MIN < 1000)
            AgentHelper.PAUSE_DELAY_MIN = 1000;

        //we won't allow any time less than 1 second
        if (AgentHelper.PAUSE_DELAY_RANGE < 1000)
            AgentHelper.PAUSE_DELAY_RANGE = 1000;

        //let's perform at least 1 search
        if (Agent.BING_SEARCH_LIMIT < 1)
            Agent.BING_SEARCH_LIMIT = 1;
    }
}
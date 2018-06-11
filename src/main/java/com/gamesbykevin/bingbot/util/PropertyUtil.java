package com.gamesbykevin.bingbot.util;

import com.gamesbykevin.bingbot.Main;
import com.gamesbykevin.bingbot.agent.Agent;
import com.gamesbykevin.bingbot.agent.AgentHelper;

import java.io.FileInputStream;
import java.util.Properties;

public class PropertyUtil {

    public static final String PROPERTY_FILE = "./application.properties";

    private static Properties PROPERTIES;

    public static final boolean DEBUG = true;

    public static Properties getProperties() {

        if (PROPERTIES == null) {

            PROPERTIES = new Properties();

            try {

                if (DEBUG) {

                    //call this when running the project in intellij
                    PROPERTIES.load(Main.class.getClassLoader().getResourceAsStream(PROPERTY_FILE));

                } else {

                    //call this when you create an executable .jar and place the application.properties file in the same directory as the .jar
                    PROPERTIES.load(new FileInputStream(PROPERTY_FILE));

                }

            } catch(Exception ex) {
                ex.printStackTrace();
                System.exit(10);
            }
        }

        return PROPERTIES;
    }

    public static void loadProperties() {

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

        //how long does the bot sleep
        Main.SLEEP_BOT = Long.parseLong(getProperties().getProperty("sleep"));

        //minimum time we wait to perform our next action
        AgentHelper.PAUSE_DELAY_MIN = Long.parseLong(getProperties().getProperty("pauseDelayMin"));

        //additional time on the minimum delay
        AgentHelper.PAUSE_DELAY_RANGE = Long.parseLong(getProperties().getProperty("pauseDelayRange"));

        //how many searches do we perform before resting
        Agent.BING_SEARCH_LIMIT = Integer.parseInt(getProperties().getProperty("bingSearchLimit"));

        //where is the driver so we can interact with the web pages
        Agent.CHROME_DRIVER_LOCATION = getProperties().getProperty("chromeDriverLocation");

        //we won't allow any time less than 1 second
        if (AgentHelper.PAUSE_DELAY_MIN < 1000)
            AgentHelper.PAUSE_DELAY_MIN = 1000;

        //we won't allow any time less than 1 second
        if (AgentHelper.PAUSE_DELAY_RANGE < 1000)
            AgentHelper.PAUSE_DELAY_RANGE = 1000;

        //let's maintain a minimum of 1 minute
        if (Main.SLEEP_BOT < 1)
            Main.SLEEP_BOT = 1;

        //let's perform at least 1 search
        if (Agent.BING_SEARCH_LIMIT < 1)
            Agent.BING_SEARCH_LIMIT = 1;
    }
}
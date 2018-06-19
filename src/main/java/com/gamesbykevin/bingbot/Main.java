package com.gamesbykevin.bingbot;

import com.gamesbykevin.bingbot.util.Email;
import com.gamesbykevin.bingbot.util.LogFile;
import com.gamesbykevin.bingbot.util.PropertyUtil;

import java.util.concurrent.TimeUnit;

import static com.gamesbykevin.bingbot.MainHelper.*;
import static com.gamesbykevin.bingbot.util.LogFile.displayMessage;
import static com.gamesbykevin.bingbot.util.LogFile.recycle;

public class Main extends Thread {

    //how long do we sleep our thread
    public static final long THREAD_DELAY = 1000L;

    //how long does the bot sleep (in minutes);
    public static long SLEEP_BOT;

    //how many milliseconds per minute
    private static final long MILLIS_PER_MINUTE = (1000 * 60);

    //when did we start
    private final long start;

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
            displayMessage(e);
            recycle();
        }
    }

    public Main() {

        //mark the start time
        this.start = System.currentTimeMillis();
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
                    runSearchProgram(false);

                    //run our program one again, but spoofing a mobile browser
                    runSearchProgram(true);

                    //check for bonus links
                    //runBonusProgram();

                    //store the new time since our last successful run
                    previous = System.currentTimeMillis();

                    //get the # of points
                    int points = getPoints();

                    //send email that we are done
                    Email.send("Bing Points: " + points, "Uptime HH:MM:SS - " + getDurationDesc(System.currentTimeMillis() - start));

                    //clean up log file resources
                    LogFile.recycle();

                } else {

                    displayMessage("Bot sleeping will run again in " + TimeUnit.MILLISECONDS.toSeconds(remaining) + " seconds.", false);

                }

            } catch (Exception e) {
                displayMessage(e);
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
}
package com.gamesbykevin.bingbot;

import com.gamesbykevin.bingbot.util.Email;
import com.gamesbykevin.bingbot.util.LogFile;
import com.gamesbykevin.bingbot.util.Properties;

import java.util.concurrent.TimeUnit;

import static com.gamesbykevin.bingbot.MainHelper.*;
import static com.gamesbykevin.bingbot.util.LogFile.displayMessage;
import static com.gamesbykevin.bingbot.util.LogFile.recycle;
import static com.gamesbykevin.bingbot.util.Properties.isUnixLinux;
import static com.gamesbykevin.bingbot.util.Properties.isWindows;

public class Main extends Thread {

    //how long do we sleep
    public static final long THREAD_DELAY = 60000L;

    //how long does the bot sleep (in minutes);
    public static long SLEEP_BOT;

    //how many milliseconds per minute
    private static final long MILLIS_PER_MINUTE = (1000 * 60);

    //when did we start
    private final long start;

    //this script will reboot ubuntu
    private static final String SHELL_SCRIPT_FILE = "./turn_off.sh";

    public static void main(String[] args) {

        try {

            //load our properties
            displayMessage("Loading properties: " + Properties.PROPERTY_FILE);
            Properties.load();

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

        //while (true) {

            try {

                //milliseconds remaining until we can run again
                final long remaining = (MILLIS_PER_MINUTE * SLEEP_BOT) - (System.currentTimeMillis() - previous);

                //if there is no time remaining we can run again
                if (remaining <= 0) {

                    //run our program in our typical chrome browser
                    //Email.send("Bing bot","Desktop Search Start");
                    runSearchProgram(false);

                    //run our program one again, but spoofing a mobile browser
                    //Email.send("Bing bot","Mobile Search Start");
                    runSearchProgram(true);

                    //check for bonus links
                    //Email.send("Bing bot","Bonus Links Start");
                    runBonusProgram();

                    //store the new time since our last successful run
                    previous = System.currentTimeMillis();

                    //get the # of points
                    //Email.send("Bing bot","Checking points");
                    int points = getPoints();

                    //send email that we are done
                    Email.send("Bing Points: " + points, "Runtime HH:MM:SS - " + getDurationDesc(System.currentTimeMillis() - start));

                    //clean up log file resources
                    LogFile.recycle();

                } else {

                    displayMessage("Bot sleeping will run again in " + TimeUnit.MILLISECONDS.toSeconds(remaining) + " seconds.", false);

                }

            } catch (Exception e) {
                displayMessage(e);
                //break;
            }

            try {

                //sleep for a short time
                Thread.sleep(THREAD_DELAY);

                //call batch script to shutdown
                if (isWindows()) {
                    Process process = Runtime.getRuntime().exec("shutdown /s");
                    process.waitFor();
                } else if (isUnixLinux()) {
                    Process process = Runtime.getRuntime().exec("sudo shutdown");//SHELL_SCRIPT_FILE);
                    process.waitFor();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        //}
    }
}
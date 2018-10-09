package com.gamesbykevin.bingbot;

import com.gamesbykevin.bingbot.util.Email;
import com.gamesbykevin.bingbot.util.LogFile;
import com.gamesbykevin.bingbot.util.Properties;
import com.gamesbykevin.bingbot.util.Words;

import static com.gamesbykevin.bingbot.MainHelper.*;
import static com.gamesbykevin.bingbot.util.LogFile.displayMessage;

public class Main extends Thread {

    //how long do we sleep
    public static final long THREAD_DELAY = 10000L;

    //how long do we wait before starting
    public static final long START_DELAY = (1000L * 60 * 5);

    //this script will reboot ubuntu
    private static final String SHELL_SCRIPT_FILE = "./turn_off.sh";

    public static void main(String[] args) {

        try {

            //delay first before we start anything
            displayMessage("Sleeping before start");

            //sleep
            Thread.sleep(START_DELAY);

        } catch (Exception e) {

            //display error
            displayMessage(e);

        } finally {

            //notify that we are done
            displayMessage("Sleep Done");
        }

        try {

            //load our properties
            displayMessage("Loading properties: " + Properties.PROPERTY_FILE);
            Properties.load();

            //load our list of common english words
            Words.load();

            //start our thread
            Main main = new Main();
            main.start();

            //process has begun
            displayMessage("Bingbot started...");

        } catch (Exception e) {
            displayMessage(e);
            LogFile.recycle();
            Words.recycle();
        }
    }

    public Main() {
        //default constructor
    }

    @Override
    public void run() {

        try {

            //track how long our process takes
            final long start = System.currentTimeMillis();

            //notify that we have started
            Email.send("Bing bot","Start");

            //run our program in our typical chrome browser
            //Email.send("Bing bot","Desktop Search Start");
            runSearchProgram(false);

            //run our program one again, but spoofing a mobile browser
            //Email.send("Bing bot","Mobile Search Start");
            runSearchProgram(true);

            //we no longer need our words
            Words.recycle();

            //check for bonus links
            //Email.send("Bing bot","Bonus Links Start");
            runBonusProgram();

            //get the # of points
            //Email.send("Bing bot","Checking points");
            int points = getPoints();

            //send email that we are done
            Email.send("Bing Points: " + points, "Runtime HH:MM:SS - " + getDurationDesc(System.currentTimeMillis() - start));

            //clean up log file resources
            LogFile.recycle();

        } catch (Exception e) {
            displayMessage(e);
        } finally {
            Words.recycle();
            LogFile.recycle();
        }

        try {

            //sleep for a short time
            Thread.sleep(THREAD_DELAY);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {

            //call batch script to shutdown
            Process process = Runtime.getRuntime().exec(SHELL_SCRIPT_FILE);
            process.waitFor();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
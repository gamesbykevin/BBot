package com.gamesbykevin.bingbot.agent;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Random;

public class AgentHelper {

    //how long do we pause for
    private static final long PAUSE_DELAY_MIN = 5000L;

    //when we pause let's do it at a random time with this range
    private static final long PAUSE_DELAY_RANGE = 5000L;

    //object used to pick random time
    private static Random random;

    private static Random getRandom() {
        if (random == null)
            random = new Random(System.currentTimeMillis());

        return random;
    }

    protected static WebElement getWebElement(WebDriver driver, By by) {
        return getWebElement(driver, by, false);
    }

    protected static WebElement getWebElement(WebDriver driver, By by, boolean exitOnError) {

        WebElement element = null;

        //we are looking for an element on the page
        while (true) {

            try {

                //look for the element
                element = driver.findElement(by);

                //if the element is shown, we can return it
                if (element.isDisplayed())
                    return element;

            } catch (Exception e) {
                e.printStackTrace();
                pause();

                if (exitOnError)
                    break;
            }
        }

        //we weren't expecting to get here, so return null
        return null;
    }

    protected static List<WebElement> getWebElements(WebDriver driver, By by) {

        List<WebElement> elements = null;

        boolean wait = true;

        int count = 0;

        //we are looking for an element on the page
        while (wait) {

            try {

                //look for the element
                elements = driver.findElements(by);

                wait = false;

                for (int i = 0; i < elements.size(); i++) {

                    WebElement element = elements.get(i);

                    //all elements should be displayed by now, so remove ones not displayed
                    if (!element.isDisplayed()) {
                        elements.remove(i);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                pause();
            }

            count++;

            System.out.println("Waiting ... " + count);
        }

        //return our elements
        return elements;
    }

    protected static void pause() {

        try {

            //pick random sleep time
            final long sleep = PAUSE_DELAY_MIN + (getRandom().nextInt((int)PAUSE_DELAY_RANGE));

            System.out.println("Sleeping for: " + sleep);

            Thread.sleep(sleep);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Sleep completed");
    }

    protected static String getRandomWord() {

        //list of characters
        final String alphabet = "abcdefghijklmnopqrstuvwxyz";

        //our word
        String word = "";

        //the length of the word will be random size
        final int length = getRandom().nextInt(6) + 5;

        //pick random letters to create a fake word
        while (word.length() < length) {
            int index = random.nextInt(alphabet.length());
            word += alphabet.charAt(index);
        }

        //return our result
        return word;
    }
}
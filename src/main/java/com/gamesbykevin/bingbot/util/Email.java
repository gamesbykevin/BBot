package com.gamesbykevin.bingbot.util;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static com.gamesbykevin.bingbot.util.LogFile.displayMessage;

public class Email {

    public static String EMAIL_NOTIFICATION_ADDRESS = null;

    public static String GMAIL_SMTP_USERNAME = null;
    public static String GMAIL_SMTP_PASSWORD = null;

    public static void send(final String subject, final String body) {

        //don't send email if email address doesn't exist
        if (EMAIL_NOTIFICATION_ADDRESS == null || EMAIL_NOTIFICATION_ADDRESS.trim().length() < 5)
            return;

        try {

            //setup our google properties
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            //use google's smtp servers to send emails
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(GMAIL_SMTP_USERNAME, GMAIL_SMTP_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(GMAIL_SMTP_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(EMAIL_NOTIFICATION_ADDRESS));
            message.setSubject(subject);
            message.setText(body);

            //we are now sending
            displayMessage("Sending email....");

            //send the email
            Transport.send(message);

            //display we are good
            displayMessage("Sent message successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
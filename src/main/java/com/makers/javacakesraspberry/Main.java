package com.makers.javacakesraspberry;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

/**
 *
 * @author laura
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Running...");

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        // provision gpio pin #02 as an input pin with its internal pull down resistor enabled
        final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_08);

        // set shutdown state for this input pin
        myButton.setShutdownOptions(true);

        // create and register gpio pin listener
        myButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                boolean buttonPressed = myButton.isLow();

                if (buttonPressed) {
                    TakePicture picture = new TakePicture();
                }
            }
        });

        System.out.println("Press doorbell to take picture");

        // keep program running until user aborts (CTRL-C)
        while(true) {
            Thread.sleep(500);
        }
        // SMTP server information
        String host = "smtp.gmail.com";
        String port = "587";
        String mailFrom = "javacakes101@gmail.com";
        String password = "makers2020";
 
        // outgoing message information
        String mailTo = "<ADD YOUR EMAIL ADDRESS>";
        String subject = "Hello from javacakes with an attachment";
        String message = "Hi guy, Hope you have a lot of javacakes. Duke.";
        
        // attachments
        String[] attachFiles = new String[1];
        attachFiles[0] = "/home/pi/cam.jpg";
 
        EmailSender mailer = new EmailSender();
 
        try {
            mailer.sendEmailWithAttachments(host, port, mailFrom, password, mailTo,
                    subject, message, attachFiles);
            System.out.println("Email sent with attachment.");
        } catch (Exception ex) {
            System.out.println("Failed to sent email with attachment.");
            ex.printStackTrace();
        }
    }
    
}

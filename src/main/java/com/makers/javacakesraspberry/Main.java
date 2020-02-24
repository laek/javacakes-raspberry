package com.makers.javacakesraspberry;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Running...");

        // SMTP server information
        String host = "smtp.gmail.com";
        String port = "587";
        String mailFrom = "javacakes101@gmail.com";
        String password = "makers2020";

        // outgoing message information
        String mailTo = "javacakes101@gmail.com";
        String subject = "Hello from javacakes with an attachment";
        String message = "Hi guy, Hope you have a lot of javacakes. Duke.";

        // attachments
        String[] attachFiles = new String[1];
        attachFiles[0] = "/home/pi/javacakes/images/pic.jpg";

        EmailSender mailer = new EmailSender();

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

                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

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
        });

        System.out.println("Press doorbell to take picture");

        // keep program running until user aborts (CTRL-C)
        while(true) {
            Thread.sleep(500);
        }

    }
    
}

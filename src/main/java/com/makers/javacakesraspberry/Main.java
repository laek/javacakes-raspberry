package com.makers.javacakesraspberry;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;
import com.pi4j.io.gpio.trigger.GpioPulseStateTrigger;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        String message = "There's somebody at your door! Visit http://10.0.209.112:8081/ to see who it is.";

        // attachments
        String[] attachFiles = new String[1];
        attachFiles[0] = "/home/pi/javacakes/images/pic.jpg";

        EmailSender mailer = new EmailSender();

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        // Button
        final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_29);

        // LED and Buzzer
        GpioPinDigitalOutput myOutput[] = {
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06, "LED", PinState.LOW),
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_16, "Buzzer", PinState.LOW)
        };

        // set shutdown state for this input pin
        myButton.setShutdownOptions(true);

        // Buzzer Trigger
        myButton.addTrigger(new GpioPulseStateTrigger(PinState.LOW, myOutput[1], 500));

        // LED Trigger
        myButton.addTrigger(new GpioPulseStateTrigger(PinState.LOW, myOutput[0], 500));

        // create and register gpio pin listener
        myButton.addTrigger(new GpioCallbackTrigger(new Callable<Void>() {
            public Void call() throws InterruptedException {
                boolean buttonPressed = myButton.isLow();

                if (buttonPressed) {

                    LocalDateTime doorbellAlert = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy HH:mm:ss a");
                    String newDateTime = formatter.format(doorbellAlert);
                    String subject = "Doorbell rang on " + newDateTime;

                    new TakePicture();

                    try {
                        TimeUnit.SECONDS.sleep(5);

                        mailer.sendEmailWithAttachments(host, port, mailFrom, password, mailTo,
                                subject, message, attachFiles);

                        System.out.println("Email sent with attachment.");
                    } catch (Exception ex) {
                        System.out.println("Failed to sent email with attachment.");
                        ex.printStackTrace();
                    }

                    new VideoCall();

                    new DuoCall();
                }
                return null;
            }
        }));

        System.out.println("Press doorbell to take picture");

        // keep program running until user aborts (CTRL-C)
        while(true) {
            Thread.sleep(500);
        }
    }
}

package com.makers.javacakesraspberry;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;
import com.pi4j.io.gpio.trigger.GpioPulseStateTrigger;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class ButtonActivation {

        public static final GpioController gpio = GpioFactory.getInstance();

        // Button
        public static final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_29);

       // LED and Buzzer
        public static GpioPinDigitalOutput myOutput[] = {
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06, "LED", PinState.LOW),
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_16, "Buzzer", PinState.LOW)
        };

        public static void takePicture() throws InterruptedException {

            // set shutdown state for this input pin
            myButton.setShutdownOptions(true);

            // Buzzer Trigger
            myButton.addTrigger(new GpioPulseStateTrigger(PinState.LOW, myOutput[1], 500));

            // LED Trigger
            myButton.addTrigger(new GpioPulseStateTrigger(PinState.LOW, myOutput[0], 500));

            // create and register gpio pin listener
            myButton.addTrigger(new GpioCallbackTrigger(new Callable<Void>() {
                public Void call() throws IOException {
                    boolean buttonPressed = myButton.isLow();

                    if (buttonPressed) {

                        Livestream livestream = new Livestream();

                        livestream.stopLivestream();

                        String subject = "Doorbell rang on " + DoorbellTime.newDateTime();

                        new TakePicture();

                        try {
                            TimeUnit.SECONDS.sleep(5);

                            new EmailSender().sendEmailWithAttachments(MessageInfo.host, MessageInfo.port, MessageInfo.mailFrom, MessageInfo.password, MessageInfo.mailTo,
                                    subject, MessageInfo.message, MessageInfo.attachFiles);

                            System.out.println("Email sent with attachment.");
                        } catch (Exception ex) {
                            System.out.println("Failed to sent email with attachment.");
                            ex.printStackTrace();
                        }
                    }
                    return null;
                }
            }));

            System.out.println("Press doorbell to take picture");

            // keep program running until user aborts (CTRL-C)
            while (true) {
                Thread.sleep(500);
            }

        }

}

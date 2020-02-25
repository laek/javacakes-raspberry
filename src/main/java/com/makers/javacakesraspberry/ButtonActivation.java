package com.makers.javacakesraspberry;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;
import com.pi4j.io.gpio.trigger.GpioPulseStateTrigger;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class ButtonActivation {

        public static final GpioController gpio = GpioFactory.getInstance();

        // Button
        public static final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_29);

//        // LED and Buzzer
        public static GpioPinDigitalOutput myOutput[] = {
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06, "LED", PinState.LOW),
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_16, "Buzzer", PinState.LOW)
        };

        public static void takePicture() throws InterruptedException {
            System.out.println("Press doorbell to take picture from the method");

            // set shutdown state for this input pin
            myButton.setShutdownOptions(true);

//            // Buzzer Trigger
            myButton.addTrigger(new GpioPulseStateTrigger(PinState.LOW, myOutput[1], 500));
//
//            // LED Trigger
            myButton.addTrigger(new GpioPulseStateTrigger(PinState.LOW, myOutput[0], 500));

            // create and register gpio pin listener
            myButton.addTrigger(new GpioCallbackTrigger(new Callable<Void>() {
                public Void call() {
                    boolean buttonPressed = myButton.isLow();

                    if (buttonPressed) {
                        String subject = "Doorbell rang on " + DoorbellTime.newDateTime();

                        TakePicture picture = new TakePicture();

                        try {
                            TimeUnit.SECONDS.sleep(5);

                            new EmailSender().sendEmailWithAttachments(MessageInfo.host, MessageInfo.port, MessageInfo.mailFrom, MessageInfo.password, MessageInfo.mailTo,
                                    subject, MessageInfo.message, MessageInfo.attachFiles);

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

            System.out.println("Press doorbell to take picture from while loop");

            // keep program running until user aborts (CTRL-C)
            while (true) {
                Thread.sleep(500);
            }

        }

//        public void startVideoStream () {
//            VideoCall videoCall = new VideoCall();
//        }

//        public static void terminateVideoStream () throws IOException {
//            Runtime runtime = Runtime.getRuntime();
//            runtime.exec("sudo service motion stop");
//            System.out.println("Video Stopped");
//        }

}

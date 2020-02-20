/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.makers.javacakesraspberry;

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
    }
}
package com.makers.javacakesraspberry;

import java.awt.*;
import java.awt.event.InputEvent;

public class DuoCall {

    public DuoCall() {

        System.out.println("Starting duocall");

        Robot robot = null;

        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

//        robot.delay(500);
//
//        robot.mouseMove(20,20);     // open top left menu
//
//        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
//        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
//
//        robot.delay(500);
//
//        robot.mouseMove(20, 350);   // move to chromium apps
//
//        robot.delay(500);
//
//        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
//        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);           // click
//
//        robot.delay(500);
//
//        robot.mouseMove(250, 350);      // move to duo
//
//        robot.delay(500);
//
//        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
//        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);           // click
//
//        robot.delay(11000);

        robot.mouseMove(500, 200);          // move to contact

        robot.delay(600);

        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);       // click
        robot.delay(400);

        robot.mouseMove(600, 650);          // move to video call

        robot.delay(300);

        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);       // click
    }
}

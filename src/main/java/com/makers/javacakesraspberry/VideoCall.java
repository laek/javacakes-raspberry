package com.makers.javacakesraspberry;

import java.util.concurrent.TimeUnit;

public class VideoCall {

    public VideoCall() {

        try {

            Runtime runtime = Runtime.getRuntime();

            runtime.exec("sudo service motion start");

            System.out.println("Video Started");

            TimeUnit.SECONDS.sleep(30);

            runtime.exec("sudo service motion stop");

            System.out.println("Video Stopped");

        } catch (Exception e) {

            System.out.println("Error: " + e.getMessage());

        }

    }
}

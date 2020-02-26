package com.makers.javacakesraspberry;

import java.io.IOException;

public class Livestream {

    Runtime runtime = Runtime.getRuntime();

    public void startLivestream() throws IOException {
        runtime.exec("sudo service motion start");

        System.out.println("Livestream Started");
    }

    public void stopLivestream() throws IOException {
        runtime.exec("sudo service motion stop");

        System.out.println("Livestream Stopped");
    }
}

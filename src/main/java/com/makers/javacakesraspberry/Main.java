package com.makers.javacakesraspberry;

import static spark.Spark.*;
import spark.ModelAndView;
import java.util.HashMap;

public class Main {

    static {
        System.setProperty("java.awt.headless", "false");
    }

    public static void main(String[] args) throws InterruptedException {

        Livestream livestream = new Livestream();

        System.out.println("Running...");

        get("/", (req, res) -> {
            livestream.startLivestream();

            HashMap index = new HashMap();

            return new ModelAndView(index, "templates/index.vtl");
        }, new VelocityTemplateEngine());

        post("/talk", (req, res) -> {
            livestream.stopLivestream();

            new DuoCall();

            return "Call started please close this page.";
        });

        post("/endstream", (req, res) -> {
            livestream.stopLivestream();
            return "Doorbell ignored - please close this window.";
        });

        new ButtonActivation().takePicture();
    }
}

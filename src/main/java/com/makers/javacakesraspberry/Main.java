package com.makers.javacakesraspberry;

import static spark.Spark.*;
import spark.ModelAndView;
import java.util.HashMap;

public class Main {

    static {
        System.setProperty("java.awt.headless", "false");
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Running...");

        get("/", (req, res) -> {

            HashMap index = new HashMap();

            return new ModelAndView(index, "templates/index.vtl");
        }, new VelocityTemplateEngine());

        post("/talk", (req, res) -> {
            return "you are talking now";
        });

        new ButtonActivation().takePicture();
    }
}

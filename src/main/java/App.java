package com.makers.javacakesraspberry;

import static spark.Spark.*;
import spark.ModelAndView;
import java.util.HashMap;



public class App {

    public static void main(String[] args) {

        staticFileLocation("/public");


        get("/", (req, res) -> {

            HashMap index = new HashMap();

            return new ModelAndView(index, "templates/index.vtl");
        }, new VelocityTemplateEngine());

        post("/talk", (req, res) -> {
            return "you are talking now";
        });

    }
}

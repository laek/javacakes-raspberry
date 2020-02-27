package com.makers.javacakesraspberry;

public class MessageInfo {
    // SMTP server information
    public static String host = "smtp.gmail.com";
    public static String port = "587";
    public static String mailFrom = "javacakes101@gmail.com";
    public static String password = "makers2020";

    // outgoing message information
    public static String mailTo = "maxosully@gmail.com";
    public static String message = "There's somebody at your door! Visit http://10.0.209.112:4567/ to see who it is.";

    // attachments
    public static String[] attachFiles = {"/home/pi/javacakes/images/pic.jpg"};
}

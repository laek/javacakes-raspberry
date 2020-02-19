/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.makers.javacakesraspberry;

/**
 *
 * @author laura
 */
public class Main {
    
    public static void main(String[] args) {
        // SMTP server information
        String host = "smtp.gmail.com";
        String port = "587";
        String mailFrom = "javacakes101@gmail.com";
        String password = "makers2020";
 
        // outgoing message information
        String mailTo = "laura.kytonen@gmail.com";
        String subject = "Hello from javacakes with an attachment";
        String message = "Hi guy, Hope you have a lot of javacakes. Duke.";
        
        // attachments
        String[] attachFiles = new String[3];
        attachFiles[0] = "/home/pi/cam.jpg";
 
        EmailSender mailer = new EmailSender();
 
        try {
            mailer.sendEmailWithAttachments(host, port, mailFrom, password, mailTo,
                    subject, message, attachFiles);
            System.out.println("Email sent.");
        } catch (Exception ex) {
            System.out.println("Failed to sent email.");
            ex.printStackTrace();
        }
    }
    
}

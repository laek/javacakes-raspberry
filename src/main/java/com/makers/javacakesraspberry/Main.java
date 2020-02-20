/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.makers.javacakesraspberry;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;

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
        String mailTo = "marianetchaeva@gmail.com";
        String subject = "Hello from javacakes with an attachment";
        String message = "Hi guy, Hope you have a lot of javacakes. Duke.";
        
        // attachments
        String[] attachFiles = new String[1];
        attachFiles[0] = "/home/pi/cam.jpg";
 
        EmailSender mailer = new EmailSender();
 
        try {
            mailer.sendEmailWithAttachments(host, port, mailFrom, password, mailTo,
                    subject, message, attachFiles);
            System.out.println("Email sent with attachment v.1.");
        } catch (Exception ex) {
//            mailer.sendPlainTextEmail(host, port, mailFrom, password, mailTo,
//                subject, message);
//            System.out.println("Email sent without attachment.");
            System.out.println("Failed to sent email with attachment.");
            ex.printStackTrace();
        }
    }
    
}

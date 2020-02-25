package com.makers.javacakesraspberry;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class MainTest {
    private static final int SMTP_TEST_PORT = 3025;
    private GreenMail greenMail;
    EmailSender mailer;
    SimulatedGpioProvider testGpio;

//    @Rule
//    public final EnvironmentVariables environmentVariables
//            = new EnvironmentVariables();
//    @Test
//    public void setEnvironmentVariable() {
//        environmentVariables.set("host", "localhost");
//        environmentVariables.set("port", Integer.toString(SMTP_TEST_PORT));
//        environmentVariables.set("mailFrom", "sender@localhost.com");
//        environmentVariables.set("password", "password");
//        environmentVariables.set("mailTo", "recepient@localhost.com");
//        environmentVariables.set("message", "This is an email sending test.");
//        assertEquals("localhost", System.getenv("host"));
//    }


    // SMTP server information
//    String host = "localhost"; //"smtp.gmail.com";
//    String port = Integer.toString(SMTP_TEST_PORT); //"587";
//    String mailFrom = "sender@localhost.com";
//    String password = "password";

    // outgoing message information
//    String mailTo = "recepient@localhost.com";
//    String subject = "Email sending test";
//    String message = "This is an email sending test.";

    // attachments
    String[] attachFiles = {"/Users/student/Projects/week11/javacakes-raspberry/javacakes-raspberry/src/test/resources/testFile.txt"};

    @BeforeEach
    void setUp() {
//        environmentVariables.set("host", "localhost");
        greenMail = new GreenMail(new ServerSetup(SMTP_TEST_PORT, null, "smtp"));
        greenMail.start();
        mailer = new EmailSender(); // ("localhost", SMTP_TEST_PORT);
//        try {
//            mailer.sendEmailWithAttachments(host, port, mailFrom, password, mailTo,
//                    subject, message, attachFiles);
//        } catch (Exception ex) {
//            ex.printStackTrace();
        }

    @AfterEach
    void tearDown() throws Exception {
        greenMail.stop();
        }

    public static class TestMain {
        public void init() throws InterruptedException {
            String[] arr = {};
            Main.main(arr);
        }
    }

    @Test
    void pictureTakenWhenButtonPressed() {
        testGpio.setState(RaspiPin.GPIO_29, PinState.LOW);

    }
}
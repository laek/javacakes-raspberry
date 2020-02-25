package com.makers.javacakesraspberry;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EmailSenderTest {
    private static final int SMTP_TEST_PORT = 3025;
    private GreenMail greenMail;
    EmailSender mailer;

    // SMTP server information
    String host = "localhost"; //"smtp.gmail.com";
    String port = Integer.toString(SMTP_TEST_PORT); //"587";
    String mailFrom = "sender@localhost.com";
    String password = "password";

    // outgoing message information
    String mailTo = "recepient@localhost.com";
    String subject = "Email sending test";
    String message = "This is an email sending test.";

    // attachments
    String[] attachFiles = {"src/test/resources/testFile.jpg"};

    @BeforeEach
    void setUp() throws Exception {
        greenMail = new GreenMail(new ServerSetup(SMTP_TEST_PORT, null, "smtp"));
        greenMail.start();
        mailer = new EmailSender(); // ("localhost", SMTP_TEST_PORT);
        try {
            mailer.sendEmailWithAttachments(host, port, mailFrom, password, mailTo,
                    subject, message, attachFiles);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        greenMail.stop();
    }

    @Test //Testing that an email can be sent
    void sendingEmail() throws MessagingException, IOException {
        Message[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length);
    }

    @Test //Testing the text content of the email
    void checkingEmailContent() throws IOException, MessagingException {
        MimeMessage message = greenMail.getReceivedMessages()[0];
        Multipart retParts = (Multipart) message.getContent();
        BodyPart bp = retParts.getBodyPart(0);
        assertThat((String) bp.getContent(),
                equalTo("This is an email sending test."));
    }

    @Test //Testing that the attached file is the file specified in the file path
    void sendingEmailWithAttachments() throws Exception {
        MimeMessage retMeg = greenMail.getReceivedMessages()[0];
        Multipart retParts = (Multipart) (retMeg.getContent());
        int numAttach = 0;
        for (int i = 0; i < retParts.getCount(); i++) {
            BodyPart bp = retParts.getBodyPart(i);
            String disp = bp.getDisposition();
            if (disp != null && (disp.equals(BodyPart.ATTACHMENT))) {
                assertEquals(bp.getFileName(), "testFile.jpg");
                numAttach++;
            }
        }
        assertEquals(1, numAttach);
    }

}

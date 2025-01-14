package com.project.JournalApp.services;

import com.project.services.EmailService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTests {

    @Autowired
    private EmailService emailService;

    @Test
    public void testSendMail() {
        Assertions.assertDoesNotThrow(() -> emailService.sendMail(
                "example@gmail.com",
                "Testing Mail Service",
                "This mail is send while testing mail service using Java Mail Sender"
        ));
    }
}

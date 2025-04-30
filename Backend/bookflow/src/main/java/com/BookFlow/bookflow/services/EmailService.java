package com.BookFlow.bookflow.services;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String to, String subject, String body){

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mail = new MimeMessageHelper(message,true, "UTF-8");

            mail.setFrom("anjeshmainali348@gmail.com");
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body,true);

            javaMailSender.send(message);
        }
        catch (Exception e){
            log.error("Exception while sendEmail",e);
        }

    }


}

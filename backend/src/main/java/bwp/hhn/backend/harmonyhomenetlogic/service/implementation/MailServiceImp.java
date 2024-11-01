package bwp.hhn.backend.harmonyhomenetlogic.service.implementation;

import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class MailServiceImp implements MailService {

    private final JavaMailSender javaMailSender;

    private final static Logger logger = Logger.getLogger(MailServiceImp.class.getName());

    private final String mailFrom = "harmonyhomenet.service@gmail.com";
    private final String noReplyAddress = "no-reply@harmonyhomenet.com";

    @Override
    @Async
    public void sendNotificationMail(String subject, String text, String recipient) {

        logger.info("Sending mail to " + recipient + " with subject: " + subject);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mailFrom);
        mailMessage.setTo(recipient);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);
        mailMessage.setReplyTo(noReplyAddress);

        javaMailSender.send(mailMessage);

        logger.info("Mail sent to " + recipient + " with subject: " + subject);
    }

}

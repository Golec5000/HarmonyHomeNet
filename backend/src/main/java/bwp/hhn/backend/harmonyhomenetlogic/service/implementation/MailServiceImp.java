package bwp.hhn.backend.harmonyhomenetlogic.service.implementation;

import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailParseException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class MailServiceImp implements MailService {

    private final JavaMailSender mailSender;

    private final static Logger logger = Logger.getLogger(MailServiceImp.class.getName());

    private final String mailFrom = "harmonyhomenet.service@gmail.com";
    private final String noReplyAddress = "no-reply@harmonyhomenet.com";

    @Override
    @Async
    public void sendNotificationMail(String subject, String text, String recipient) {

        logger.info("Sending mail to " + recipient + " with subject: " + subject);

        try {
            // Validate email address
            InternetAddress emailAddr = new InternetAddress(recipient);
            emailAddr.validate();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(recipient);
            helper.setFrom(mailFrom, "Harmony Home Net");
            helper.setSubject(subject);
            helper.setText(text, true);
            helper.setReplyTo(noReplyAddress);

            mailSender.send(message);
        } catch (AddressException ex) {
            throw new IllegalArgumentException("Invalid email address: " + recipient, ex);
        } catch (MessagingException ex) {
            throw new MailParseException("Failed to send email", ex);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to send email", e);
        }

        logger.info("Mail sent to " + recipient + " with subject: " + recipient);
    }


}
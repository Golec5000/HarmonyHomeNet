package bwp.hhn.backend.harmonyhomenetlogic.service.interfaces;

public interface MailService {

    void sendNotificationMail(String subject, String text, String recipient);

}

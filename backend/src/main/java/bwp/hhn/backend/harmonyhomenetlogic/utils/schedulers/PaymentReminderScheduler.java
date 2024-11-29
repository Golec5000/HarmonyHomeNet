package bwp.hhn.backend.harmonyhomenetlogic.utils.schedulers;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.NotificationType;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Payment;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.PossessionHistory;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.NotificationTypeRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.PaymentRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.adapters.SmsService;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.MailService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class PaymentReminderScheduler {

    private final MailService mailService;
    private final SmsService smsService;
    private final PaymentRepository paymentRepository;
    private final NotificationTypeRepository notificationTypeRepository;

    private static final Logger LOGGER = Logger.getLogger(PaymentReminderScheduler.class.getName());

    // Przypomnienia przed terminem płatności (w poniedziałek o 12:00)
    @Scheduled(cron = "0 0 12 * * MON")
    @Async
    public void sendUpcomingPaymentReminders() {
        Instant twoWeeksBefore = Instant.now().plus(2, ChronoUnit.WEEKS);
        Instant oneWeekBefore = Instant.now().plus(1, ChronoUnit.WEEKS);

        // Pobranie płatności z terminem za 2 tygodnie lub 1 tydzień
        List<Payment> upcomingPayments = paymentRepository.findByPaymentDateBetween(twoWeeksBefore, oneWeekBefore);

        for (Payment payment : upcomingPayments) {

            List<User> owners = payment.getApartment().getPossessionHistories().stream()
                    .map(PossessionHistory::getUser)
                    .toList();

            owners.forEach(owner -> {
                String message = "Przypomnienie: Twoja płatność za " + payment.getDescription() +
                        " jest wymagana do " + payment.getPaymentDate() + ".";
                sendNotification(owner.getEmail(), owner.getPhoneNumber(), message);
                LOGGER.info("Wysłano przypomnienie o nadchodzącej płatności do użytkownika: " + owner.getFirstName() + " " + owner.getLastName());
            });
        }
    }

    // Przypomnienia po terminie płatności (co 2 dni)
    @Scheduled(cron = "0 0 12 */2 * ?")
    @Async
    public void sendOverduePaymentReminders() {
        Instant today = Instant.now();

        // Pobranie płatności, które są po terminie i nie zostały jeszcze zapłacone
        List<Payment> overduePayments = paymentRepository.findByDueDateBeforeAndIsPaidFalse(today);

        for (Payment payment : overduePayments) {

            List<User> owners = payment.getApartment().getPossessionHistories().stream()
                    .map(PossessionHistory::getUser)
                    .toList();

            owners.forEach(owner -> {
                String message = "Przypomnienie o zaległej płatności: Twoja płatność za " + payment.getDescription() +
                        " była wymagana do " + payment.getPaymentDate() + ". Prosimy o dokonanie płatności jak najszybciej.";
                sendNotification(owner.getEmail(), owner.getPhoneNumber(), message);
                LOGGER.info("Wysłano przypomnienie o zaległej płatności do użytkownika: " + owner.getFirstName() + " " + owner.getLastName());
            });

        }
    }

    // Wysyłanie powiadomienia e-mail i SMS
    private void sendNotification(String email, String phoneNumber, String message) {

        List<NotificationType> notificationTypes = notificationTypeRepository.findByUserEmail(email);

        if (notificationTypes.contains(Notification.EMAIL)) {
            mailService.sendNotificationMail("Payment Reminder", message, email);
        }
        if (notificationTypes.contains(Notification.SMS)) {
            smsService.sendSms(phoneNumber, message);
        }
    }
}

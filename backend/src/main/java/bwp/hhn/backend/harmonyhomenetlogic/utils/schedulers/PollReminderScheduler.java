package bwp.hhn.backend.harmonyhomenetlogic.utils.schedulers;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.NotificationType;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Poll;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.NotificationTypeRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.PollRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.VoteRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables.PossessionHistoryRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.adapters.SmsService;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

import static bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Notification.EMAIL;
import static bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Notification.SMS;

@Component
@RequiredArgsConstructor
public class PollReminderScheduler {

    private final MailService mailService;
    private final SmsService smsService;
    private final PollRepository pollRepository;
    private final PossessionHistoryRepository possessionHistoryRepository;
    private final VoteRepository voteRepository;
    private final NotificationTypeRepository notificationTypeRepository;


    @Scheduled(cron = "0 0 12 */4 * ?")
    @Async
    public void sendPollReminder() {
        List<Poll> activePolls = pollRepository.findAllByEndDateAfter(Instant.now());

        for (Poll poll : activePolls) {
            List<User> owners = possessionHistoryRepository.findAllUniqueOwners();
            String apartmentSignature = possessionHistoryRepository.findApartmentSignatureByUserUuidID(poll.getUuidID());

            for (User owner : owners) {
                if (!voteRepository.existsByPollUuidIDAndApartmentSignature(poll.getUuidID(), apartmentSignature)) {
                    List<NotificationType> notificationTypes = notificationTypeRepository.findByUserEmail(owner.getEmail());

                    if (notificationTypes.contains(SMS)) {
                        smsService.sendSms("Przypominamy o głosowaniu nad : " + poll.getPollName(), owner.getPhoneNumber());
                    }
                    if (notificationTypes.contains(EMAIL)) {
                        mailService.sendNotificationMail("Przyppomnienie o głosowaniu", "Przypominamy o głosowaniu nad : " + poll.getPollName(), owner.getEmail());
                    }
                }
            }
        }
    }

    @Scheduled(fixedRate = 60000) // Check every minute
    @Async
    public void checkPollEndDates() {
        List<Poll> endedPolls = pollRepository.findAllByEndDateBefore(Instant.now());
        endedPolls.forEach(this::sendEndPollNotification);
    }

    private void sendEndPollNotification(Poll poll) {
        String subject = "Głosowanie zakończone";
        String message = "Głosowanie nad: " + poll.getPollName() + " zostało zakończone. ";
        if (poll.getVotes().size() >= poll.getMinCurrentVotesCount()) {
            message += "Frekwencja została osiągnięta. ";
            if (poll.getSummary().compareTo(poll.getMinSummary()) >= 0) {
                message += "Wynik głosowania przekroczył minimalny próg: " + poll.getSummary();
            } else {
                message += "Wynik głosowania nie przekroczył minimalnego progu: " + poll.getSummary();
            }
        } else {
            message += "Frekwencja nie została osiągnięta.";
        }
        List<User> uniqueOwners = possessionHistoryRepository.findAllUniqueOwners();
        String finalMessage = message;
        uniqueOwners.forEach(owner -> {
            List<NotificationType> notificationTypes = notificationTypeRepository.findByUserEmail(owner.getEmail());
            if (notificationTypes.contains(SMS)) {
                smsService.sendSms(finalMessage, owner.getPhoneNumber());
            }
            if (notificationTypes.contains(EMAIL)) {
                mailService.sendNotificationMail(subject, finalMessage, owner.getEmail());
            }
        });
    }

}

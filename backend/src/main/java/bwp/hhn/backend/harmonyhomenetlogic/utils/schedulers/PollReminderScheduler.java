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
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

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
        List<Poll> activePolls = pollRepository.findAllByEndDateAfter(LocalDateTime.now());

        for (Poll poll : activePolls) {
            List<User> owners = possessionHistoryRepository.findAllUniqueOwners();
            String apartmentSignature = possessionHistoryRepository.findApartmentSignatureByUserUuidID(poll.getUuidID());
            for (User owner : owners) {
                boolean hasVoted = voteRepository.existsByPollUuidIDAndApartmentSignature(poll.getUuidID(), apartmentSignature);

                if (!hasVoted) {
                    List<NotificationType> notificationTypes = notificationTypeRepository.findByUserEmail(owner.getEmail());
                    if (notificationTypes.contains(Notification.SMS)) {
                        smsService.sendSms(
                                "Przypominamy o głosowaniu nad : " + poll.getPollName(),
                                owner.getPhoneNumber())
                        ;
                    }
                    if (notificationTypes.contains(Notification.SMS)) {
                        mailService.sendNotificationMail(
                                "Przyppomnienie o głosowaniu",
                                "Przypominamy o głosowaniu nad : " + poll.getPollName(),
                                owner.getEmail()
                        );
                    }
                }
            }
        }
    }

}

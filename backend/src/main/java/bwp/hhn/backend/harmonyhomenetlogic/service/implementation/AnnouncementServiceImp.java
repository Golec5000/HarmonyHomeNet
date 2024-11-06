package bwp.hhn.backend.harmonyhomenetlogic.service.implementation;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.AnnouncementNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Announcement;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.AnnouncementApartment;
import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.PossessionHistory;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.AnnouncementRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.ApartmentsRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables.AnnouncementApartmentRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.adapters.SmsService;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.AnnouncementService;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.MailService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.AnnouncementRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.DateRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.AnnouncementResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImp implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final ApartmentsRepository apartmentsRepository;
    private final AnnouncementApartmentRepository announcementApartmentRepository;
    private final MailService mailService;
    private final SmsService smsService;

    @Override
    public AnnouncementResponse createAnnouncement(AnnouncementRequest announcementRequest) throws UserNotFoundException {

        UUID userId = announcementRequest.getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User: " + userId + " not found"));

        Announcement announcement = Announcement.builder()
                .title(announcementRequest.getTitle())
                .content(announcementRequest.getContent())
                .user(user)
                .build();

        Announcement saved = announcementRepository.save(announcement);

        if (user.getAnnouncements() == null) user.setAnnouncements(new ArrayList<>());
        user.getAnnouncements().add(saved);

        return AnnouncementResponse.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .content(saved.getContent())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();

    }

    @Override
    public String deleteAnnouncement(Long announcementId) throws AnnouncementNotFoundException {
        if (!announcementRepository.existsById(announcementId)) {
            throw new AnnouncementNotFoundException("Announcement: " + announcementId + " not found");
        }
        announcementRepository.deleteById(announcementId);
        return "Announcement: " + announcementId + " deleted";
    }

    @Override
    public AnnouncementResponse updateAnnouncement(Long announcementId, AnnouncementRequest announcementRequest) throws AnnouncementNotFoundException, UserNotFoundException {

        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new AnnouncementNotFoundException("Announcement: " + announcementId + " not found"));

        announcement.setTitle(announcementRequest.getTitle());
        announcement.setContent(announcementRequest.getContent());

        Announcement saved = announcementRepository.save(announcement);

        return AnnouncementResponse.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .content(saved.getContent())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();

    }

    @Override
    public AnnouncementResponse getAnnouncement(Long announcementId) throws AnnouncementNotFoundException {

        return announcementRepository.findById(announcementId)
                .map(announcement -> AnnouncementResponse.builder()
                        .id(announcement.getId())
                        .title(announcement.getTitle())
                        .content(announcement.getContent())
                        .createdAt(announcement.getCreatedAt())
                        .updatedAt(announcement.getUpdatedAt())
                        .build())
                .orElseThrow(() -> new AnnouncementNotFoundException("Announcement: " + announcementId + " not found"));

    }

    @Override
    public PageResponse<AnnouncementResponse> getAllAnnouncements(int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Announcement> announcements = announcementRepository.findAll(pageable);
        
        return new PageResponse<>(
                announcements.getNumber(),
                announcements.getSize(),
                announcements.getContent().stream()
                        .map(announcement -> AnnouncementResponse.builder()
                                .id(announcement.getId())
                                .title(announcement.getTitle())
                                .content(announcement.getContent())
                                .createdAt(announcement.getCreatedAt())
                                .updatedAt(announcement.getUpdatedAt())
                                .build())
                        .toList(),
                announcements.isLast()
        );
    }

    @Override
    public PageResponse<AnnouncementResponse> getAnnouncementsByUserId(UUID userId, int pageNo, int pageSize) throws UserNotFoundException {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Announcement> announcements =announcementRepository.findByUserUuidID(userId, pageable);


        return new PageResponse<>(
                announcements.getNumber(),
                announcements.getSize(),
                announcements.getContent().stream()
                        .map(announcement -> AnnouncementResponse.builder()
                                .id(announcement.getId())
                                .title(announcement.getTitle())
                                .content(announcement.getContent())
                                .createdAt(announcement.getCreatedAt())
                                .updatedAt(announcement.getUpdatedAt())
                                .build())
                        .toList(),
                announcements.isLast()
        );

    }

    @Override
    public PageResponse<AnnouncementResponse> getAnnouncementsFromStartDateTOEndDate(DateRequest dateRequest, int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Announcement> announcements =announcementRepository.findDistinctByCreatedAtOrUpdatedAtBetween(
                dateRequest.getStartDate(),
                dateRequest.getEndDate(),
                pageable
        );

        return new PageResponse<>(
                announcements.getNumber(),
                announcements.getSize(),
                announcements.getContent().stream()
                        .map(announcement -> AnnouncementResponse.builder()
                                .id(announcement.getId())
                                .title(announcement.getTitle())
                                .content(announcement.getContent())
                                .createdAt(announcement.getCreatedAt())
                                .updatedAt(announcement.getUpdatedAt())
                                .build())
                        .toList(),
                announcements.isLast()
        );
    }

    @Override
    @Transactional
    public String linkAnnouncementsToApartments(Long announcementId, List<String> apartmentSignatures) throws AnnouncementNotFoundException, ApartmentNotFoundException {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new AnnouncementNotFoundException("Announcement: " + announcementId + " not found"));

        // Pobierz istniejące identyfikatory apartamentów
        Set<UUID> existingApartmentIds = new HashSet<>(announcementApartmentRepository.findApartmentIdsByAnnouncementId(announcementId));

        List<AnnouncementApartment> newAnnouncementApartments = apartmentSignatures.stream()
                .map(apartmentSignature -> apartmentsRepository.findByApartmentSignature(apartmentSignature)
                        .orElseThrow(() -> new ApartmentNotFoundException("Apartment: " + apartmentSignature + " not found")))
                .filter(apartment -> !existingApartmentIds.contains(apartment.getUuidID()))
                .map(apartment -> {
                    AnnouncementApartment newLink = AnnouncementApartment.builder()
                            .announcement(announcement)
                            .apartment(apartment)
                            .build();
                    announcement.getAnnouncementApartments().add(newLink);
                    return newLink;
                })
                .toList();

        announcementRepository.save(announcement);

        newAnnouncementApartments.forEach(announcementApartment -> {
            List<User> residents = announcementApartment.getApartment().getPossessionHistories().stream()
                    .map(PossessionHistory::getUser)
                    .distinct()
                    .toList();

            residents.forEach(resident -> {
                if (resident.getNotificationTypes() != null) {
                    resident.getNotificationTypes().forEach(notificationType -> {
                        switch (notificationType.getType()) {
                            case EMAIL:
                                mailService.sendNotificationMail(
                                        "Nowe ogłoszenie",
                                        "Nowe ogłoszenie zostało oddane: " + announcement.getTitle(),
                                        resident.getEmail()
                                );
                                break;
                            case SMS:
                                smsService.sendSms(
                                        "Nowe ogłoszenie zostało oddane: " + announcement.getTitle(),
                                        resident.getPhoneNumber()
                                );
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + notificationType.getType());
                        }
                    });
                }
            });
        });

        return "Linked " + newAnnouncementApartments.size() + " apartments to announcement: " + announcementId;
    }

    @Override
    @Transactional
    public String unlinkAnnouncementsFromApartments(Long announcementId, List<String> apartmentSignature) throws AnnouncementNotFoundException {

        Announcement announcement = announcementRepository.findByIdWithApartments(announcementId)
                .orElseThrow(() -> new AnnouncementNotFoundException("Announcement: " + announcementId + " not found"));

        List<AnnouncementApartment> announcementApartmentsToRemove = announcement.getAnnouncementApartments().stream()
                .filter(announcementApartment -> apartmentSignature.contains(announcementApartment.getApartment().getApartmentSignature()))
                .toList();

        for (AnnouncementApartment aa : announcementApartmentsToRemove) {
            aa.removeAssociation();
        }

        // Zapisz ogłoszenie po usunięciu powiązań
        announcementRepository.save(announcement);

        return "Unlinked " + announcementApartmentsToRemove.size() + " apartments from announcement: " + announcementId;
    }

    @Override
    public PageResponse<AnnouncementResponse> getAnnouncementsByApartmentSignature(String apartmentSignature, int pageNo, int pageSize) throws ApartmentNotFoundException {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<AnnouncementApartment> announcementApartments = announcementApartmentRepository.findByApartmentSignature(apartmentSignature, pageable);

        return new PageResponse<>(
                announcementApartments.getNumber(),
                announcementApartments.getSize(),
                announcementApartments.getContent().stream()
                        .map(announcementApartment -> AnnouncementResponse.builder()
                                .id(announcementApartment.getAnnouncement().getId())
                                .title(announcementApartment.getAnnouncement().getTitle())
                                .content(announcementApartment.getAnnouncement().getContent())
                                .createdAt(announcementApartment.getAnnouncement().getCreatedAt())
                                .updatedAt(announcementApartment.getAnnouncement().getUpdatedAt())
                                .build())
                        .toList(),
                announcementApartments.isLast()
        );
    }

}

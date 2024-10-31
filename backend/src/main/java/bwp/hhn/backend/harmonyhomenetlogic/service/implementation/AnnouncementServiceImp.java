package bwp.hhn.backend.harmonyhomenetlogic.service.implementation;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.AnnouncementNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Announcement;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Apartment;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.AnnouncementApartment;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.AnnouncementRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.ApartmentsRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables.AnnouncementApartmentRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.AnnouncementService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.AnnouncementRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.DateRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.AnnouncementResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImp implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final ApartmentsRepository apartmentsRepository;
    private final AnnouncementApartmentRepository announcementApartmentRepository;

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

        return AnnouncementResponse.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .createdAt(announcement.getCreatedAt())
                .updatedAt(announcement.getUpdatedAt())
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

        UUID userId = announcementRequest.getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User: " + userId + " not found"));

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
    public List<AnnouncementResponse> getAllAnnouncements() {
        return announcementRepository.findAll().stream()
                .map(announcement -> AnnouncementResponse.builder()
                        .id(announcement.getId())
                        .title(announcement.getTitle())
                        .content(announcement.getContent())
                        .createdAt(announcement.getCreatedAt())
                        .updatedAt(announcement.getUpdatedAt())
                        .build())
                .toList();
    }

    @Override
    public List<AnnouncementResponse> getAnnouncementsByUserId(UUID userId) throws UserNotFoundException {

        return userRepository.findById(userId)
                .map(user -> announcementRepository.findByUserUuidID(userId).stream()
                        .map(announcement -> AnnouncementResponse.builder()
                                .id(announcement.getId())
                                .title(announcement.getTitle())
                                .content(announcement.getContent())
                                .createdAt(announcement.getCreatedAt())
                                .updatedAt(announcement.getUpdatedAt())
                                .build())
                        .toList())
                .orElseThrow(() -> new UserNotFoundException("User: " + userId + " not found"));

    }

    @Override
    public List<AnnouncementResponse> getAnnouncementsFromStartDateTOEndDate(DateRequest dateRequest) {

        List<Announcement> announcements = announcementRepository.findDistinctByCreatedAtOrUpdatedAtBetween(
                dateRequest.getStartDate(), dateRequest.getEndDate()
        );

        return announcements.stream()
                .map(announcement -> AnnouncementResponse.builder()
                        .id(announcement.getId())
                        .title(announcement.getTitle())
                        .content(announcement.getContent())
                        .createdAt(announcement.getCreatedAt())
                        .updatedAt(announcement.getUpdatedAt())
                        .build())
                .toList();
    }

    @Transactional
    public String linkAnnouncementsToApartments(Long announcementId, List<String> apartmentSignature) throws AnnouncementNotFoundException, ApartmentNotFoundException {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new AnnouncementNotFoundException("Announcement: " + announcementId + " not found"));

        List<AnnouncementApartment> newAnnouncementApartments = apartmentSignature.stream()
                .map(apartmentId -> {
                    Apartment apartment = apartmentsRepository.findByApartmentSignature(apartmentId)
                            .orElseThrow(() -> new ApartmentNotFoundException("Apartment: " + apartmentId + " not found"));

                    AnnouncementApartment newLink = AnnouncementApartment.builder()
                            .announcement(announcement)
                            .apartment(apartment)
                            .build();

                    if (apartment.getAnnouncementApartments() == null)
                        apartment.setAnnouncementApartments(new ArrayList<>());
                    apartment.getAnnouncementApartments().add(newLink);

                    return newLink;
                })
                .filter(newLink -> announcement.getAnnouncementApartments() == null || !announcement.getAnnouncementApartments().contains(newLink))
                .toList();


        if (announcement.getAnnouncementApartments() == null) announcement.setAnnouncementApartments(new ArrayList<>());
        announcement.getAnnouncementApartments().addAll(newAnnouncementApartments);

        announcementRepository.save(announcement);

        announcementApartmentRepository.saveAll(newAnnouncementApartments);

        return "Linked " + newAnnouncementApartments.size() + " apartments to announcement: " + announcementId;
    }

    @Override
    @Transactional
    public String unlinkAnnouncementsFromApartments(Long announcementId, List<String> apartmentSignature) throws AnnouncementNotFoundException {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new AnnouncementNotFoundException("Announcement: " + announcementId + " not found"));

        List<AnnouncementApartment> announcementApartmentsToRemove = announcement.getAnnouncementApartments().stream()
                .filter(announcementApartment -> apartmentSignature.contains(announcementApartment.getApartment().getApartmentSignature()))
                .toList();

        announcement.getAnnouncementApartments().removeAll(announcementApartmentsToRemove);

        announcementRepository.save(announcement);
        announcementApartmentRepository.deleteAll(announcementApartmentsToRemove);

        return "Unlinked " + announcementApartmentsToRemove.size() + " apartments from announcement: " + announcementId;
    }

}

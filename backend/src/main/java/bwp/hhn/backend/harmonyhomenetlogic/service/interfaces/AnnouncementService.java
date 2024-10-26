package bwp.hhn.backend.harmonyhomenetlogic.service.interfaces;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.AnnouncementNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.AnnouncementRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.DateRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.AnnouncementResponse;

import java.util.List;
import java.util.UUID;

public interface AnnouncementService {

    AnnouncementResponse createAnnouncement(AnnouncementRequest announcementRequest) throws UserNotFoundException;

    String deleteAnnouncement(Long announcementId) throws AnnouncementNotFoundException;

    AnnouncementResponse updateAnnouncement(Long announcementId, AnnouncementRequest announcementRequest) throws AnnouncementNotFoundException, UserNotFoundException;

    AnnouncementResponse getAnnouncement(Long announcementId) throws AnnouncementNotFoundException;

    List<AnnouncementResponse> getAllAnnouncements();

    List<AnnouncementResponse> getAnnouncementsByUserId(UUID userId) throws UserNotFoundException;

    List<AnnouncementResponse> getAnnouncementsFromStartDateTOEndDate(DateRequest dateRequest);

    String linkAnnouncementsToApartments(Long announcementId, List<UUID> apartmentIds) throws AnnouncementNotFoundException, ApartmentNotFoundException;

    String unlinkAnnouncementsFromApartments(Long announcementId, List<UUID> apartmentIds) throws AnnouncementNotFoundException;

}

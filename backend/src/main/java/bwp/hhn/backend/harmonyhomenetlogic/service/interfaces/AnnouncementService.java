package bwp.hhn.backend.harmonyhomenetlogic.service.interfaces;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.AnnouncementNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.AnnouncementRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.AnnouncementResponse;

import java.util.List;

public interface AnnouncementService {

    AnnouncementResponse createAnnouncement(AnnouncementRequest announcementRequest) throws UserNotFoundException;

    String deleteAnnouncement(Long announcementId) throws AnnouncementNotFoundException;

    AnnouncementResponse updateAnnouncement(Long announcementId, AnnouncementRequest announcementRequest) throws AnnouncementNotFoundException, UserNotFoundException;

    AnnouncementResponse getAnnouncement(Long announcementId) throws AnnouncementNotFoundException;

    PageResponse<AnnouncementResponse> getAllAnnouncements(int pageNo, int pageSize);

    String linkAnnouncementsToApartments(Long announcementId, List<String> apartmentSignature) throws AnnouncementNotFoundException, ApartmentNotFoundException;

    String unlinkAnnouncementsFromApartments(Long announcementId, List<String> apartmentSignature) throws AnnouncementNotFoundException;

    PageResponse<AnnouncementResponse> getAnnouncementsByApartmentSignature(String apartmentSignature, int pageNo, int pageSize) throws ApartmentNotFoundException;

}

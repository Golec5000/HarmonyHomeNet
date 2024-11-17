package bwp.hhn.backend.harmonyhomenetlogic.controller;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.AnnouncementNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.AnnouncementService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.AnnouncementRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.AnnouncementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bwp/hhn/api/v1/announcement")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    //GET
    @GetMapping("/get-announcement/{announcementId}")
    public ResponseEntity<AnnouncementResponse> getAnnouncement(@PathVariable Long announcementId) throws AnnouncementNotFoundException {
        return ResponseEntity.ok(announcementService.getAnnouncement(announcementId));
    }

    @GetMapping("/get-all-announcements")
    public ResponseEntity<PageResponse<AnnouncementResponse>> getAllAnnouncements(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        return ResponseEntity.ok(announcementService.getAllAnnouncements(pageNo, pageSize));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_EMPLOYEE') or hasRole('ROLE_OWNER')")
    @GetMapping("/get-announcements-by-apartment")
    public ResponseEntity<PageResponse<AnnouncementResponse>> getAnnouncementsByApartmentSignature(
            @RequestParam String apartmentSignature,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) throws ApartmentNotFoundException {
        return ResponseEntity.ok(announcementService.getAnnouncementsByApartmentSignature(apartmentSignature, pageNo, pageSize));
    }

    //POST
    @PostMapping("/create-announcement")
    public ResponseEntity<AnnouncementResponse> createAnnouncement(@RequestBody AnnouncementRequest request) throws UserNotFoundException {
        return ResponseEntity.ok(announcementService.createAnnouncement(request));
    }

    @PostMapping("/link-announcement-to-apartments/{announcementId}")
    public ResponseEntity<String> linkAnnouncementsToApartments(@PathVariable Long announcementId, @RequestBody List<String> apartmentSignature) throws AnnouncementNotFoundException, ApartmentNotFoundException {
        return ResponseEntity.ok(announcementService.linkAnnouncementsToApartments(announcementId, apartmentSignature));
    }

    //PUT
    @PutMapping("/update-announcement/{announcementId}")
    public ResponseEntity<AnnouncementResponse> updateAnnouncement(@PathVariable Long announcementId, @RequestBody AnnouncementRequest request) throws AnnouncementNotFoundException, UserNotFoundException {
        return ResponseEntity.ok(announcementService.updateAnnouncement(announcementId, request));
    }

    //DELETE
    @DeleteMapping("/delete-announcement/{announcementId}")
    public ResponseEntity<String> deleteAnnouncement(@PathVariable Long announcementId) throws AnnouncementNotFoundException {
        return ResponseEntity.ok(announcementService.deleteAnnouncement(announcementId));
    }

    @DeleteMapping("/unlink-announcement-from-apartments/{announcementId}")
    public ResponseEntity<String> unlinkAnnouncementsFromApartments(@PathVariable Long announcementId, @RequestBody List<String> apartmentSignature) throws AnnouncementNotFoundException {
        return ResponseEntity.ok(announcementService.unlinkAnnouncementsFromApartments(announcementId, apartmentSignature));
    }
}
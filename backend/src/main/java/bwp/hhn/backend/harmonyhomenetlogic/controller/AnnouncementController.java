package bwp.hhn.backend.harmonyhomenetlogic.controller;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.AnnouncementNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.AnnouncementService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.AnnouncementRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.DateRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.AnnouncementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<List<AnnouncementResponse>> getAllAnnouncements() {
        return ResponseEntity.ok(announcementService.getAllAnnouncements());
    }

    @GetMapping("/get-announcements-by-user")
    public ResponseEntity<List<AnnouncementResponse>> getAnnouncementsByUserId(@RequestParam UUID userId) throws UserNotFoundException {
        return ResponseEntity.ok(announcementService.getAnnouncementsByUserId(userId));
    }

    @GetMapping("/get-announcement-by-date-range")
    public ResponseEntity<List<AnnouncementResponse>> getAnnouncementsFromStartDateToEndDate(@RequestBody DateRequest dateRequest) {
        return ResponseEntity.ok(announcementService.getAnnouncementsFromStartDateTOEndDate(dateRequest));
    }

    @GetMapping("/get-announcements-by-apartment")
    public ResponseEntity<List<AnnouncementResponse>> getAnnouncementsByApartmentSignature(@RequestParam String apartmentSignature) throws ApartmentNotFoundException {
        return ResponseEntity.ok(announcementService.getAnnouncementsByApartmentSignature(apartmentSignature));
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
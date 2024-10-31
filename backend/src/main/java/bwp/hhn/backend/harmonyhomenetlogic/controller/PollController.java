package bwp.hhn.backend.harmonyhomenetlogic.controller;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.*;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.PollService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.VoteChoice;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PollRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.VoteRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.PollResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.VoteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bwp/hhn/api/v1/poll")
@RequiredArgsConstructor
public class PollController {

    private final PollService pollService;

    //GET
    @GetMapping("/get-all-polls")
    public ResponseEntity<List<PollResponse>> getAllPolls() {
        return ResponseEntity.ok(pollService.getAllPolls());
    }

    @GetMapping("/get-votes-from-poll")
    public ResponseEntity<List<VoteResponse>> getVotesFromPoll(@RequestParam UUID pollId) throws PollNotFoundException {
        return ResponseEntity.ok(pollService.getVotesFromPoll(pollId));
    }

    @GetMapping("/get-votes-by-user")
    public ResponseEntity<List<VoteResponse>> getVotesFromUser(@RequestParam UUID userId) throws UserNotFoundException {
        return ResponseEntity.ok(pollService.getVotesFromUser(userId));
    }

    @GetMapping("/get-poll")
    public ResponseEntity<PollResponse> getPoll(@RequestParam UUID pollId) throws PollNotFoundException {
        return ResponseEntity.ok(pollService.getPoll(pollId));
    }

    @GetMapping("/download-poll/{pollId}")
    public ResponseEntity<ByteArrayResource> downloadPoll(@PathVariable UUID pollId) throws PollNotFoundException {
        PollResponse pollResponse = pollService.downloadPoll(pollId);

        ByteArrayResource resource = new ByteArrayResource(pollResponse.uploadData());

        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pollResponse.pollName() + "\"")
                .body(resource);
    }

    //POST
    @PostMapping(value = "/create-poll/{employeeId}", consumes = "multipart/form-data")
    public ResponseEntity<PollResponse> createPoll(@RequestPart("data") PollRequest pollRequest, @PathVariable UUID employeeId, @RequestPart("file") MultipartFile file)
            throws UserNotFoundException, IllegalArgumentException, IOException {
        return ResponseEntity.ok(pollService.createPoll(pollRequest, employeeId, file));
    }

    //PUT
    @PutMapping("/delete-vote/{voteId}")
    public ResponseEntity<String> deleteVote(@PathVariable Long voteId) throws VoteNotFoundException {
        return ResponseEntity.ok(pollService.deleteVote(voteId));
    }

    @PutMapping("/vote")
    public ResponseEntity<VoteResponse> vote(@RequestBody VoteRequest voteRequest)
            throws UserNotFoundException, PollNotFoundException, PossessionHistoryNotFoundException, ApartmentNotFoundException {
        return ResponseEntity.ok(pollService.vote(voteRequest.getPollId(), voteRequest.getUserId(), voteRequest.getApartmentSignature(), voteRequest.getVoteChoice()));
    }

    //DELETE
    @DeleteMapping("/delete-poll")
    public ResponseEntity<String> deletePoll(@RequestParam UUID pollId) throws PollNotFoundException {
        return ResponseEntity.ok(pollService.deletePoll(pollId));
    }

}
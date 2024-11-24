package bwp.hhn.backend.harmonyhomenetlogic.controller;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.*;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.PollService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PollRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.VoteRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.PollResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.VoteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/bwp/hhn/api/v1/poll")
@RequiredArgsConstructor
public class PollController {

    private final PollService pollService;

    //GET
    @GetMapping("/get-all-polls")
    public ResponseEntity<PageResponse<PollResponse>> getAllPolls(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        return ResponseEntity.ok(pollService.getAllPolls(pageNo, pageSize));
    }

    @GetMapping("/get-votes-from-poll")
    public ResponseEntity<PageResponse<VoteResponse>> getVotesFromPoll(
            @RequestParam UUID pollId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) throws PollNotFoundException {
        return ResponseEntity.ok(pollService.getVotesFromPoll(pollId, pageNo, pageSize));
    }

    @GetMapping("/get-votes-by-user")
    public ResponseEntity<PageResponse<VoteResponse>> getVotesFromUser(
            @RequestParam UUID userId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) throws UserNotFoundException {
        return ResponseEntity.ok(pollService.getVotesFromUser(userId, pageNo, pageSize));
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
    @PostMapping(value = "/create-poll/{employeeId}")
    public ResponseEntity<PollResponse> createPoll(
            @RequestParam("pollName") String pollName, @RequestParam("content") String content,
            @RequestParam("endDate") Instant endDate, @RequestParam("minCurrentVotesCount") int minCurrentVotesCount,
            @RequestParam("minSummary") BigDecimal minSummary, @PathVariable UUID employeeId,
            @RequestPart("file") MultipartFile file) throws Exception {

        PollRequest pollRequest = PollRequest.builder()
                .pollName(pollName)
                .content(content)
                .endDate(endDate)
                .minCurrentVotesCount(minCurrentVotesCount)
                .minSummary(minSummary)
                .build();

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
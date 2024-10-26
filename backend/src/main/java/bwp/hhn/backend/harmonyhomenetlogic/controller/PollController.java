package bwp.hhn.backend.harmonyhomenetlogic.controller;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.*;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.PollService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PollRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.VoteRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.PollResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.VoteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bwp/hhn/api/v1/poll")
@RequiredArgsConstructor
public class PollController {

    private final PollService pollService;

    @GetMapping("/get-all-polls")
    public ResponseEntity<List<PollResponse>> getAllPolls() {
        return ResponseEntity.ok(pollService.getAllPolls());
    }

    @PostMapping("/create-poll")
    public ResponseEntity<PollResponse> createPoll(@RequestBody PollRequest pollRequest, @RequestParam UUID employeeId) throws UserNotFoundException, IllegalArgumentException {
        return ResponseEntity.ok(pollService.createPoll(pollRequest, employeeId));
    }

    @GetMapping("/get-poll/{pollId}")
    public ResponseEntity<PollResponse> getPoll(@PathVariable UUID pollId) throws PollNotFoundException {
        return ResponseEntity.ok(pollService.getPoll(pollId));
    }

    @DeleteMapping("/delete-poll/{pollId}")
    public ResponseEntity<String> deletePoll(@PathVariable UUID pollId) throws PollNotFoundException {
        return ResponseEntity.ok(pollService.deletePoll(pollId));
    }

    @PostMapping("/vote/{pollId}")
    public ResponseEntity<VoteResponse> vote(@PathVariable UUID pollId, @RequestParam UUID userId, @RequestParam UUID apartmentId, @RequestBody VoteRequest voteRequest) throws UserNotFoundException, PollNotFoundException, PossessionHistoryNotFoundException, ApartmentNotFoundException {
        return ResponseEntity.ok(pollService.vote(pollId, userId, apartmentId, voteRequest));
    }

    @GetMapping("/get-votes-from-poll/{pollId}")
    public ResponseEntity<List<VoteResponse>> getVotesFromPoll(@PathVariable UUID pollId) throws PollNotFoundException {
        return ResponseEntity.ok(pollService.getVotesFromPoll(pollId));
    }

    @GetMapping("/get-votes-by-user/{userId}")
    public ResponseEntity<List<VoteResponse>> getVotesFromUser(@PathVariable UUID userId) throws UserNotFoundException {
        return ResponseEntity.ok(pollService.getVotesFromUser(userId));
    }

    @DeleteMapping("/delete-vote/{voteId}")
    public ResponseEntity<String> deleteVote(@PathVariable Long voteId) throws VoteNotFoundException {
        return ResponseEntity.ok(pollService.deleteVote(voteId));
    }
}
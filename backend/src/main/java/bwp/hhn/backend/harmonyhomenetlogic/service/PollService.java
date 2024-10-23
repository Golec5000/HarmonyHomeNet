package bwp.hhn.backend.harmonyhomenetlogic.service;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.*;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PollRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.VoteRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.PollResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.VoteResponse;

import java.util.List;
import java.util.UUID;

public interface PollService {

    List<PollResponse> getAllPolls();

    PollResponse createPoll(PollRequest pollRequest, UUID employeeId) throws UserNotFoundException, IllegalArgumentException;

    PollResponse getPoll(UUID pollId) throws PollNotFoundException;

    String deletePoll(UUID pollId) throws PollNotFoundException;

    VoteResponse vote(UUID pollId, UUID userId, UUID apartmentId, VoteRequest voteRequest) throws UserNotFoundException, PollNotFoundException, PossessionHistoryNotFoundException, ApartmentNotFoundException;

    List<VoteResponse> getVotesFromPoll(UUID pollId) throws PollNotFoundException;

    List<VoteResponse> getVotesFromUser(UUID userId) throws UserNotFoundException;

    String deleteVote(Long voteId) throws VoteNotFoundException;

}

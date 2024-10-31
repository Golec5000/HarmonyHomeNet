package bwp.hhn.backend.harmonyhomenetlogic.service.interfaces;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.*;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.VoteChoice;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PollRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.PollResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.VoteResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface PollService {

    List<PollResponse> getAllPolls();

    PollResponse createPoll(PollRequest pollRequest, UUID employeeId, MultipartFile file) throws UserNotFoundException, IllegalArgumentException, IOException;

    PollResponse getPoll(UUID pollId) throws PollNotFoundException;

    String deletePoll(UUID pollId) throws PollNotFoundException;

    VoteResponse vote(UUID pollId, UUID userId, String apartmentSignature, VoteChoice voteChoice) throws UserNotFoundException, PollNotFoundException, PossessionHistoryNotFoundException, ApartmentNotFoundException;

    List<VoteResponse> getVotesFromPoll(UUID pollId) throws PollNotFoundException;

    List<VoteResponse> getVotesFromUser(UUID userId) throws UserNotFoundException;

    String deleteVote(Long voteId) throws VoteNotFoundException;

    PollResponse downloadPoll(UUID pollId) throws PollNotFoundException;

}

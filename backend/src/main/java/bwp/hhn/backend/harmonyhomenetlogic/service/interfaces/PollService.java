package bwp.hhn.backend.harmonyhomenetlogic.service.interfaces;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.*;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.VoteChoice;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PollRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.PollResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.VoteResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface PollService {

    PageResponse<PollResponse> getAllPolls(int pageNo, int pageSize);

    PollResponse createPoll(PollRequest pollRequest, UUID employeeId, MultipartFile file) throws UserNotFoundException, IllegalArgumentException, IOException;

    PollResponse getPoll(UUID pollId) throws PollNotFoundException;

    String deletePoll(UUID pollId) throws PollNotFoundException;

    VoteResponse vote(UUID pollId, UUID userId, String apartmentSignature, VoteChoice voteChoice) throws UserNotFoundException, PollNotFoundException, PossessionHistoryNotFoundException, ApartmentNotFoundException;

    PageResponse<VoteResponse> getVotesFromPoll(UUID pollId, int pageNo, int pageSize) throws PollNotFoundException;

    PageResponse<VoteResponse> getVotesFromUser(UUID userId, int pageNo, int pageSize) throws UserNotFoundException;

    String deleteVote(Long voteId) throws VoteNotFoundException;

    PollResponse downloadPoll(UUID pollId) throws PollNotFoundException;

}

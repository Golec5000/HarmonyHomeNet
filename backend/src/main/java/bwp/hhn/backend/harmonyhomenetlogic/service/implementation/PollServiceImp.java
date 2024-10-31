package bwp.hhn.backend.harmonyhomenetlogic.service.implementation;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.*;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Apartment;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Poll;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Vote;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.ApartmentsRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.PollRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.VoteRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables.PossessionHistoryRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.PollService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.VoteChoice;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PollRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.PollResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.VoteResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PollServiceImp implements PollService {

    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final PossessionHistoryRepository possessionHistoryRepository;
    private final ApartmentsRepository apartmentsRepository;


    @Override
    public List<PollResponse> getAllPolls() {
        return pollRepository.findAll().stream()
                .map(
                        poll -> PollResponse.builder()
                                .pollName(poll.getPollName())
                                .content(poll.getContent())
                                .createdAt(poll.getCreatedAt())
                                .endDate(poll.getEndDate())
                                .summary(poll.getSummary())
                                .build()
                )
                .toList();
    }

    @Override
    public PollResponse createPoll(PollRequest pollRequest, UUID employeeId, MultipartFile file) throws UserNotFoundException, IllegalArgumentException, IOException {

        User user = userRepository.findByIdAndRole(employeeId)
                .orElseThrow(() -> new UserNotFoundException("User: " + employeeId + " not found"));

        if (pollRequest.getEndDate().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("End date must be after current date");

        Poll poll = Poll.builder()
                .pollName(pollRequest.getPollName())
                .content(pollRequest.getContent())
                .uploadData(file.getBytes())
                .endDate(pollRequest.getEndDate())
                .summary(BigDecimal.ZERO)
                .user(user)
                .build();

        if (user.getPolls() == null) user.setPolls(new ArrayList<>());
        user.getPolls().add(poll);

        Poll saved = pollRepository.save(poll);

        return PollResponse.builder()
                .id(saved.getUuidID())
                .pollName(saved.getPollName())
                .createdAt(saved.getCreatedAt())
                .endDate(saved.getEndDate())
                .build();

    }

    @Override
    public PollResponse getPoll(UUID pollId) throws PollNotFoundException {
        return pollRepository.findById(pollId)
                .map(
                        poll -> PollResponse.builder()
                                .pollName(poll.getPollName())
                                .content(poll.getContent())
                                .createdAt(poll.getCreatedAt())
                                .endDate(poll.getEndDate())
                                .summary(poll.getSummary())
                                .build()
                )
                .orElseThrow(() -> new PollNotFoundException("Poll: " + pollId + " not found"));
    }

    @Override
    public String deletePoll(UUID pollId) throws PollNotFoundException {
        if (!pollRepository.existsByUuidID(pollId))
            throw new PollNotFoundException("Poll: " + pollId + " not found");

        pollRepository.deleteById(pollId);
        return "Poll: " + pollId + " deleted";
    }

    @Override
    @Transactional
    public VoteResponse vote(UUID pollId, UUID userId, String apartmentSignature, VoteChoice voteChoice) throws UserNotFoundException, PollNotFoundException, PossessionHistoryNotFoundException, ApartmentNotFoundException {

        User user = userRepository.findByIdAndRoleUser(userId)
                .orElseThrow(() -> new UserNotFoundException("User: " + userId + " not found"));

        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new PollNotFoundException("Poll: " + pollId + " not found"));

        Apartment apartment = apartmentsRepository.findByApartmentSignature(apartmentSignature)
                .orElseThrow(() -> new ApartmentNotFoundException("Apartment: " + apartmentSignature + " not found"));

        if (poll.getEndDate().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("Poll: " + pollId + " has ended");

        if (!possessionHistoryRepository.existsByUserUuidIDAndApartmentUuidID(userId, apartment.getUuidID()))
            throw new IllegalArgumentException("User: " + userId + " does not own apartment: " + apartment.getUuidID());

        if (voteRepository.existsByPollUuidIDAndUserUuidIDAndApartmentSignature(pollId, userId, apartmentSignature))
            throw new IllegalArgumentException("For apartment: " + apartmentSignature + " in poll: " + pollId + " owners have already voted");

        Vote vote = Vote.builder()
                .voteChoice(voteChoice)
                .createdAt(LocalDateTime.now())
                .user(user)
                .poll(poll)
                .apartmentSignature(apartmentSignature)
                .build();

        if (user.getVotes() == null) user.setVotes(new ArrayList<>());
        user.getVotes().add(vote);

        if (poll.getVotes() == null) poll.setVotes(new ArrayList<>());
        poll.getVotes().add(vote);

        Vote saved = voteRepository.save(vote);

        recalculateSummary(poll);

        return VoteResponse.builder()
                .id(saved.getId())
                .voteChoice(saved.getVoteChoice())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    public List<VoteResponse> getVotesFromPoll(UUID pollId) throws PollNotFoundException {
        return pollRepository.findById(pollId)
                .map(
                        poll -> poll.getVotes().stream()
                                .map(
                                        vote -> VoteResponse.builder()
                                                .voteChoice(vote.getVoteChoice())
                                                .createdAt(vote.getCreatedAt())
                                                .build()
                                )
                                .toList()
                )
                .orElseThrow(() -> new PollNotFoundException("Poll: " + pollId + " not found"));
    }

    @Override
    public List<VoteResponse> getVotesFromUser(UUID userId) throws UserNotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User: " + userId + " not found");
        }
        return voteRepository.findVotesByUserId(userId);
    }

    @Override
    public String deleteVote(Long voteId) throws VoteNotFoundException {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new VoteNotFoundException("Vote: " + voteId + " not found"));

        Poll poll = vote.getPoll();

        voteRepository.deleteById(voteId);

        // Recalculate summary after removing a vote
        recalculateSummary(poll);

        return "Vote: " + voteId + " deleted";
    }

    @Override
    public PollResponse downloadPoll(UUID pollId) throws PollNotFoundException {
        return pollRepository.findById(pollId)
                .map(
                        poll -> PollResponse.builder()
                                .pollName(poll.getPollName())
                                .uploadData(poll.getUploadData())
                                .build()
                )
                .orElseThrow(() -> new PollNotFoundException("Poll: " + pollId + " not found"));
    }

    private void recalculateSummary(Poll poll) {
        BigDecimal newSummary = poll.getVotes().stream()
                .filter(vote -> vote.getVoteChoice() == VoteChoice.FOR)
                .map(vote -> apartmentsRepository.findByApartmentSignature(vote.getApartmentSignature())
                        .map(Apartment::getApartmentPercentValue)
                        .orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        poll.setSummary(newSummary);
        pollRepository.save(poll);
    }

}

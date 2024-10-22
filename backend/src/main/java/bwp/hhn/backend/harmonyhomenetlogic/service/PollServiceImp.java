package bwp.hhn.backend.harmonyhomenetlogic.service;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.PollNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.PossessionHistoryNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.VoteNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Apartments;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Poll;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Vote;
import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.PossessionHistory;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.ApartmentsRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.PollRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.VoteRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables.PossessionHistoryRepository;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PollRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.VoteRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.PollResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.VoteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                                .uploadData(poll.getUploadData())
                                .createdAt(poll.getCreatedAt())
                                .endDate(poll.getEndDate())
                                .build()
                )
                .toList();
    }

    @Override
    public PollResponse createPoll(PollRequest pollRequest, UUID employeeId) throws UserNotFoundException, IllegalArgumentException {

        User user = userRepository.findByIdAndRole(employeeId)
                .orElseThrow(() -> new UserNotFoundException("User: " + employeeId + " not found"));

        if (pollRequest.getEndDate().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("End date must be after current date");

        Poll poll = Poll.builder()
                .pollName(pollRequest.getPollName())
                .content(pollRequest.getContent())
                .uploadData(pollRequest.getUploadData())
                .endDate(pollRequest.getEndDate())
                .user(user)
                .build();

        if (user.getPolls() == null) user.setPolls(new ArrayList<>());
        user.getPolls().add(poll);

        userRepository.save(user);
        pollRepository.save(poll);

        return PollResponse.builder()
                .pollName(poll.getPollName())
                .content(poll.getContent())
                .uploadData(poll.getUploadData())
                .createdAt(poll.getCreatedAt())
                .endDate(poll.getEndDate())
                .build();

    }

    @Override
    public PollResponse getPoll(UUID pollId) throws PollNotFoundException {
        return pollRepository.findById(pollId)
                .map(
                        poll -> PollResponse.builder()
                                .pollName(poll.getPollName())
                                .content(poll.getContent())
                                .uploadData(poll.getUploadData())
                                .createdAt(poll.getCreatedAt())
                                .endDate(poll.getEndDate())
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
    public VoteResponse vote(UUID pollId, UUID userId, VoteRequest voteRequest) throws UserNotFoundException, PollNotFoundException, PossessionHistoryNotFoundException {

        User user = userRepository.findByIdAndRoleUser(userId)
                .orElseThrow(() -> new UserNotFoundException("User: " + userId + " not found"));

        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new PollNotFoundException("Poll: " + pollId + " not found"));

        if (poll.getEndDate().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("Poll: " + pollId + " has ended");

        // Sprawdź czy użytkownik ma przypisane jakieś mieszkanie
        List<PossessionHistory> possessionHistory = possessionHistoryRepository.findByUserUuidID(userId);

        if (possessionHistory.isEmpty())
            throw new PossessionHistoryNotFoundException("User: " + userId + " does not have any associated apartments.");

        // Sprawdź czy dany użytkownik nie oddał już głosu dla swojego mieszkania w tej ankiecie
        for (PossessionHistory possession : possessionHistory) {
            boolean hasVoted = voteRepository.existsByPollUuidIDAndUserUuidIDAndApartmentUUID(pollId, userId, possession.getApartment().getUuidID());

            if (hasVoted)
                throw new IllegalArgumentException("User: " + userId + " has already voted for apartment: " + possession.getApartment().getUuidID());

            Vote vote = Vote.builder()
                    .voteChoice(voteRequest.getVoteChoice())
                    .createdAt(LocalDateTime.now())
                    .user(user)
                    .poll(poll)
                    .build();

            if (user.getVotes() == null) user.setVotes(new ArrayList<>());
            user.getVotes().add(vote);

            if (poll.getVotes() == null) poll.setVotes(new ArrayList<>());
            poll.getVotes().add(vote);

            userRepository.save(user);
            pollRepository.save(poll);

            voteRepository.save(vote);

            return VoteResponse.builder()
                    .voteChoice(vote.getVoteChoice())
                    .createdAt(vote.getCreatedAt())
                    .build();
        }
        throw new IllegalArgumentException("No valid apartment to vote for.");
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
        return userRepository.findById(userId)
                .map(
                        user -> user.getVotes().stream()
                                .map(
                                        vote -> VoteResponse.builder()
                                                .voteChoice(vote.getVoteChoice())
                                                .createdAt(vote.getCreatedAt())
                                                .build()
                                )
                                .toList()
                )
                .orElseThrow(() -> new UserNotFoundException("User: " + userId + " not found"));
    }

    @Override
    public String deleteVote(Long voteId) throws VoteNotFoundException {

        if (!voteRepository.existsById(voteId)) throw new VoteNotFoundException("Vote: " + voteId + " not found");

        voteRepository.deleteById(voteId);
        return "Vote: " + voteId + " deleted";
    }

    @Override
    public String summaryPoll(UUID pollId) throws PollNotFoundException {

        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new PollNotFoundException("Poll: " + pollId + " not found"));

        List<Vote> votes = poll.getVotes();

        BigDecimal totalVotes = new BigDecimal(0);

        for (Vote vote : votes) {
            totalVotes = totalVotes.add(
                    apartmentsRepository.findById(vote.getApartmentUUID())
                            .map(Apartments::getApartmentPercentValue)
                            .orElse(BigDecimal.ZERO)
            );
        }

        poll.setSummary(totalVotes);

        pollRepository.save(poll);

        return "Poll: " + pollId + " summary updated";

    }
}

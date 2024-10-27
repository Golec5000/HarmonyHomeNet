package bwp.hhn.backend.harmonyhomenetlogic.serviceTests;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.*;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.*;
import bwp.hhn.backend.harmonyhomenetlogic.entity.sideTables.PossessionHistory;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.*;
import bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables.PossessionHistoryRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.implementation.PollServiceImp;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.VoteChoice;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.PollResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.VoteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PollServiceTest {

    @Mock
    private PollRepository pollRepository;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PossessionHistoryRepository possessionHistoryRepository;

    @Mock
    private ApartmentsRepository apartmentsRepository;

    @InjectMocks
    private PollServiceImp pollService;

    private User user;
    private Poll poll;
    private UUID userId;
    private UUID pollId;
    private UUID apartmentId;
    private Apartment apartment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        pollId = UUID.randomUUID();
        apartmentId = UUID.randomUUID();

        user = User.builder()
                .uuidID(userId)
                .role(Role.USER)
                .votes(new ArrayList<>())
                .build();

        poll = Poll.builder()
                .uuidID(pollId)
                .pollName("Test Poll")
                .content("Test Content")
                .uploadData("Test Data".getBytes())
                .createdAt(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .summary(BigDecimal.ZERO)
                .votes(new ArrayList<>())
                .build();

        apartment = Apartment.builder()
                .uuidID(apartmentId)
                .apartmentPercentValue(new BigDecimal("0.05"))
                .build();
    }

    @Test
    void testGetAllPolls() {
        when(pollRepository.findAll()).thenReturn(Collections.singletonList(poll));

        List<PollResponse> responses = pollService.getAllPolls();

        assertEquals(1, responses.size());
        assertEquals("Test Poll", responses.get(0).pollName());
        verify(pollRepository, times(1)).findAll();
    }



    @Test
    void testGetPoll_Success() throws PollNotFoundException {
        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));

        PollResponse response = pollService.getPoll(pollId);

        assertEquals("Test Poll", response.pollName());
        verify(pollRepository, times(1)).findById(pollId);
    }

    @Test
    void testGetPoll_NotFound() {
        when(pollRepository.findById(pollId)).thenReturn(Optional.empty());

        assertThrows(PollNotFoundException.class, () -> pollService.getPoll(pollId));
        verify(pollRepository, times(1)).findById(pollId);
    }

    @Test
    void testDeletePoll_Success() throws PollNotFoundException {
        when(pollRepository.existsByUuidID(pollId)).thenReturn(true);

        String result = pollService.deletePoll(pollId);

        assertEquals("Poll: " + pollId + " deleted", result);
        verify(pollRepository, times(1)).existsByUuidID(pollId);
        verify(pollRepository, times(1)).deleteById(pollId);
    }

    @Test
    void testDeletePoll_NotFound() {
        when(pollRepository.existsByUuidID(pollId)).thenReturn(false);

        assertThrows(PollNotFoundException.class, () -> pollService.deletePoll(pollId));
        verify(pollRepository, times(1)).existsByUuidID(pollId);
        verify(pollRepository, times(0)).deleteById(pollId);
    }




    @Test
    void testGetVotesFromPoll_Success() throws PollNotFoundException {
        Vote vote = Vote.builder()
                .voteChoice(VoteChoice.FOR)
                .createdAt(LocalDateTime.now())
                .build();

        poll.setVotes(Collections.singletonList(vote));

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));

        List<VoteResponse> responses = pollService.getVotesFromPoll(pollId);

        assertEquals(1, responses.size());
        assertEquals(VoteChoice.FOR, responses.get(0).voteChoice());
        verify(pollRepository, times(1)).findById(pollId);
    }

    @Test
    void testGetVotesFromPoll_NotFound() {
        when(pollRepository.findById(pollId)).thenReturn(Optional.empty());

        assertThrows(PollNotFoundException.class, () -> pollService.getVotesFromPoll(pollId));
        verify(pollRepository, times(1)).findById(pollId);
    }

    @Test
    void testGetVotesFromUser_Success() throws UserNotFoundException {
        Vote vote = Vote.builder()
                .voteChoice(VoteChoice.AGAINST)
                .createdAt(LocalDateTime.now())
                .build();

        user.setVotes(Collections.singletonList(vote));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        List<VoteResponse> responses = pollService.getVotesFromUser(userId);

        assertEquals(1, responses.size());
        assertEquals(VoteChoice.AGAINST, responses.get(0).voteChoice());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetVotesFromUser_NotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> pollService.getVotesFromUser(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testDeleteVote_Success() throws VoteNotFoundException {
        Long voteId = 1L;
        when(voteRepository.existsById(voteId)).thenReturn(true);

        String result = pollService.deleteVote(voteId);

        assertEquals("Vote: " + voteId + " deleted", result);
        verify(voteRepository, times(1)).existsById(voteId);
        verify(voteRepository, times(1)).deleteById(voteId);
    }

    @Test
    void testDeleteVote_NotFound() {
        Long voteId = 1L;
        when(voteRepository.existsById(voteId)).thenReturn(false);

        assertThrows(VoteNotFoundException.class, () -> pollService.deleteVote(voteId));
        verify(voteRepository, times(1)).existsById(voteId);
        verify(voteRepository, times(0)).deleteById(voteId);
    }
}

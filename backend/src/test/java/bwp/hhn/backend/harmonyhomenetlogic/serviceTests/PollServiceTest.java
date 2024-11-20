package bwp.hhn.backend.harmonyhomenetlogic.serviceTests;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.PollNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.UserNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.VoteNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Apartment;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Poll;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Vote;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.ApartmentsRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.PollRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.VoteRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables.PossessionHistoryRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.implementation.PollServiceImp;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.VoteChoice;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PollRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.PollResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.VoteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

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
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        pollId = UUID.randomUUID();
        apartmentId = UUID.randomUUID();

        user = User.builder()
                .uuidID(userId)
                .role(Role.ROLE_EMPLOYEE)
                .polls(new ArrayList<>())
                .votes(new ArrayList<>())
                .build();

        poll = Poll.builder()
                .uuidID(pollId)
                .pollName("Test Poll")
                .content("Test Content")
                .uploadData("Test Data".getBytes())
                .createdAt(Instant.now())
                .endDate(Instant.now().plus(1, ChronoUnit.DAYS))
                .summary(BigDecimal.ZERO)
                .votes(new ArrayList<>())
                .build();

        apartment = Apartment.builder()
                .uuidID(apartmentId)
                .apartmentSignature("A-101")
                .apartmentPercentValue(new BigDecimal("0.05"))
                .build();

        file = mock(MultipartFile.class);
    }

//    @Test
//    void testGetAllPolls() {
//        when(pollRepository.findAll()).thenReturn(Collections.singletonList(poll));
//
//        List<PollResponse> responses = pollService.getAllPolls();
//
//        assertEquals(1, responses.size());
//        assertEquals("Test Poll", responses.get(0).pollName());
//        verify(pollRepository, times(1)).findAll();
//    }

    @Test
    void testCreatePoll_Success() throws UserNotFoundException, IOException {
        PollRequest pollRequest = new PollRequest();
        pollRequest.setPollName("New Poll");
        pollRequest.setContent("Poll Content");
        pollRequest.setEndDate(Instant.now().plus(5, ChronoUnit.DAYS));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(file.getBytes()).thenReturn("File Data".getBytes());
        when(pollRepository.save(any(Poll.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PollResponse response = pollService.createPoll(pollRequest, userId, file);

        assertNotNull(response);
        assertEquals("New Poll", response.pollName());

        verify(userRepository, times(1)).findById(userId);
        verify(file, times(1)).getBytes();
        verify(pollRepository, times(1)).save(any(Poll.class));
    }

    @Test
    void testCreatePoll_UserNotFound() throws IOException {
        PollRequest pollRequest = new PollRequest();
        pollRequest.setPollName("New Poll");
        pollRequest.setContent("Poll Content");
        pollRequest.setEndDate(Instant.now().plus(5, ChronoUnit.DAYS));

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> pollService.createPoll(pollRequest, userId, file));

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(file);
        verifyNoInteractions(pollRepository);
    }

    @Test
    void testCreatePoll_EndDateBeforeNow() throws UserNotFoundException, IOException {
        PollRequest pollRequest = new PollRequest();
        pollRequest.setPollName("New Poll");
        pollRequest.setContent("Poll Content");
        pollRequest.setEndDate(Instant.now().minus(1, ChronoUnit.DAYS));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> pollService.createPoll(pollRequest, userId, file));

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(file);
        verifyNoInteractions(pollRepository);
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
        verify(pollRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void testVote_Success() throws UserNotFoundException, PollNotFoundException, ApartmentNotFoundException {
        UUID ownerId = UUID.randomUUID();
        User owner = User.builder()
                .uuidID(ownerId)
                .role(Role.ROLE_OWNER)
                .votes(new ArrayList<>())
                .build();

        VoteChoice voteChoice = VoteChoice.FOR;
        String apartmentSignature = "A-101";

        when(userRepository.findByIdAndRoleUser(ownerId)).thenReturn(Optional.of(owner));
        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(apartmentsRepository.findByApartmentSignature(apartmentSignature)).thenReturn(Optional.of(apartment));
        when(possessionHistoryRepository.existsByUserUuidIDAndApartmentUuidID(ownerId, apartment.getUuidID())).thenReturn(true);
        when(voteRepository.existsByPollUuidIDAndApartmentSignature(pollId, apartmentSignature)).thenReturn(false);
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pollRepository.save(any(Poll.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VoteResponse response = pollService.vote(pollId, ownerId, apartmentSignature, voteChoice);

        assertNotNull(response);
        assertEquals(VoteChoice.FOR, response.voteChoice());

        verify(userRepository, times(1)).findByIdAndRoleUser(ownerId);
        verify(pollRepository, times(1)).findById(pollId);
        verify(apartmentsRepository, times(2)).findByApartmentSignature(apartmentSignature);
        verify(possessionHistoryRepository, times(1)).existsByUserUuidIDAndApartmentUuidID(ownerId, apartment.getUuidID());
        verify(voteRepository, times(1)).existsByPollUuidIDAndApartmentSignature(pollId, apartmentSignature);
        verify(voteRepository, times(1)).save(any(Vote.class));
        verify(pollRepository, times(1)).save(any(Poll.class));
    }

    @Test
    void testVote_PollEnded() throws UserNotFoundException, PollNotFoundException, ApartmentNotFoundException {
        UUID ownerId = UUID.randomUUID();
        User owner = User.builder()
                .uuidID(ownerId)
                .role(Role.ROLE_OWNER)
                .votes(new ArrayList<>())
                .build();

        poll.setEndDate(Instant.now().minus(1, ChronoUnit.DAYS)); // Poll has ended

        String apartmentSignature = "A-101";

        when(userRepository.findByIdAndRoleUser(ownerId)).thenReturn(Optional.of(owner));
        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(apartmentsRepository.findByApartmentSignature(apartmentSignature)).thenReturn(Optional.of(apartment));

        assertThrows(IllegalArgumentException.class, () -> pollService.vote(pollId, ownerId, apartmentSignature, VoteChoice.FOR));

        verify(userRepository, times(1)).findByIdAndRoleUser(ownerId);
        verify(pollRepository, times(1)).findById(pollId);
        verify(apartmentsRepository, times(1)).findByApartmentSignature(apartmentSignature);
        verifyNoMoreInteractions(apartmentsRepository);
        verifyNoMoreInteractions(possessionHistoryRepository);
        verifyNoMoreInteractions(voteRepository);
    }

//    @Test
//    void testGetVotesFromPoll_Success() throws PollNotFoundException {
//        Vote vote = Vote.builder()
//                .voteChoice(VoteChoice.FOR)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        poll.setVotes(Collections.singletonList(vote));
//
//        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));
//
//        List<VoteResponse> responses = pollService.getVotesFromPoll(pollId);
//
//        assertEquals(1, responses.size());
//        assertEquals(VoteChoice.FOR, responses.get(0).voteChoice());
//        verify(pollRepository, times(1)).findById(pollId);
//    }

//    @Test
//    void testGetVotesFromPoll_NotFound() {
//        when(pollRepository.findById(pollId)).thenReturn(Optional.empty());
//
//        assertThrows(PollNotFoundException.class, () -> pollService.getVotesFromPoll(pollId));
//        verify(pollRepository, times(1)).findById(pollId);
//    }
//
//    @Test
//    void testGetVotesFromUser_NotFound() {
//        when(userRepository.existsById(userId)).thenReturn(false);
//
//        assertThrows(UserNotFoundException.class, () -> pollService.getVotesFromUser(userId));
//        verify(userRepository, times(1)).existsById(userId);
//        verifyNoInteractions(voteRepository);
//    }

    @Test
    void testDeleteVote_Success() throws VoteNotFoundException {
        Long voteId = 1L;
        Vote vote = Vote.builder()
                .id(voteId)
                .poll(poll)
                .build();

        when(voteRepository.findById(voteId)).thenReturn(Optional.of(vote));
        doNothing().when(voteRepository).deleteById(voteId);
        when(pollRepository.save(any(Poll.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = pollService.deleteVote(voteId);

        assertEquals("Vote: " + voteId + " deleted", result);
        verify(voteRepository, times(1)).findById(voteId);
        verify(voteRepository, times(1)).deleteById(voteId);
        verify(pollRepository, times(1)).save(any(Poll.class));
    }

    @Test
    void testDeleteVote_NotFound() {
        Long voteId = 1L;
        when(voteRepository.findById(voteId)).thenReturn(Optional.empty());

        assertThrows(VoteNotFoundException.class, () -> pollService.deleteVote(voteId));
        verify(voteRepository, times(1)).findById(voteId);
        verify(voteRepository, never()).deleteById(anyLong());
        verifyNoMoreInteractions(pollRepository);
    }

    @Test
    void testDownloadPoll_Success() throws PollNotFoundException {
        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));

        PollResponse response = pollService.downloadPoll(pollId);

        assertEquals("Test Poll", response.pollName());
        assertArrayEquals("Test Data".getBytes(), response.uploadData());
        verify(pollRepository, times(1)).findById(pollId);
    }

    @Test
    void testDownloadPoll_NotFound() {
        when(pollRepository.findById(pollId)).thenReturn(Optional.empty());

        assertThrows(PollNotFoundException.class, () -> pollService.downloadPoll(pollId));
        verify(pollRepository, times(1)).findById(pollId);
    }
}

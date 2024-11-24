package bwp.hhn.backend.harmonyhomenetlogic.serviceTests;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.*;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.*;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.*;
import bwp.hhn.backend.harmonyhomenetlogic.repository.sideTables.PossessionHistoryRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.implementation.PollServiceImp;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.MailService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.Role;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.VoteChoice;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PollRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.PollResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.VoteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.*;
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

    @Mock
    private MailService mailService;

    @InjectMocks
    private PollServiceImp pollService;

    private User user;
    private Poll poll;
    private UUID userId;
    private UUID pollId;
    private Apartment apartment;
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        pollId = UUID.randomUUID();
        UUID apartmentId = UUID.randomUUID();

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
                .endDate(Instant.now().plus(Duration.ofDays(1)))
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

    @Test
    void testGetAllPolls_Success() {
        // Given
        int pageNo = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        List<Poll> polls = Collections.singletonList(poll);
        Page<Poll> pollPage = new PageImpl<>(polls, pageable, polls.size());

        when(pollRepository.findAll(pageable)).thenReturn(pollPage);

        // When
        PageResponse<PollResponse> responses = pollService.getAllPolls(pageNo, pageSize);

        // Then
        assertEquals(1, responses.content().size());
        assertEquals("Test Poll", responses.content().get(0).pollName());
        verify(pollRepository, times(1)).findAll(pageable);
    }

    @Test
    void testCreatePoll_Success() throws UserNotFoundException, IOException {
        // Given
        PollRequest pollRequest = new PollRequest();
        pollRequest.setPollName("New Poll");
        pollRequest.setContent("Poll Content");
        pollRequest.setEndDate(Instant.now().plus(Duration.ofDays(5)));
        pollRequest.setMinCurrentVotesCount(1);
        pollRequest.setMinSummary(BigDecimal.valueOf(50));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(file.getOriginalFilename()).thenReturn("document.pdf");
        when(file.getBytes()).thenReturn("File Data".getBytes());
        when(pollRepository.save(any(Poll.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(apartmentsRepository.count()).thenReturn(10L);
        when(possessionHistoryRepository.findAllUniqueOwners()).thenReturn(Collections.singletonList(user));

        // When
        PollResponse response = pollService.createPoll(pollRequest, userId, file);

        // Then
        assertNotNull(response);
        assertEquals("New Poll", response.pollName());

        verify(userRepository, times(1)).findById(userId);
        verify(file, times(2)).getOriginalFilename(); // Expecting 2 invocations
        verify(file, times(1)).getBytes();
        verify(pollRepository, times(1)).save(any(Poll.class));
        verify(mailService, times(1)).sendNotificationMail(anyString(), anyString(), eq(user.getEmail()));
    }

    @Test
    void testCreatePoll_UserNotFound() throws IOException {
        // Given
        PollRequest pollRequest = new PollRequest();
        pollRequest.setPollName("New Poll");
        pollRequest.setContent("Poll Content");
        pollRequest.setEndDate(Instant.now().plus(Duration.ofDays(5)));
        pollRequest.setMinCurrentVotesCount(1);
        pollRequest.setMinSummary(BigDecimal.valueOf(50));

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> pollService.createPoll(pollRequest, userId, file));

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(file);
        verifyNoInteractions(pollRepository);
    }

    @Test
    void testCreatePoll_InvalidEndDate() throws UserNotFoundException {
        // Given
        PollRequest pollRequest = new PollRequest();
        pollRequest.setPollName("New Poll");
        pollRequest.setContent("Poll Content");
        pollRequest.setEndDate(Instant.now().minus(Duration.ofDays(1)));
        pollRequest.setMinCurrentVotesCount(1);
        pollRequest.setMinSummary(BigDecimal.valueOf(50));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> pollService.createPoll(pollRequest, userId, file));

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(file);
        verifyNoInteractions(pollRepository);
    }

    @Test
    void testGetPoll_Success() throws PollNotFoundException {
        // Given
        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));

        // When
        PollResponse response = pollService.getPoll(pollId);

        // Then
        assertEquals("Test Poll", response.pollName());
        verify(pollRepository, times(1)).findById(pollId);
    }

    @Test
    void testGetPoll_NotFound() {
        // Given
        when(pollRepository.findById(pollId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(PollNotFoundException.class, () -> pollService.getPoll(pollId));
        verify(pollRepository, times(1)).findById(pollId);
    }

    @Test
    void testDeletePoll_Success() throws PollNotFoundException {
        // Given
        when(pollRepository.existsByUuidID(pollId)).thenReturn(true);

        // When
        String result = pollService.deletePoll(pollId);

        // Then
        assertEquals("Poll: " + pollId + " deleted", result);
        verify(pollRepository, times(1)).existsByUuidID(pollId);
        verify(pollRepository, times(1)).deleteById(pollId);
    }

    @Test
    void testDeletePoll_NotFound() {
        // Given
        when(pollRepository.existsByUuidID(pollId)).thenReturn(false);

        // When & Then
        assertThrows(PollNotFoundException.class, () -> pollService.deletePoll(pollId));
        verify(pollRepository, times(1)).existsByUuidID(pollId);
        verify(pollRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void testVote_Success() throws UserNotFoundException, PollNotFoundException, ApartmentNotFoundException {
        // Given
        UUID ownerId = UUID.randomUUID();
        User owner = User.builder()
                .uuidID(ownerId)
                .role(Role.ROLE_OWNER)
                .votes(new ArrayList<>())
                .build();

        VoteChoice voteChoice = VoteChoice.FOR;
        String apartmentSignature = "A-101";

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(apartmentsRepository.findByApartmentSignature(apartmentSignature)).thenReturn(Optional.of(apartment));
        when(possessionHistoryRepository.existsByUserUuidIDAndApartmentUuidID(ownerId, apartment.getUuidID())).thenReturn(true);
        when(voteRepository.existsByPollUuidIDAndApartmentSignature(pollId, apartmentSignature)).thenReturn(false);
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pollRepository.save(any(Poll.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(apartmentsRepository.findByApartmentSignature(apartmentSignature)).thenReturn(Optional.of(apartment));

        // When
        VoteResponse response = pollService.vote(pollId, ownerId, apartmentSignature, voteChoice);

        // Then
        assertNotNull(response);
        assertEquals(VoteChoice.FOR, response.voteChoice());

        verify(userRepository, times(1)).findById(ownerId);
        verify(pollRepository, times(1)).findById(pollId);
        verify(apartmentsRepository, times(2)).findByApartmentSignature(apartmentSignature);
        verify(possessionHistoryRepository, times(1)).existsByUserUuidIDAndApartmentUuidID(ownerId, apartment.getUuidID());
        verify(voteRepository, times(1)).existsByPollUuidIDAndApartmentSignature(pollId, apartmentSignature);
        verify(voteRepository, times(1)).save(any(Vote.class));
        verify(pollRepository, times(1)).save(any(Poll.class));
    }

    @Test
    void testVote_PollEnded() throws UserNotFoundException, PollNotFoundException, ApartmentNotFoundException {
        // Given
        UUID ownerId = UUID.randomUUID();
        User owner = User.builder()
                .uuidID(ownerId)
                .role(Role.ROLE_OWNER)
                .votes(new ArrayList<>())
                .build();

        poll.setEndDate(Instant.now().minus(Duration.ofDays(1))); // Poll has ended

        String apartmentSignature = "A-101";

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(apartmentsRepository.findByApartmentSignature(apartmentSignature)).thenReturn(Optional.of(apartment));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> pollService.vote(pollId, ownerId, apartmentSignature, VoteChoice.FOR));

        verify(userRepository, times(1)).findById(ownerId);
        verify(pollRepository, times(1)).findById(pollId);
        verify(apartmentsRepository, times(1)).findByApartmentSignature(apartmentSignature);
        verifyNoMoreInteractions(apartmentsRepository);
        verifyNoMoreInteractions(possessionHistoryRepository);
        verifyNoMoreInteractions(voteRepository);
    }

    @Test
    void testGetVotesFromPoll_Success() throws PollNotFoundException {
        // Given
        int pageNo = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Vote vote = Vote.builder()
                .id(1L)
                .voteChoice(VoteChoice.FOR)
                .createdAt(Instant.now())
                .apartmentSignature("A-101")
                .build();

        List<Vote> votes = Collections.singletonList(vote);
        Page<Vote> votePage = new PageImpl<>(votes, pageable, votes.size());

        when(voteRepository.findVotesByPollUuidID(pollId, pageable)).thenReturn(votePage);

        // When
        PageResponse<VoteResponse> responses = pollService.getVotesFromPoll(pollId, pageNo, pageSize);

        // Then
        assertEquals(1, responses.content().size());
        assertEquals(VoteChoice.FOR, responses.content().get(0).voteChoice());

        verify(voteRepository, times(1)).findVotesByPollUuidID(pollId, pageable);
    }

    @Test
    void testGetVotesFromPoll_PollNotFound() {
        // Given
        int pageNo = 0;
        int pageSize = 10;

        when(voteRepository.findVotesByPollUuidID(pollId, PageRequest.of(pageNo, pageSize))).thenReturn(Page.empty());

        // When & Then
        PageResponse<VoteResponse> responses = pollService.getVotesFromPoll(pollId, pageNo, pageSize);

        assertNotNull(responses);
        assertEquals(0, responses.content().size());

        verify(voteRepository, times(1)).findVotesByPollUuidID(pollId, PageRequest.of(pageNo, pageSize));
    }

    @Test
    void testGetVotesFromUser_Success() throws UserNotFoundException {
        // Given
        int pageNo = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Vote vote = Vote.builder()
                .id(1L)
                .voteChoice(VoteChoice.FOR)
                .createdAt(Instant.now())
                .build();

        List<Vote> votes = Collections.singletonList(vote);
        Page<Vote> votePage = new PageImpl<>(votes, pageable, votes.size());

        when(userRepository.existsById(userId)).thenReturn(true);
        when(voteRepository.findVotesByUserUuidID(userId, pageable)).thenReturn(votePage);

        // When
        PageResponse<VoteResponse> responses = pollService.getVotesFromUser(userId, pageNo, pageSize);

        // Then
        assertEquals(1, responses.content().size());
        assertEquals(VoteChoice.FOR, responses.content().get(0).voteChoice());

        verify(userRepository, times(1)).existsById(userId);
        verify(voteRepository, times(1)).findVotesByUserUuidID(userId, pageable);
    }

    @Test
    void testGetVotesFromUser_UserNotFound() {
        // Given
        int pageNo = 0;
        int pageSize = 10;

        when(userRepository.existsById(userId)).thenReturn(false);

        // When & Then
        assertThrows(UserNotFoundException.class, () -> pollService.getVotesFromUser(userId, pageNo, pageSize));

        verify(userRepository, times(1)).existsById(userId);
        verifyNoInteractions(voteRepository);
    }

    @Test
    void testDeleteVote_Success() throws VoteNotFoundException {
        // Given
        Long voteId = 1L;
        Vote vote = Vote.builder()
                .id(voteId)
                .poll(poll)
                .build();

        when(voteRepository.findById(voteId)).thenReturn(Optional.of(vote));
        doNothing().when(voteRepository).deleteById(voteId);
        when(pollRepository.save(any(Poll.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String result = pollService.deleteVote(voteId);

        // Then
        assertEquals("Vote: " + voteId + " deleted", result);
        verify(voteRepository, times(1)).findById(voteId);
        verify(voteRepository, times(1)).deleteById(voteId);
        verify(pollRepository, times(1)).save(any(Poll.class));
    }

    @Test
    void testDeleteVote_NotFound() {
        // Given
        Long voteId = 1L;
        when(voteRepository.findById(voteId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(VoteNotFoundException.class, () -> pollService.deleteVote(voteId));
        verify(voteRepository, times(1)).findById(voteId);
        verify(voteRepository, never()).deleteById(anyLong());
        verifyNoMoreInteractions(pollRepository);
    }

    @Test
    void testDownloadPoll_Success() throws PollNotFoundException {
        // Given
        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));

        // When
        PollResponse response = pollService.downloadPoll(pollId);

        // Then
        assertEquals("Test Poll", response.pollName());
        assertArrayEquals("Test Data".getBytes(), response.uploadData());
        verify(pollRepository, times(1)).findById(pollId);
    }

    @Test
    void testDownloadPoll_NotFound() {
        // Given
        when(pollRepository.findById(pollId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(PollNotFoundException.class, () -> pollService.downloadPoll(pollId));
        verify(pollRepository, times(1)).findById(pollId);
    }
}

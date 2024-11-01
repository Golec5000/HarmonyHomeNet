package bwp.hhn.backend.harmonyhomenetlogic.serviceTests;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.PaymentComponentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.PaymentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Apartment;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Payment;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.PaymentComponent;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.ApartmentsRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.PaymentComponentRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.PaymentRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.implementation.PaymentServiceImp;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.PaymentStatus;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PaymentComponentRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PaymentRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.PaymentComponentResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.PaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ApartmentsRepository apartmentsRepository;

    @Mock
    private PaymentComponentRepository paymentComponentRepository;

    @InjectMocks
    private PaymentServiceImp paymentService;

    private UUID paymentId;
    private UUID apartmentId;
    private Payment payment;
    private Apartment apartment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        paymentId = UUID.randomUUID();
        apartmentId = UUID.randomUUID();

        apartment = Apartment.builder()
                .uuidID(apartmentId)
                .apartmentSignature("A1")
                .payments(new ArrayList<>())
                .build();

        payment = Payment.builder()
                .uuidID(paymentId)
                .paymentDate(LocalDateTime.now())
                .paymentStatus(PaymentStatus.UNPAID)
                .paymentAmount(BigDecimal.ZERO)
                .paymentComponents(new ArrayList<>())
                .apartment(apartment)
                .build();
    }

    @Test
    void testCreatePayment_Success() throws ApartmentNotFoundException {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setApartmentSignature("A1");
        paymentRequest.setPaymentDate(LocalDateTime.now());

        when(apartmentsRepository.findByApartmentSignature("A1")).thenReturn(Optional.of(apartment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(apartmentsRepository.save(any(Apartment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.createPayment(paymentRequest);

        assertNotNull(response);
        assertEquals(PaymentStatus.UNPAID, response.paymentStatus());
        assertEquals(apartment.getUuidID(), payment.getApartment().getUuidID());

        verify(apartmentsRepository, times(1)).findByApartmentSignature("A1");
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(apartmentsRepository, times(1)).save(any(Apartment.class));
    }

    @Test
    void testCreatePayment_ApartmentNotFound() {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setApartmentSignature("A1");
        paymentRequest.setPaymentDate(LocalDateTime.now());

        when(apartmentsRepository.findByApartmentSignature("A1")).thenReturn(Optional.empty());

        assertThrows(ApartmentNotFoundException.class, () -> paymentService.createPayment(paymentRequest));

        verify(apartmentsRepository, times(1)).findByApartmentSignature("A1");
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    void testGetPaymentById_Success() throws PaymentNotFoundException {
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getPaymentById(paymentId);

        assertNotNull(response);
        assertEquals(paymentId, response.paymentId());

        verify(paymentRepository, times(1)).findById(paymentId);
    }

    @Test
    void testGetPaymentById_NotFound() {
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> paymentService.getPaymentById(paymentId));

        verify(paymentRepository, times(1)).findById(paymentId);
    }

    @Test
    void testDeletePaymentById_Success() throws PaymentNotFoundException {
        when(paymentRepository.existsById(paymentId)).thenReturn(true);
        doNothing().when(paymentRepository).deleteById(paymentId);

        String result = paymentService.deletePaymentById(paymentId);

        assertEquals("Payment: " + paymentId + " deleted", result);

        verify(paymentRepository, times(1)).existsById(paymentId);
        verify(paymentRepository, times(1)).deleteById(paymentId);
    }

    @Test
    void testDeletePaymentById_NotFound() {
        when(paymentRepository.existsById(paymentId)).thenReturn(false);

        assertThrows(PaymentNotFoundException.class, () -> paymentService.deletePaymentById(paymentId));

        verify(paymentRepository, times(1)).existsById(paymentId);
        verify(paymentRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void testGetAllPayments() {
        when(paymentRepository.findAll()).thenReturn(Collections.singletonList(payment));

        List<PaymentResponse> responses = paymentService.getAllPayments();

        assertEquals(1, responses.size());
        assertEquals(paymentId, responses.get(0).paymentId());

        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    void testGetPaymentsByApartmentSignature_Success() throws ApartmentNotFoundException {
        when(apartmentsRepository.findByApartmentSignature("A1")).thenReturn(Optional.of(apartment));
        when(paymentRepository.findAllByApartmentUuidID(apartmentId)).thenReturn(Collections.singletonList(payment));

        List<PaymentResponse> responses = paymentService.getPaymentsByApartmentSignature("A1");

        assertEquals(1, responses.size());
        assertEquals(paymentId, responses.get(0).paymentId());

        verify(apartmentsRepository, times(1)).findByApartmentSignature("A1");
        verify(paymentRepository, times(1)).findAllByApartmentUuidID(apartmentId);
    }

    @Test
    void testGetPaymentsByApartmentSignature_NotFound() {
        when(apartmentsRepository.findByApartmentSignature("A1")).thenReturn(Optional.empty());

        assertThrows(ApartmentNotFoundException.class, () -> paymentService.getPaymentsByApartmentSignature("A1"));

        verify(apartmentsRepository, times(1)).findByApartmentSignature("A1");
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    void testPayPayment_Success() throws PaymentNotFoundException {
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.payPayment(paymentId);

        assertEquals(PaymentStatus.PAID, response.paymentStatus());
        assertNotNull(response.paymentTime());

        verify(paymentRepository, times(1)).findById(paymentId);
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void testPayPayment_AlreadyPaid() {
        payment.setPaymentStatus(PaymentStatus.PAID);
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        assertThrows(IllegalArgumentException.class, () -> paymentService.payPayment(paymentId));

        verify(paymentRepository, times(1)).findById(paymentId);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void testPayPayment_NotFound() {
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> paymentService.payPayment(paymentId));

        verify(paymentRepository, times(1)).findById(paymentId);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void testAddPaymentComponent_Success() throws PaymentNotFoundException {
        PaymentComponentRequest componentRequest = new PaymentComponentRequest();
        componentRequest.setComponentType("Water");
        componentRequest.setComponentAmount(new BigDecimal("10"));
        componentRequest.setUnitPrice(new BigDecimal("2"));
        componentRequest.setSpecialMultiplier(new BigDecimal("1.5"));
        componentRequest.setUnit("m3");

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentComponentRepository.save(any(PaymentComponent.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.addPaymentComponent(paymentId, componentRequest);

        assertNotNull(response);
        assertEquals(1, payment.getPaymentComponents().size());
        assertEquals(new BigDecimal("30.0"), response.paymentAmount());

        verify(paymentRepository, times(1)).findById(paymentId);
        verify(paymentComponentRepository, times(1)).save(any(PaymentComponent.class));
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void testAddPaymentComponent_PaymentNotFound() {
        PaymentComponentRequest componentRequest = new PaymentComponentRequest();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> paymentService.addPaymentComponent(paymentId, componentRequest));

        verify(paymentRepository, times(1)).findById(paymentId);
        verifyNoMoreInteractions(paymentComponentRepository);
    }

    @Test
    void testRemovePaymentComponent_Success() throws PaymentNotFoundException, PaymentComponentNotFoundException {
        PaymentComponent component = PaymentComponent.builder()
                .id(1L)
                .componentType("Water")
                .componentAmount(new BigDecimal("10"))
                .unitPrice(new BigDecimal("2"))
                .specialMultiplier(new BigDecimal("1"))
                .unit("m3")
                .build();

        payment.getPaymentComponents().add(component);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentComponentRepository.findById(1L)).thenReturn(Optional.of(component));
        doNothing().when(paymentComponentRepository).deleteById(1L);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.removePaymentComponent(paymentId, 1L);

        assertNotNull(response);
        assertEquals(0, payment.getPaymentComponents().size());
        assertEquals(BigDecimal.ZERO, response.paymentAmount());

        verify(paymentRepository, times(1)).findById(paymentId);
        verify(paymentComponentRepository, times(1)).findById(1L);
        verify(paymentComponentRepository, times(1)).deleteById(1L);
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void testRemovePaymentComponent_PaymentNotFound() {
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> paymentService.removePaymentComponent(paymentId, 1L));

        verify(paymentRepository, times(1)).findById(paymentId);
        verifyNoMoreInteractions(paymentComponentRepository);
    }

    @Test
    void testRemovePaymentComponent_ComponentNotFound() {
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentComponentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PaymentComponentNotFoundException.class, () -> paymentService.removePaymentComponent(paymentId, 1L));

        verify(paymentRepository, times(1)).findById(paymentId);
        verify(paymentComponentRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdatePaymentComponent_Success() throws PaymentNotFoundException, PaymentComponentNotFoundException {
        PaymentComponent component = PaymentComponent.builder()
                .id(1L)
                .componentType("Water")
                .componentAmount(new BigDecimal("10"))
                .unitPrice(new BigDecimal("2"))
                .specialMultiplier(new BigDecimal("1"))
                .unit("m3")
                .build();

        payment.getPaymentComponents().add(component);

        PaymentComponentRequest componentRequest = new PaymentComponentRequest();
        componentRequest.setComponentAmount(new BigDecimal("20"));
        componentRequest.setUnitPrice(new BigDecimal("2.5"));

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentComponentRepository.findById(1L)).thenReturn(Optional.of(component));
        when(paymentComponentRepository.save(any(PaymentComponent.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.updatePaymentComponent(paymentId, 1L, componentRequest);

        assertNotNull(response);
        assertEquals(1, payment.getPaymentComponents().size());
        assertEquals(new BigDecimal("50.0"), response.paymentAmount());

        verify(paymentRepository, times(1)).findById(paymentId);
        verify(paymentComponentRepository, times(1)).findById(1L);
        verify(paymentComponentRepository, times(1)).save(component);
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void testUpdatePaymentComponent_PaymentNotFound() {
        PaymentComponentRequest componentRequest = new PaymentComponentRequest();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> paymentService.updatePaymentComponent(paymentId, 1L, componentRequest));

        verify(paymentRepository, times(1)).findById(paymentId);
        verifyNoMoreInteractions(paymentComponentRepository);
    }

    @Test
    void testUpdatePaymentComponent_ComponentNotFound() {
        PaymentComponentRequest componentRequest = new PaymentComponentRequest();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentComponentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PaymentComponentNotFoundException.class, () -> paymentService.updatePaymentComponent(paymentId, 1L, componentRequest));

        verify(paymentRepository, times(1)).findById(paymentId);
        verify(paymentComponentRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPaymentComponents_Success() throws PaymentNotFoundException {
        PaymentComponent component = PaymentComponent.builder()
                .id(1L)
                .componentType("Electricity")
                .componentAmount(new BigDecimal("100"))
                .unitPrice(new BigDecimal("0.5"))
                .specialMultiplier(new BigDecimal("1"))
                .unit("kWh")
                .build();

        payment.getPaymentComponents().add(component);

        when(paymentComponentRepository.findAllByPaymentUuidID(paymentId)).thenReturn(Collections.singletonList(component));

        List<PaymentComponentResponse> responses = paymentService.getPaymentComponents(paymentId);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Electricity", responses.get(0).componentType());

        verify(paymentComponentRepository, times(1)).findAllByPaymentUuidID(paymentId);
    }

    @Test
    void testGetPaymentComponents_PaymentNotFound() {
        when(paymentComponentRepository.findAllByPaymentUuidID(paymentId)).thenReturn(Collections.emptyList());

        List<PaymentComponentResponse> responses = paymentService.getPaymentComponents(paymentId);

        assertNotNull(responses);
        assertEquals(0, responses.size());

        verify(paymentComponentRepository, times(1)).findAllByPaymentUuidID(paymentId);
    }
}

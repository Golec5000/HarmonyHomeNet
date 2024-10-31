package bwp.hhn.backend.harmonyhomenetlogic.service.implementation;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.PaymentComponentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.PaymentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Apartment;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.Payment;
import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.PaymentComponent;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.ApartmentsRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.PaymentComponentRepository;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.PaymentRepository;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.PaymentService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.PaymentStatus;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PaymentComponentRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PaymentRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.PaymentComponentResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.PaymentResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class PaymentServiceImp implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ApartmentsRepository apartmentsRepository;
    private final PaymentComponentRepository paymentComponentRepository;

    private static final Logger LOGGER = Logger.getLogger(PaymentServiceImp.class.getName());

    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentRequest paymentRequest) throws ApartmentNotFoundException {

        Apartment apartment = apartmentsRepository.findByApartmentSignature(paymentRequest.getApartmentSignature())
                .orElseThrow(() -> new ApartmentNotFoundException("Apartment: " + paymentRequest.getApartmentSignature() + " not found"));

        Payment payment = Payment.builder()
                .paymentDate(paymentRequest.getPaymentDate())
                .paymentComponents(new ArrayList<>())
                .paymentStatus(PaymentStatus.UNPAID)
                .apartment(apartment)
                .paymentAmount(BigDecimal.ZERO)
                .build();

        if (apartment.getPayments() == null) apartment.setPayments(new ArrayList<>());
        apartment.getPayments().add(payment);

        Payment saved = paymentRepository.save(payment);
        apartmentsRepository.save(apartment);

        return PaymentResponse.builder()
                .paymentId(saved.getUuidID())
                .paymentStatus(saved.getPaymentStatus())
                .paymentDate(saved.getPaymentDate())
                .paymentTime(saved.getPaymentTime())
                .paymentAmount(saved.getPaymentAmount())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    public PaymentResponse getPaymentById(UUID paymentId) throws PaymentNotFoundException {
        return paymentRepository.findById(paymentId)
                .map(payment -> PaymentResponse.builder()
                        .paymentId(payment.getUuidID())
                        .paymentStatus(payment.getPaymentStatus())
                        .paymentDate(payment.getPaymentDate())
                        .paymentTime(payment.getPaymentTime())
                        .paymentAmount(payment.getPaymentAmount())
                        .createdAt(payment.getCreatedAt())
                        .build())
                .orElseThrow(() -> new PaymentNotFoundException("Payment: " + paymentId + " not found"));
    }

    @Override
    public String deletePaymentById(UUID paymentId) throws PaymentNotFoundException {

        if (!paymentRepository.existsById(paymentId)) {
            throw new PaymentNotFoundException("Payment: " + paymentId + " not found");
        }

        paymentRepository.deleteById(paymentId);
        return "Payment: " + paymentId + " deleted";
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(payment -> PaymentResponse.builder()
                        .paymentId(payment.getUuidID())
                        .paymentStatus(payment.getPaymentStatus())
                        .paymentDate(payment.getPaymentDate())
                        .paymentTime(payment.getPaymentTime())
                        .paymentAmount(payment.getPaymentAmount())
                        .createdAt(payment.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    public List<PaymentResponse> getPaymentsByApartmentSignature(String apartmentSignature) throws ApartmentNotFoundException {

        Apartment apartment = apartmentsRepository.findByApartmentSignature(apartmentSignature)
                .orElseThrow(() -> new ApartmentNotFoundException("Apartment: " + apartmentSignature + " not found"));

        return paymentRepository.findAllByApartmentUuidID(apartment.getUuidID()).stream()
                .map(payment -> PaymentResponse.builder()
                        .paymentId(payment.getUuidID())
                        .paymentStatus(payment.getPaymentStatus())
                        .paymentDate(payment.getPaymentDate())
                        .paymentTime(payment.getPaymentTime())
                        .paymentAmount(payment.getPaymentAmount())
                        .createdAt(payment.getCreatedAt())
                        .build())
                .toList();

    }

    @Override
    public PaymentResponse payPayment(UUID paymentId) throws PaymentNotFoundException, IllegalArgumentException {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment: " + paymentId + " not found"));

        if (payment.getPaymentStatus() == PaymentStatus.PAID)
            throw new IllegalArgumentException("Payment: " + paymentId + " already paid");

        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaymentTime(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        LOGGER.info("Payment: " + paymentId + " paid with amount: " + saved.getPaymentAmount());

        return PaymentResponse.builder()
                .paymentId(saved.getUuidID())
                .paymentStatus(saved.getPaymentStatus())
                .paymentDate(saved.getPaymentDate())
                .paymentTime(saved.getPaymentTime())
                .paymentAmount(saved.getPaymentAmount())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public PaymentResponse addPaymentComponent(UUID paymentId, PaymentComponentRequest paymentComponentRequest) throws PaymentNotFoundException {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment: " + paymentId + " not found"));

        // Walidacja specialMultiplier
        BigDecimal specialMultiplier = paymentComponentRequest.getSpecialMultiplier();
        if (specialMultiplier == null || specialMultiplier.compareTo(BigDecimal.ONE) < 0) {
            specialMultiplier = BigDecimal.ONE;
        }

        PaymentComponent paymentComponent = PaymentComponent.builder()
                .payment(payment)
                .componentType(paymentComponentRequest.getComponentType())
                .componentAmount(paymentComponentRequest.getComponentAmount())
                .unitPrice(paymentComponentRequest.getUnitPrice())
                .specialMultiplier(specialMultiplier)
                .unit(paymentComponentRequest.getUnit())
                .build();

        if (payment.getPaymentComponents() == null) payment.setPaymentComponents(new ArrayList<>());
        payment.getPaymentComponents().add(paymentComponent);

        paymentComponentRepository.save(paymentComponent);

        recalculatePaymentAmount(payment);
        Payment saved = paymentRepository.save(payment);

        return PaymentResponse.builder()
                .paymentId(saved.getUuidID())
                .paymentStatus(saved.getPaymentStatus())
                .paymentDate(saved.getPaymentDate())
                .paymentTime(saved.getPaymentTime())
                .paymentAmount(saved.getPaymentAmount())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public PaymentResponse removePaymentComponent(UUID paymentId, Long paymentComponentId) throws PaymentNotFoundException, PaymentComponentNotFoundException {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment: " + paymentId + " not found"));

        PaymentComponent paymentComponent = paymentComponentRepository.findById(paymentComponentId)
                .orElseThrow(() -> new PaymentComponentNotFoundException("PaymentComponent: " + paymentComponentId + " not found"));

        payment.getPaymentComponents().remove(paymentComponent);

        paymentComponentRepository.deleteById(paymentComponentId);

        recalculatePaymentAmount(payment);
        Payment saved = paymentRepository.save(payment);

        return PaymentResponse.builder()
                .paymentId(saved.getUuidID())
                .paymentStatus(saved.getPaymentStatus())
                .paymentDate(saved.getPaymentDate())
                .paymentTime(saved.getPaymentTime())
                .paymentAmount(saved.getPaymentAmount())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public PaymentResponse updatePaymentComponent(UUID paymentId, Long paymentComponentId, PaymentComponentRequest paymentComponentRequest) throws PaymentNotFoundException, PaymentComponentNotFoundException {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment: " + paymentId + " not found"));

        PaymentComponent paymentComponent = paymentComponentRepository.findById(paymentComponentId)
                .orElseThrow(() -> new PaymentComponentNotFoundException("PaymentComponent: " + paymentComponentId + " not found"));

        paymentComponent.setComponentType(paymentComponentRequest.getComponentType() != null ? paymentComponentRequest.getComponentType() : paymentComponent.getComponentType());
        paymentComponent.setComponentAmount(paymentComponentRequest.getComponentAmount() != null ? paymentComponentRequest.getComponentAmount() : paymentComponent.getComponentAmount());
        paymentComponent.setUnitPrice(paymentComponentRequest.getUnitPrice() != null ? paymentComponentRequest.getUnitPrice() : paymentComponent.getUnitPrice());
        paymentComponent.setUnit(paymentComponentRequest.getUnit() != null ? paymentComponentRequest.getUnit() : paymentComponent.getUnit());


        BigDecimal specialMultiplier = paymentComponentRequest.getSpecialMultiplier();
        if (specialMultiplier == null || specialMultiplier.compareTo(BigDecimal.ONE) < 0) {
            specialMultiplier = BigDecimal.ONE;
        }
        paymentComponent.setSpecialMultiplier(specialMultiplier);

        paymentComponentRepository.save(paymentComponent);

        recalculatePaymentAmount(payment);
        Payment saved = paymentRepository.save(payment);

        return PaymentResponse.builder()
                .paymentId(saved.getUuidID())
                .paymentStatus(saved.getPaymentStatus())
                .paymentDate(saved.getPaymentDate())
                .paymentTime(saved.getPaymentTime())
                .paymentAmount(saved.getPaymentAmount())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    public List<PaymentComponentResponse> getPaymentComponents(UUID paymentId) throws PaymentNotFoundException {
        return paymentComponentRepository.findAllByPaymentUuidID(paymentId)
                .stream()
                .map(paymentComponent -> PaymentComponentResponse.builder()
                        .componentType(paymentComponent.getComponentType())
                        .componentAmount(paymentComponent.getComponentAmount())
                        .unitPrice(paymentComponent.getUnitPrice())
                        .specialMultiplier(paymentComponent.getSpecialMultiplier())
                        .updatedAt(paymentComponent.getUpdatedAt())
                        .createdAt(paymentComponent.getCreatedAt())
                        .unit(paymentComponent.getUnit())
                        .build())
                .toList();
    }

    private void recalculatePaymentAmount(Payment payment) {
        BigDecimal totalAmount = payment.getPaymentComponents().stream()
                .map(component -> {
                    BigDecimal componentMultiplier = component.getSpecialMultiplier() != null ? component.getSpecialMultiplier() : BigDecimal.ONE;
                    return component.getComponentAmount()
                            .multiply(component.getUnitPrice())
                            .multiply(componentMultiplier);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        payment.setPaymentAmount(totalAmount);
    }


}

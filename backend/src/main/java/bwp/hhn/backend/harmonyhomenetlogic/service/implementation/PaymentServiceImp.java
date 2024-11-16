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
import bwp.hhn.backend.harmonyhomenetlogic.service.adapters.BankingService;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.PaymentService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.enums.PaymentStatus;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PaymentComponentRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PaymentRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.PaymentComponentResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.PaymentResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImp implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ApartmentsRepository apartmentsRepository;
    private final PaymentComponentRepository paymentComponentRepository;
    private final BankingService bankingService;

    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentRequest paymentRequest) throws ApartmentNotFoundException {

        Apartment apartment = apartmentsRepository.findByApartmentSignature(paymentRequest.getApartmentSignature())
                .orElseThrow(() -> new ApartmentNotFoundException("Apartment: " + paymentRequest.getApartmentSignature() + " not found"));

        Payment payment = Payment.builder()
                .paymentComponents(new ArrayList<>())
                .paymentStatus(PaymentStatus.UNPAID)
                .apartment(apartment)
                .description(paymentRequest.getDescription())
                .paymentAmount(BigDecimal.ZERO)
                .readyToPay(Boolean.FALSE)
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
                .description(saved.getDescription())
                .readyToPay(saved.getReadyToPay())
                .apartmentSignature(saved.getApartment().getApartmentSignature())
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
                        .description(payment.getDescription())
                        .readyToPay(payment.getReadyToPay())
                        .apartmentSignature(payment.getApartment().getApartmentSignature())
                        .build()
                )
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
    public PageResponse<PaymentResponse> getAllPayments(int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Payment> payments = paymentRepository.findAll(pageable);

        return getPaymentResponsePageResponse(payments);
    }

    @Override
    public PageResponse<PaymentResponse> getPaymentsByApartmentSignature(String apartmentSignature, int pageNo, int pageSize) throws ApartmentNotFoundException {

        Apartment apartment = apartmentsRepository.findByApartmentSignature(apartmentSignature)
                .orElseThrow(() -> new ApartmentNotFoundException("Apartment: " + apartmentSignature + " not found"));

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Payment> payments = paymentRepository.findAllByApartmentUuidID(apartment.getUuidID(), pageable);


        return getPaymentResponsePageResponse(payments);

    }


    @Override
    public PaymentResponse payPayment(UUID paymentId) throws PaymentNotFoundException, IllegalArgumentException {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment: " + paymentId + " not found"));

        if (payment.getPaymentStatus() == PaymentStatus.PAID)
            throw new IllegalArgumentException("Payment: " + paymentId + " already paid");

        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaymentTime(Instant.now());

        Payment saved = paymentRepository.save(payment);

        bankingService.pay(payment.getPaymentAmount());

        return PaymentResponse.builder()
                .paymentId(saved.getUuidID())
                .paymentStatus(saved.getPaymentStatus())
                .paymentDate(saved.getPaymentDate())
                .paymentTime(saved.getPaymentTime())
                .paymentAmount(saved.getPaymentAmount())
                .createdAt(saved.getCreatedAt())
                .description(saved.getDescription())
                .readyToPay(saved.getReadyToPay())
                .apartmentSignature(saved.getApartment().getApartmentSignature())
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
                .readyToPay(saved.getReadyToPay())
                .description(saved.getDescription())
                .apartmentSignature(saved.getApartment().getApartmentSignature())
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
                .description(saved.getDescription())
                .readyToPay(saved.getReadyToPay())
                .apartmentSignature(saved.getApartment().getApartmentSignature())
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
                .readyToPay(saved.getReadyToPay())
                .description(saved.getDescription())
                .apartmentSignature(saved.getApartment().getApartmentSignature())
                .build();
    }

    @Override
    public List<PaymentComponentResponse> getPaymentComponents(UUID paymentId) throws PaymentNotFoundException {

        return paymentComponentRepository.findAllByPaymentUuidID(paymentId).stream()
                .map(
                        paymentComponent -> PaymentComponentResponse.builder()
                                .id(paymentComponent.getId())
                                .componentType(paymentComponent.getComponentType())
                                .componentAmount(paymentComponent.getComponentAmount())
                                .unitPrice(paymentComponent.getUnitPrice())
                                .specialMultiplier(paymentComponent.getSpecialMultiplier())
                                .updatedAt(paymentComponent.getUpdatedAt())
                                .createdAt(paymentComponent.getCreatedAt())
                                .unit(paymentComponent.getUnit())
                                .build()
                )
                .toList();
    }

    @Override
    public String activatePayment(UUID paymentId, Boolean setActive) throws PaymentNotFoundException {

            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new PaymentNotFoundException("Payment: " + paymentId + " not found"));

            payment.setReadyToPay(setActive);
            paymentRepository.save(payment);

            return "Payment: " + paymentId + " activated";
    }

    @Override
    @Transactional
    public PaymentResponse updatePayment(UUID paymentId, PaymentRequest paymentRequest) throws PaymentNotFoundException, ApartmentNotFoundException{

            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new PaymentNotFoundException("Payment: " + paymentId + " not found"));

            Apartment apartment = apartmentsRepository.findByApartmentSignature(paymentRequest.getApartmentSignature())
                    .orElseThrow(() -> new ApartmentNotFoundException("Apartment: " + paymentRequest.getApartmentSignature() + " not found"));

            payment.setApartment(apartment);
            payment.setDescription(paymentRequest.getDescription() != null ? paymentRequest.getDescription() : payment.getDescription());

            Payment saved = paymentRepository.save(payment);

            return PaymentResponse.builder()
                    .paymentId(saved.getUuidID())
                    .paymentStatus(saved.getPaymentStatus())
                    .paymentDate(saved.getPaymentDate())
                    .paymentTime(saved.getPaymentTime())
                    .paymentAmount(saved.getPaymentAmount())
                    .createdAt(saved.getCreatedAt())
                    .description(saved.getDescription())
                    .readyToPay(saved.getReadyToPay())
                    .apartmentSignature(saved.getApartment().getApartmentSignature())
                    .build();
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

    private PageResponse<PaymentResponse> getPaymentResponsePageResponse(Page<Payment> payments) {
        return new PageResponse<>(
                payments.getNumber(),
                payments.getSize(),
                payments.getTotalPages(),
                payments.getContent().stream()
                        .map(payment -> PaymentResponse.builder()
                                .paymentId(payment.getUuidID())
                                .paymentStatus(payment.getPaymentStatus())
                                .paymentDate(payment.getPaymentDate())
                                .paymentTime(payment.getPaymentTime())
                                .paymentAmount(payment.getPaymentAmount())
                                .createdAt(payment.getCreatedAt())
                                .description(payment.getDescription())
                                .readyToPay(payment.getReadyToPay())
                                .apartmentSignature(payment.getApartment().getApartmentSignature())
                                .build())
                        .toList(),
                payments.isLast(),
                payments.hasNext(),
                payments.hasPrevious()
        );
    }
}

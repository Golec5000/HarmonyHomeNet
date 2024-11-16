package bwp.hhn.backend.harmonyhomenetlogic.service.interfaces;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.PaymentComponentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.PaymentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PaymentComponentRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PaymentRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.PaymentComponentResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.PaymentResponse;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    PaymentResponse createPayment(PaymentRequest paymentRequest) throws ApartmentNotFoundException;

    PaymentResponse getPaymentById(UUID paymentId) throws PaymentNotFoundException;

    String deletePaymentById(UUID paymentId) throws PaymentNotFoundException;

    PageResponse<PaymentResponse> getAllPayments(int pageNo, int pageSize);

    PageResponse<PaymentResponse> getPaymentsByApartmentSignature(String apartmentSignature, int pageNo, int pageSize) throws ApartmentNotFoundException;

    PaymentResponse payPayment(UUID paymentId) throws PaymentNotFoundException, IllegalArgumentException;

    PaymentResponse addPaymentComponent(UUID paymentId, PaymentComponentRequest paymentComponentRequest) throws PaymentNotFoundException;

    PaymentResponse removePaymentComponent(UUID paymentId, Long paymentComponentId) throws PaymentNotFoundException;

    PaymentResponse updatePaymentComponent(UUID paymentId, Long paymentComponentId, PaymentComponentRequest paymentComponentRequest) throws PaymentNotFoundException, PaymentComponentNotFoundException;

    List<PaymentComponentResponse> getPaymentComponents(UUID paymentId) throws PaymentNotFoundException;

    String activatePayment(UUID paymentId, Boolean setActive) throws PaymentNotFoundException;

    PaymentResponse updatePayment(UUID paymentId, PaymentRequest paymentRequest) throws PaymentNotFoundException, ApartmentNotFoundException;

}

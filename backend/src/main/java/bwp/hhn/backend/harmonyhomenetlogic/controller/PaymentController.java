package bwp.hhn.backend.harmonyhomenetlogic.controller;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.PaymentComponentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.PaymentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.PaymentService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PaymentComponentRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PaymentRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.PaymentComponentResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bwp/hhn/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-payment")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest paymentRequest) throws ApartmentNotFoundException {
        return ResponseEntity.ok(paymentService.createPayment(paymentRequest));
    }

    @GetMapping("/get-payment/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable UUID paymentId) throws PaymentNotFoundException {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }

    @DeleteMapping("/delete-payment/{paymentId}")
    public ResponseEntity<String> deletePaymentById(@PathVariable UUID paymentId) throws PaymentNotFoundException {
        return ResponseEntity.ok(paymentService.deletePaymentById(paymentId));
    }

    @GetMapping("/get-all-payments")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/get-payment-by-apartment/{apartmentSignature}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByApartmentId(@PathVariable String apartmentSignature) throws ApartmentNotFoundException {
        return ResponseEntity.ok(paymentService.getPaymentsByApartmentSignature(apartmentSignature));
    }

    @PostMapping("/pay/{paymentId}")
    public ResponseEntity<PaymentResponse> payPayment(@PathVariable UUID paymentId) throws PaymentNotFoundException, IllegalArgumentException {
        return ResponseEntity.ok(paymentService.payPayment(paymentId));
    }

    @PutMapping("/change-payment-status/{paymentId}")
    public ResponseEntity<PaymentResponse> changePaymentStatus(@PathVariable UUID paymentId, @RequestBody PaymentRequest paymentRequest) throws PaymentNotFoundException {
        return ResponseEntity.ok(paymentService.changePaymentStatus(paymentId, paymentRequest));
    }

    @PostMapping("/add-component-to-payment/{paymentId}")
    public ResponseEntity<PaymentResponse> addPaymentComponent(@PathVariable UUID paymentId, @RequestBody PaymentComponentRequest paymentComponentRequest) throws PaymentNotFoundException {
        return ResponseEntity.ok(paymentService.addPaymentComponent(paymentId, paymentComponentRequest));
    }

    @DeleteMapping("/remove-component-from-payment/{paymentId}/{paymentComponentId}")
    public ResponseEntity<PaymentResponse> removePaymentComponent(@PathVariable UUID paymentId, @PathVariable Long paymentComponentId) throws PaymentNotFoundException {
        return ResponseEntity.ok(paymentService.removePaymentComponent(paymentId, paymentComponentId));
    }

    @PutMapping("/update-payment-component/{paymentId}/{paymentComponentId}")
    public ResponseEntity<PaymentResponse> updatePaymentComponent(@PathVariable UUID paymentId, @PathVariable Long paymentComponentId, @RequestBody PaymentComponentRequest paymentComponentRequest) throws PaymentNotFoundException, PaymentComponentNotFoundException {
        return ResponseEntity.ok(paymentService.updatePaymentComponent(paymentId, paymentComponentId, paymentComponentRequest));
    }

    @GetMapping("/get-payment-components/{paymentId}")
    public ResponseEntity<List<PaymentComponentResponse>> getPaymentComponents(@PathVariable UUID paymentId) throws PaymentNotFoundException {
        return ResponseEntity.ok(paymentService.getPaymentComponents(paymentId));
    }
}
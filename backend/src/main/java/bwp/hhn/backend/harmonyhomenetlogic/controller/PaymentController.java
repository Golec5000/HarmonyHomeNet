package bwp.hhn.backend.harmonyhomenetlogic.controller;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.ApartmentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.PaymentComponentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.PaymentNotFoundException;
import bwp.hhn.backend.harmonyhomenetlogic.service.interfaces.PaymentService;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PaymentComponentRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.request.PaymentRequest;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.page.PageResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.PaymentComponentResponse;
import bwp.hhn.backend.harmonyhomenetlogic.utils.response.typesOfPage.PaymentResponse;
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

    //GET
    @GetMapping("/get-payment")
    public ResponseEntity<PaymentResponse> getPaymentById(@RequestParam UUID paymentId) throws PaymentNotFoundException {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }

    @GetMapping("/get-all-payments")
    public ResponseEntity<PageResponse<PaymentResponse>> getAllPayments(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        return ResponseEntity.ok(paymentService.getAllPayments(pageNo, pageSize));
    }

    @GetMapping("/get-payment-by-apartment")
    public ResponseEntity<PageResponse<PaymentResponse>> getPaymentsByApartmentId(
            @RequestParam String apartmentSignature,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) throws ApartmentNotFoundException {
        return ResponseEntity.ok(paymentService.getPaymentsByApartmentSignature(apartmentSignature, pageNo, pageSize));
    }

    @GetMapping("/get-payment-components")
    public ResponseEntity<List<PaymentComponentResponse>> getPaymentComponents(@RequestParam UUID paymentId) throws PaymentNotFoundException {
        return ResponseEntity.ok(paymentService.getPaymentComponents(paymentId));
    }

    //POST
    @PostMapping("/pay")
    public ResponseEntity<PaymentResponse> payPayment(@RequestParam UUID paymentId) throws PaymentNotFoundException, IllegalArgumentException {
        return ResponseEntity.ok(paymentService.payPayment(paymentId));
    }

    @PostMapping("/add-component-to-payment")
    public ResponseEntity<PaymentResponse> addPaymentComponent(@RequestParam UUID paymentId, @RequestBody PaymentComponentRequest paymentComponentRequest) throws PaymentNotFoundException {
        return ResponseEntity.ok(paymentService.addPaymentComponent(paymentId, paymentComponentRequest));
    }

    @PostMapping("/create-payment")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest paymentRequest) throws ApartmentNotFoundException {
        return ResponseEntity.ok(paymentService.createPayment(paymentRequest));
    }

    //PUT
    @PutMapping("/remove-component-from-payment/{paymentComponentId}")
    public ResponseEntity<PaymentResponse> removePaymentComponent(@RequestParam UUID paymentId, @PathVariable Long paymentComponentId) throws PaymentNotFoundException {
        return ResponseEntity.ok(paymentService.removePaymentComponent(paymentId, paymentComponentId));
    }

    @PutMapping("/activate-payment")
    public ResponseEntity<String> activatePayment(@RequestParam UUID paymentId) throws PaymentNotFoundException {
        return ResponseEntity.ok(paymentService.activatePayment(paymentId));
    }

    @PutMapping("/update-payment-component/{paymentComponentId}")
    public ResponseEntity<PaymentResponse> updatePaymentComponent(@RequestParam UUID paymentId, @PathVariable Long paymentComponentId, @RequestBody PaymentComponentRequest paymentComponentRequest) throws PaymentNotFoundException, PaymentComponentNotFoundException {
        return ResponseEntity.ok(paymentService.updatePaymentComponent(paymentId, paymentComponentId, paymentComponentRequest));
    }

    //DELETE
    @DeleteMapping("/delete-payment")
    public ResponseEntity<String> deletePaymentById(@RequestParam UUID paymentId) throws PaymentNotFoundException {
        return ResponseEntity.ok(paymentService.deletePaymentById(paymentId));
    }

}
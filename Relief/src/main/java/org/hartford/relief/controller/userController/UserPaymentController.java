package org.hartford.relief.controller.userController;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.request.PremiumPaymentRequest;
import org.hartford.relief.dto.response.PaymentResponse;
import org.hartford.relief.service.userService.UserPaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/payments")
@RequiredArgsConstructor
public class UserPaymentController {

    private final UserPaymentService userPaymentService;

    // POST /api/users/{userId}/payments/pay-premium
    @PostMapping("/pay-premium")
    public ResponseEntity<PaymentResponse> payPremium(@PathVariable Long userId,
                                                       @RequestBody PremiumPaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userPaymentService.payPremium(userId, request));
    }

    // GET /api/users/{userId}/payments
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getMyPayments(@PathVariable Long userId) {
        return ResponseEntity.ok(userPaymentService.getMyPayments(userId));
    }
}

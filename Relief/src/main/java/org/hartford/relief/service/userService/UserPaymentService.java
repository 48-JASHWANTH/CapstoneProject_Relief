package org.hartford.relief.service.userService;

import org.hartford.relief.dto.request.PremiumPaymentRequest;
import org.hartford.relief.dto.response.PaymentResponse;

import java.util.List;

public interface UserPaymentService {

    PaymentResponse payPremium(Long userId, PremiumPaymentRequest request);

    List<PaymentResponse> getMyPayments(Long userId);
}

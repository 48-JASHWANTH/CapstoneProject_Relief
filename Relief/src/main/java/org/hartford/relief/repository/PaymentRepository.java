package org.hartford.relief.repository;

import org.hartford.relief.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByPolicyId(Long policyId);

    List<Payment> findByPaymentType(String paymentType);

    List<Payment> findByPolicyIdAndPaymentType(Long policyId, String paymentType);
}

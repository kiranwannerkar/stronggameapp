package com.tyss.strongameapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tyss.strongameapp.entity.PaymentDetails;

public interface PaymentRepository extends JpaRepository<PaymentDetails, Integer> {

	Optional<PaymentDetails> findByPaymentOrderId(String orderId);

}

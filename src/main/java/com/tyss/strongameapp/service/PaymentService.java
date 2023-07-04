package com.tyss.strongameapp.service;

import com.tyss.strongameapp.dto.PaymentRequestDto;
import com.tyss.strongameapp.dto.RazorpayResponse;

/**
 * PaymentService is implemented by PaymentServiceImple class, which is used to
 * manage the payment.
 * 
 * @author Sushma Guttal
 *
 */
public interface PaymentService {

	/**
	 * This method is implemented by its implementation class, which is used to
	 * verify the signature.
	 * 
	 * @param data
	 * @return boolean
	 */
	boolean verifySignature(RazorpayResponse data);

	/**
	 * This method is implemented by its implementation class, which is used to get
	 * the order id.
	 * 
	 * @param paymentDto
	 * @param userId
	 * @return String
	 */
	String getOrderId(PaymentRequestDto paymentDto, int userId);

}// End of PaymentService interface.

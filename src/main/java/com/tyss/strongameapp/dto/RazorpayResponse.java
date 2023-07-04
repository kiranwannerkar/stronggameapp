package com.tyss.strongameapp.dto;

import lombok.Data;

@Data
public class RazorpayResponse {

	private String razorpayPaymentId;

	private String razorpayOrderId;

	private String razorpaySignature;

	private String orderId;

}

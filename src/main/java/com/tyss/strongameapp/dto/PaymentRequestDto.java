package com.tyss.strongameapp.dto;

import lombok.Data;

@Data
public class PaymentRequestDto {

	private double amount;

	private String currency;

	/**
	 * flag is true if payment is for products (so we are checking cart products)
	 */
	private boolean flag;

	/**
	 * user completely making payment through coins(1) or amount(0) flag
	 */
	private int coinOrPriceFlag;

}

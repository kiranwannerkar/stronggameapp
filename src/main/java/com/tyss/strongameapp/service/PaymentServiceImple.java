package com.tyss.strongameapp.service;

import static com.tyss.strongameapp.constants.CartConstants.OUT_OF_STOCK;

import java.security.SignatureException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.tyss.strongameapp.dto.PaymentRequestDto;
import com.tyss.strongameapp.dto.RazorpayResponse;
import com.tyss.strongameapp.entity.CartProduct;
import com.tyss.strongameapp.entity.PaymentDetails;
import com.tyss.strongameapp.entity.ProductAccessoryVariant;
import com.tyss.strongameapp.entity.ProductClothVariant;
import com.tyss.strongameapp.entity.ProductSupplementVariant;
import com.tyss.strongameapp.entity.UserInformation;
import com.tyss.strongameapp.exception.PlaceOrderException;
import com.tyss.strongameapp.exception.ProductException;
import com.tyss.strongameapp.exception.UserNotExistException;
import com.tyss.strongameapp.repository.PaymentRepository;
import com.tyss.strongameapp.repository.UserInformationRepository;

/**
 * PaymentServiceImple is implementation class which is used for payment
 * management.
 * 
 * @author Sushma Guttal
 *
 */

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class PaymentServiceImple implements PaymentService {

	/**
	 * THis field is used to invoke persistence layer methods.
	 */
	@Autowired
	PaymentRepository paymentRepository;

	/**
	 * This field is used for invoking persistence layer methods
	 */
	@Autowired
	private UserInformationRepository userRepo;

//	@Value("${algorithm}")
	private static String hmacSha256Alorithm = "HmacSHA256";

	private String generatedSignature;

//	@Value("${keyid}")
	private static String keyId = "rzp_live_DJTelLI0VIfF5y";

//	@Value("${keysecret}")
	private static String keySecret = "8HUuFSexlFbhmhb11gj4YzCx";

	/**
	 * This method is implemented to generate order id.
	 * 
	 * @param paymentDto
	 * @param userId
	 * @return String
	 */
	@Override
	public String getOrderId(PaymentRequestDto paymentDto, int userId) {
		Order order = null;
		String id = null;
		UserInformation userInformation = userRepo.findById(userId).orElseThrow(UserNotExistException::new);
		List<CartProduct> cartProducts = userInformation.getCartProducts().stream().filter(x -> !x.isOrderd())
				.collect(Collectors.toList());
		if (paymentDto.isFlag()) {
			validateCart(cartProducts);
		}
		if (paymentDto.getCoinOrPriceFlag() == 1) {
			return "0000";
		}
		try {
			RazorpayClient razorpayClient = new RazorpayClient(keyId, keySecret);
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("amount", paymentDto.getAmount()); // amount in the smallest currency unit
			orderRequest.put("currency", paymentDto.getCurrency());
			/**
			 * orderRequest.put("receipt", "order_rcptid_11");
			 */
			order = razorpayClient.Orders.create(orderRequest);
		} catch (RazorpayException e) {
			e.printStackTrace();
		}

		JSONObject jsonObject = new JSONObject(String.valueOf(order));
		id = jsonObject.getString("id");

		PaymentDetails paymentDetails = new PaymentDetails();
		paymentDetails.setAmount(paymentDto.getAmount() / 100);
		paymentDetails.setPaymentDate(new Date());
		paymentDetails.setPaymentUser(userInformation);
		paymentDetails.setPaymentOrderId(id);
		paymentRepository.save(paymentDetails);
		return id;

	}

	// End of getOrderId method.

	public void validateCart(List<CartProduct> cartProducts) {
		if (cartProducts.isEmpty()) {
			throw new ProductException("Empty Cart");
		}
		for (CartProduct cartProduct : cartProducts) {
			ProductAccessoryVariant productAccessory = cartProduct.getProductAccessory();
			ProductClothVariant productCloth = cartProduct.getProductCloth();
			ProductSupplementVariant productSupplement = cartProduct.getProductSupplement();
			if ((productAccessory != null && (productAccessory.getQuantity() < 1
					|| productAccessory.getQuantity() < cartProduct.getQuantity()))
					|| (productCloth != null && (productCloth.getQuantity() < 1
							|| productCloth.getQuantity() < cartProduct.getQuantity()))
					|| (productSupplement != null && (productSupplement.getQuantity() < 1
							|| productSupplement.getQuantity() < cartProduct.getQuantity()))) {
				throw new ProductException(OUT_OF_STOCK);
			}
			updateStock(cartProduct, productAccessory, productCloth, productSupplement);
		}
	}

	public void updateStock(CartProduct cartProduct, ProductAccessoryVariant productAccessory,
			ProductClothVariant productCloth, ProductSupplementVariant productSupplement) {
		if (productAccessory != null) {
			if (productAccessory.getQuantity() - cartProduct.getQuantity() < 0) {
				throw new ProductException(OUT_OF_STOCK);
			}
			productAccessory.setQuantity(productAccessory.getQuantity() - cartProduct.getQuantity());
		} else if (productCloth != null) {
			if (productCloth.getQuantity() - cartProduct.getQuantity() < 0) {
				throw new ProductException(OUT_OF_STOCK);
			}
			productCloth.setQuantity(productCloth.getQuantity() - cartProduct.getQuantity());
		} else if (productSupplement != null) {
			if (productSupplement.getQuantity() - cartProduct.getQuantity() < 0) {
				throw new ProductException(OUT_OF_STOCK);
			}
			productSupplement.setQuantity(productSupplement.getQuantity() - cartProduct.getQuantity());
		}
	}

	/**
	 * This method is implemented to verify the signature.
	 * 
	 * @param data
	 * @return boolean
	 */
	public boolean verifySignature(RazorpayResponse data) {
		try {
			generatedSignature = calculateRFC2104HMAC(data.getRazorpayOrderId() + "|" + data.getRazorpayPaymentId(),
					keySecret);
		} catch (SignatureException e) {
			e.printStackTrace();
		}
		boolean flag = generatedSignature.equals(data.getRazorpaySignature());
		PaymentDetails paymentDetails = paymentRepository.findByPaymentOrderId(data.getOrderId())
				.orElseThrow(() -> new PlaceOrderException("OrderId Mismacthed"));
		if (flag) {
			BeanUtils.copyProperties(data, paymentDetails);
		}
		return flag;

	}// End of verifySignature method.

	/**
	 * This method is implemented to calculateRFC2104HMAC.
	 * 
	 * @param data
	 * @param secret
	 * @return String
	 * @throws java.security.SignatureException
	 */
	public static String calculateRFC2104HMAC(String data, String secret) throws java.security.SignatureException {
		String result;
		try {

			// get an hmac_sha256 key from the raw secret bytes
			SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), hmacSha256Alorithm);

			// get an hmac_sha256 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance(hmacSha256Alorithm);
			mac.init(signingKey);

			// compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(data.getBytes());

			// base64-encode the hmac
			result = DatatypeConverter.printHexBinary(rawHmac).toLowerCase();

		} catch (Exception e) {
			throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
		}
		return result;
	}// End of calculateRFC2104HMAC method

}// End of PaymentServiceImple class.

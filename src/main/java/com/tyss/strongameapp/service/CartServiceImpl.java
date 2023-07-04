package com.tyss.strongameapp.service;

import static com.tyss.strongameapp.constants.CartConstants.OUT_OF_STOCK;
import static com.tyss.strongameapp.constants.CartConstants.PRODUCT_REMOVED;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tyss.strongameapp.constants.CartConstants;
import com.tyss.strongameapp.dto.CartDto;
import com.tyss.strongameapp.dto.CartProductDto;
import com.tyss.strongameapp.dto.ProductInformationDto;
import com.tyss.strongameapp.dto.ProductSizeStockDto;
import com.tyss.strongameapp.dto.UpdateCartProductDto;
import com.tyss.strongameapp.entity.CartProduct;
import com.tyss.strongameapp.entity.ProductAccessoryVariant;
import com.tyss.strongameapp.entity.ProductClothVariant;
import com.tyss.strongameapp.entity.ProductInformation;
import com.tyss.strongameapp.entity.ProductSupplementVariant;
import com.tyss.strongameapp.entity.UserInformation;
import com.tyss.strongameapp.exception.ProductException;
import com.tyss.strongameapp.exception.UserNotExistException;
import com.tyss.strongameapp.repository.CartProductRepo;
import com.tyss.strongameapp.repository.ProductAccessoryVariantRepo;
import com.tyss.strongameapp.repository.ProductClothVariantRepo;
import com.tyss.strongameapp.repository.ProductInformationRepository;
import com.tyss.strongameapp.repository.ProductSupplementVariantRepo;
import com.tyss.strongameapp.repository.UserInformationRepository;

/**
 * This is the service implementation class for CartService interface. Here you
 * find implementation for saving, updating, fetching and deleting the cart
 * product
 * 
 * @author Afridi
 * 
 */
@Service
public class CartServiceImpl implements CartService {

	/**
	 * private static final String SIZEPATTERN =
	 * "^X{0,2}M{0,1}S{0,1}L{0,1}K{0,1}G{0,1}N{0,1}A{0,1}$";
	 * 
	 * /** This field is used for invoking persistence layer methods
	 */
	@Autowired
	private ProductInformationRepository productRepo;

	/**
	 * This field is used for invoking persistence layer methods
	 */
	@Autowired
	private UserInformationRepository userRepo;

	/**
	 * This field is used for invoking persistence layer methods
	 */
	@Autowired
	private CartProductRepo cartProductRepo;

	@Autowired
	private ProductClothVariantRepo productClothVariantRepo;

	@Autowired
	private ProductSupplementVariantRepo productSupplementVariantRepo;

	/**
	 * This field is used for invoking myShopService methods to fetch coins
	 */
	@Autowired
	private MyShopService myShopService;

	@Autowired
	private ProductAccessoryVariantRepo productAccessoryVariantRepo;

	ProductInformationDto productInformationDto;

	/**
	 * This method is implemented to get total number of coins used for cart
	 * products
	 * 
	 * @param userInformation
	 */
	public double userCoinInCart(List<CartProduct> cartProductList) {
		return cartProductList.stream().filter(x -> !x.isOrderd()).map(CartProduct::getUsedCoins).reduce(0.0,
				Double::sum);
	}

	/**
	 * This method is implemented to save Cart Product
	 * 
	 * @param quantityDto
	 * @param userId
	 * @return ProductInformationDto
	 */
	@Override
	@Transactional
	public String addToCart(CartProductDto cartProductDto, int userId, int productId) {
		UserInformation userInformation = userRepo.findById(userId).orElseThrow(UserNotExistException::new);
		ProductInformation product = productRepo.findById(productId)
				.orElseThrow(() -> new ProductException("Product Not found"));
		if (product.isDeleted()) {
			throw new ProductException(PRODUCT_REMOVED);
		}
		ProductInformationDto addToCartProduct = new ProductInformationDto();
		CartProduct cartProduct = new CartProduct();
		if (product.getProductType().equalsIgnoreCase("ACCESSORY")) {
			ProductAccessoryVariant productAccessoryVariant = productAccessoryVariantRepo
					.findById(cartProductDto.getVariantId())
					.orElseThrow(() -> new ProductException(CartConstants.PRODUCT_VARIANT_NOT_AVAILABLE));
			cartProduct.setProductAccessory(productAccessoryVariant);
			List<CartProduct> cartProducts = cartProductRepo
					.findAllByProductAccessoryAndCartProudutUser(productAccessoryVariant, userInformation);
			cartProduct = cartProducts.stream().filter(x -> !x.isOrderd()).findFirst().orElse(cartProduct);
			BeanUtils.copyProperties(productAccessoryVariant, addToCartProduct);
		} else if (product.getProductType().equalsIgnoreCase("CLOTH")) {
			ProductClothVariant productClothVariant = productClothVariantRepo.findById(cartProductDto.getVariantId())
					.orElseThrow(() -> new ProductException(CartConstants.PRODUCT_VARIANT_NOT_AVAILABLE));
			cartProduct.setProductCloth(productClothVariant);
			List<CartProduct> cartProducts = cartProductRepo
					.findAllByProductClothAndCartProudutUser(productClothVariant, userInformation);
			cartProduct = cartProducts.stream().filter(x -> !x.isOrderd()).findFirst().orElse(cartProduct);
			BeanUtils.copyProperties(productClothVariant, addToCartProduct);
		} else if (product.getProductType().equalsIgnoreCase("SUPPLEMENT")) {
			ProductSupplementVariant productSupplementVariant = productSupplementVariantRepo
					.findById(cartProductDto.getVariantId())
					.orElseThrow(() -> new ProductException(CartConstants.PRODUCT_VARIANT_NOT_AVAILABLE));
			cartProduct.setProductSupplement(productSupplementVariant);
			List<CartProduct> cartProducts = cartProductRepo
					.findAllByProductSupplementAndCartProudutUser(productSupplementVariant, userInformation);
			cartProduct = cartProducts.stream().filter(x -> !x.isOrderd()).findFirst().orElse(cartProduct);
			BeanUtils.copyProperties(productSupplementVariant, addToCartProduct);
		} else {
			throw new ProductException("Product Variant Not found");
		}
		return validateAddToCart(userInformation, cartProduct, cartProductDto, addToCartProduct);

	}

	@Transactional
	private String validateAddToCart(UserInformation userInformation, CartProduct cartProduct,
			CartProductDto cartProductDto, ProductInformationDto addToCartProduct) {
		double userTotalCoins = myShopService.getCoin(userInformation);
		if (addToCartProduct.isDeleted()) {
			throw new ProductException(CartConstants.PRODUCT_VARIANT_NOT_AVAILABLE);
		}
		cartProduct.setQuantity(cartProduct.getQuantity() + 1);
		if (cartProduct.getQuantity() > addToCartProduct.getQuantity()) {
			throw new ProductException(OUT_OF_STOCK + " Availibility " + addToCartProduct.getQuantity());
		}

		List<CartProduct> userCartProducts = userInformation.getCartProducts();
		double restrictedCoins = addToCartProduct.getCoins();
		double userCoinInCart = userCoinInCart(userCartProducts);
		if (cartProductDto.getUsedCoins() > restrictedCoins) {
			throw new ProductException(CartConstants.COINS_1 + restrictedCoins + CartConstants.COINS_2);
		}
		if (cartProductDto.getUsedCoins() > userTotalCoins) {
			throw new ProductException(userTotalCoins + " " + CartConstants.INSUFFICENT_COINS);
		}

		if (userCoinInCart + cartProductDto.getUsedCoins() > userTotalCoins) {
			throw new ProductException(CartConstants.INSUFFICENT_COINS);
		}
		double actualPrice = addToCartProduct.getPrice();
		double discount = addToCartProduct.getDiscount();
		if (cartProduct.getCartProductId() != 0) {
			cartProduct.setUsedCoins(cartProduct.getUsedCoins() + cartProductDto.getUsedCoins());
			cartProduct.setToBePaid(((actualPrice - (discount * actualPrice / 100)) * (cartProduct.getQuantity()))
					- cartProduct.getUsedCoins());
			return CartConstants.PRODUCT_REPEAT_IN_CART;
		}
		cartProduct.setUsedCoins(cartProductDto.getUsedCoins());
		cartProduct.setCartProudutUser(userInformation);
		cartProduct.setToBePaid(((actualPrice - (discount * actualPrice / 100)) * cartProduct.getQuantity())
				- cartProductDto.getUsedCoins());
		cartProductRepo.save(cartProduct);
		return CartConstants.SUCCESSFULLY_ADD_TO_CART;
	}

	/**
	 * This method is implemented to update Cart Product
	 * 
	 * @param updateCartProductDto
	 * @param userId
	 * @return UpdateCartProductDto
	 */
	@Override
	@Transactional
	public UpdateCartProductDto updateCart(UpdateCartProductDto dto, int userId) {
		UserInformation userInformation = userRepo.findById(userId).orElseThrow(UserNotExistException::new);
		ProductInformation product = productRepo.findById(dto.getProductId())
				.orElseThrow(() -> new ProductException(CartConstants.PRODUCT_VARIANT_NOT_AVAILABLE));
		if (product.isDeleted()) {
			throw new ProductException(PRODUCT_REMOVED);
		}
		List<CartProduct> cartProducts = userInformation.getCartProducts();
		if (cartProducts.isEmpty() && cartProducts.stream().allMatch(CartProduct::isOrderd)) {
			throw new ProductException("Cart is Empty");
		}
		ProductInformationDto updateToCartProduct = new ProductInformationDto();
		CartProduct cartProduct = cartProductRepo.findById(dto.getCartProductId())
				.orElseThrow(() -> new ProductException("Cart Product Not found"));
		if (cartProduct.getProductAccessory() != null) {
			if ((cartProducts.stream().noneMatch(x -> x.getCartProductId() == dto.getCartProductId()) || product
					.getAccessoryVariants().stream().noneMatch(x -> x.getAccessoryVariantId() == dto.getVariantId()))) {
				throw new ProductException(CartConstants.UNABLE_TO_UPDATE);
			}
			if (cartProducts.stream().anyMatch(x -> !x.isOrderd() && x.getCartProductId() != dto.getCartProductId()
					&& x.getProductAccessory().getAccessoryVariantId() == dto.getVariantId())) {
				throw new ProductException(CartConstants.ALREADY_EXIST);
			}
			ProductAccessoryVariant productAccessoryVariant = productAccessoryVariantRepo.findById(dto.getVariantId())
					.orElseThrow(() -> new ProductException(CartConstants.PRODUCT_VARIANT_NOT_AVAILABLE));
			BeanUtils.copyProperties(productAccessoryVariant, updateToCartProduct);

			cartProduct.setProductAccessory(productAccessoryVariant);
		} else if (cartProduct.getProductCloth() != null) {
			if (cartProducts.stream().noneMatch(x -> x.getCartProductId() == dto.getCartProductId()) || product
					.getClothVariants().stream().noneMatch(x -> x.getClothVariantId() == dto.getVariantId())) {
				throw new ProductException(CartConstants.UNABLE_TO_UPDATE);
			}
			if (cartProducts.stream().anyMatch(x -> !x.isOrderd() && x.getCartProductId() != dto.getCartProductId()
					&& x.getProductCloth().getClothVariantId() == dto.getVariantId())) {
				throw new ProductException(CartConstants.ALREADY_EXIST);
			}
			ProductClothVariant productClothVariant = productClothVariantRepo.findById(dto.getVariantId())
					.orElseThrow(() -> new ProductException(CartConstants.PRODUCT_VARIANT_NOT_AVAILABLE));
			BeanUtils.copyProperties(productClothVariant, updateToCartProduct);
			cartProduct.setProductCloth(productClothVariant);
		} else if (cartProduct.getProductSupplement() != null) {
			if (cartProducts.stream().noneMatch(x -> x.getCartProductId() == dto.getCartProductId())
					|| product.getProductSupplementVariant().stream()
							.noneMatch(x -> x.getProductSupplementVariantId() == dto.getVariantId())) {
				throw new ProductException(CartConstants.UNABLE_TO_UPDATE);
			}
			if (cartProducts.stream().anyMatch(x -> !x.isOrderd() && x.getCartProductId() != dto.getCartProductId()
					&& x.getProductSupplement().getProductSupplementVariantId() == dto.getVariantId())) {
				throw new ProductException(CartConstants.ALREADY_EXIST);
			}
			ProductSupplementVariant productSupplementVariant = productSupplementVariantRepo
					.findById(dto.getVariantId())
					.orElseThrow(() -> new ProductException(CartConstants.PRODUCT_VARIANT_NOT_AVAILABLE));
			BeanUtils.copyProperties(productSupplementVariant, updateToCartProduct);
			cartProduct.setProductSupplement(productSupplementVariant);
		} else {
			throw new ProductException(CartConstants.PRODUCT_VARIANT_NOT_AVAILABLE);
		}
		return validateUpdateToCart(updateToCartProduct, cartProduct, dto, userInformation);
	}

	@Transactional
	private UpdateCartProductDto validateUpdateToCart(ProductInformationDto updateToCartProduct,
			CartProduct cartProduct, UpdateCartProductDto dto, UserInformation userInformation) {
		double userTotalCoins = myShopService.getCoin(userInformation);
		double userCoinInCart = userCoinInCart(userInformation.getCartProducts());
		if (updateToCartProduct.isDeleted()) {
			throw new ProductException(CartConstants.PRODUCT_VARIANT_NOT_AVAILABLE);
		}
		if (dto.getQuantity() > updateToCartProduct.getQuantity()) {
			cartProductRepo.deleteById(cartProduct.getCartProductId());
			throw new ProductException(OUT_OF_STOCK);
		}
		if (dto.getUsedCoins() > userTotalCoins) {
			throw new ProductException(userTotalCoins + " " + CartConstants.INSUFFICENT_COINS);
		}
		if (dto.getUsedCoins() > updateToCartProduct.getCoins() * dto.getQuantity()) {
			throw new ProductException(
					CartConstants.COINS_1 + updateToCartProduct.getCoins() * dto.getQuantity() + CartConstants.COINS_2);
		}
		if (userCoinInCart - cartProduct.getUsedCoins() + dto.getUsedCoins() > userTotalCoins) {
			throw new ProductException((userTotalCoins - userCoinInCart) + " " + CartConstants.INSUFFICENT_COINS);
		}
		if (updateToCartProduct.getCoins() < cartProduct.getUsedCoins()) {
			cartProduct.setUsedCoins(updateToCartProduct.getCoins());
		}
		cartProduct.setQuantity(dto.getQuantity());
		double actualPrice = updateToCartProduct.getPrice();
		double discount = updateToCartProduct.getDiscount();

		cartProduct
				.setToBePaid(((actualPrice - (discount * actualPrice / 100)) * dto.getQuantity()) - dto.getUsedCoins());
		cartProduct.setUsedCoins(dto.getUsedCoins());
		return dto;
	}

	/**
	 * This method is implemented to view Cart
	 * 
	 * @param userId
	 */
	@Override
	@Transactional
	public CartDto viewCart(int userId) {
		UserInformation user = userRepo.findById(userId).orElseThrow(UserNotExistException::new);
		CartDto cartDto = new CartDto();
		cartDto.setUserId(userId);
		double finalCartPrice = 0.0;
		double actualCartPrice = 0.0;
		double totalDiscountPrice = 0.0;
		double discount;
		double savings;
		List<CartProduct> cartProducts = user.getCartProducts().stream().filter(x -> !x.isOrderd())
				.collect(Collectors.toList());
		if (cartProducts.isEmpty()) {
			return null;
		}
		List<ProductInformationDto> dtos = new ArrayList<>();
		for (CartProduct cartProduct : cartProducts) {
			if (!cartProduct.isOrderd()) {
				ProductAccessoryVariant productAccessory = cartProduct.getProductAccessory();
				ProductClothVariant productCloth = cartProduct.getProductCloth();
				ProductSupplementVariant productSupplement = cartProduct.getProductSupplement();
				productInformationDto = new ProductInformationDto();
				productInformationDto.setMessage("");
				if (productAccessory != null) {
					productInformationDto = accessoryToProductDto(productAccessory, cartProduct);
				} else if (productCloth != null) {
					productInformationDto = clothToProductDto(productCloth, cartProduct);
				} else if (productSupplement != null) {
					productInformationDto = supplementToProductDto(productSupplement, cartProduct);
				} else {
					throw new ProductException("Product Variant Not Found");
				}
				validate(cartProduct);
				discount = productInformationDto.getDiscount();
				savings = ((discount * productInformationDto.getPrice()) / 100);
				BeanUtils.copyProperties(cartProduct, productInformationDto);
				productInformationDto.setCartProductId(cartProduct.getCartProductId());
				productInformationDto.setFinalPrice((productInformationDto.getPrice() - savings));
				dtos.add(productInformationDto);
				finalCartPrice += cartProduct.getToBePaid();
				actualCartPrice += productInformationDto.getPrice() * cartProduct.getQuantity();
				totalDiscountPrice += ((productInformationDto.getPrice() * cartProduct.getQuantity()) / 100) * discount;
			}
		}
		if (productInformationDto.getMessage().length() > 1) {
			productInformationDto
					.setMessage(productInformationDto.getMessage() + " variant has been Removed/Out Of Stock");
		}
		cartDto.setActualCartPrice(actualCartPrice);
		cartDto.setTotalDiscountPrice(totalDiscountPrice);
		cartDto.setFinalCartPrice(finalCartPrice);
		cartDto.setTotalUsedCoins(userCoinInCart(cartProducts));
		cartDto.setCartProduct(dtos);
		return cartDto;
	}

	private void validate(CartProduct cartProduct) {
		if (productInformationDto.isDeleted()) {
			cartProductRepo.deleteById(cartProduct.getCartProductId());
		}
		if (productInformationDto.getQuantity() < cartProduct.getQuantity()) {
			throw new ProductException(OUT_OF_STOCK);
		}
	}

	/**
	 * if supplement variant is not null then copying all variant data into
	 * ProductInformationDto
	 * 
	 * @param productSupplement
	 * @param cartProduct
	 * @return
	 */
	@Transactional
	private ProductInformationDto supplementToProductDto(ProductSupplementVariant productSupplement,
			CartProduct cartProduct) {
		List<ProductSizeStockDto> sizeStockDtos = new ArrayList<>();
		ProductInformation information = productSupplement.getSupplementProduct();
		if (information.isDeleted()) {
			cartProductRepo.deleteById(cartProduct.getCartProductId());
			productInformationDto.setMessage(productInformationDto.getMessage() + " | " + information.getProductName());
		}
		List<ProductSupplementVariant> list = information.getProductSupplementVariant().stream()
				.filter(x -> x.getFlavour().equalsIgnoreCase(productSupplement.getFlavour()))
				.collect(Collectors.toList());
		for (ProductSupplementVariant supplementVariant : list) {
			if (supplementVariant.getQuantity() < 1) {
				cartProductRepo.deleteCartProduct(cartProduct.getCartProductId(), false);
				productInformationDto
						.setMessage(productInformationDto.getMessage() + " | " + information.getProductName());
			}
			ProductSizeStockDto sizeStockDto = new ProductSizeStockDto();
			sizeStockDto.setVariantId(supplementVariant.getProductSupplementVariantId());
			sizeStockDto.setStockAvailable(supplementVariant.getQuantity());
			sizeStockDto.setSize(supplementVariant.getSize() + " " + supplementVariant.getUnit());
			sizeStockDtos.add(sizeStockDto);
		}
		BeanUtils.copyProperties(information, productInformationDto);
		BeanUtils.copyProperties(productSupplement, productInformationDto);
		productInformationDto.setSize(productSupplement.getSize() + " " + productSupplement.getUnit());
		if (!productSupplement.getImages().isEmpty()) {
			productInformationDto.setProductImage(productSupplement.getImages().get(0));
		}
		productInformationDto.setVariantType("SUPPLEMENT");
		productInformationDto.setProductSizeStocks(sizeStockDtos);
		productInformationDto.setVariantId(productSupplement.getProductSupplementVariantId());
		return productInformationDto;
	}

	/**
	 * if cloth variant is not null then copying all variant data into
	 * ProductInformationDto
	 * 
	 * @param productCloth
	 * @param cartProduct
	 * @return
	 */
	@Transactional
	private ProductInformationDto clothToProductDto(ProductClothVariant productCloth, CartProduct cartProduct) {
		List<ProductSizeStockDto> sizeStockDtos = new ArrayList<>();
		ProductInformation information = productCloth.getClothProduct();
		if (information.isDeleted()) {
			cartProductRepo.deleteById(cartProduct.getCartProductId());
			productInformationDto.setMessage(productInformationDto.getMessage() + " | " + information.getProductName());
		}
		List<ProductClothVariant> list = information.getClothVariants().stream()
				.filter(x -> x.getColor().equalsIgnoreCase(productCloth.getColor())).collect(Collectors.toList());
		for (ProductClothVariant clothVariant : list) {
			if (clothVariant.getQuantity() < 1) {
				cartProductRepo.deleteCartProduct(cartProduct.getCartProductId(), false);
				productInformationDto
						.setMessage(productInformationDto.getMessage() + " | " + information.getProductName());
			}
			ProductSizeStockDto sizeStockDto = new ProductSizeStockDto();
			sizeStockDto.setVariantId(clothVariant.getClothVariantId());
			sizeStockDto.setStockAvailable(clothVariant.getQuantity());
			sizeStockDto.setSize(clothVariant.getSize());
			sizeStockDtos.add(sizeStockDto);
		}
		BeanUtils.copyProperties(information, productInformationDto);
		BeanUtils.copyProperties(productCloth, productInformationDto);
		if (!productCloth.getImages().isEmpty()) {
			productInformationDto.setProductImage(productCloth.getImages().get(0));
		}
		productInformationDto.setVariantType("CLOTH");
		productInformationDto.setProductSizeStocks(sizeStockDtos);
		productInformationDto.setVariantId(productCloth.getClothVariantId());
		return productInformationDto;
	}

	/**
	 * if accessory variant is not null then copying all variant data into
	 * ProductInformationDto
	 * 
	 * @param productAccessory
	 * @param cartProduct
	 * @return
	 */
	@Transactional
	private ProductInformationDto accessoryToProductDto(ProductAccessoryVariant productAccessory,
			CartProduct cartProduct) {
		List<ProductSizeStockDto> sizeStockDtos = new ArrayList<>();
		ProductInformation information = productAccessory.getAccessoryProduct();
		if (information.isDeleted()) {
			cartProductRepo.deleteCartProduct(cartProduct.getCartProductId(), false);
			productInformationDto.setMessage(productInformationDto.getMessage() + " | " + information.getProductName());
		}
		List<ProductAccessoryVariant> list = information.getAccessoryVariants().stream()
				.filter(x -> x.getColor().equalsIgnoreCase(productAccessory.getColor())).collect(Collectors.toList());
		for (ProductAccessoryVariant accessoryVariant : list) {
			if (accessoryVariant.getQuantity() < 1) {
				cartProductRepo.deleteCartProduct(cartProduct.getCartProductId(), false);
				productInformationDto
						.setMessage(productInformationDto.getMessage() + " | " + information.getProductName());
			}
			ProductSizeStockDto sizeStockDto = new ProductSizeStockDto();
			sizeStockDto.setVariantId(accessoryVariant.getAccessoryVariantId());
			sizeStockDto.setStockAvailable(accessoryVariant.getQuantity());
			sizeStockDto.setSize(accessoryVariant.getSize() + " " + accessoryVariant.getUnit());
			sizeStockDtos.add(sizeStockDto);
		}
		BeanUtils.copyProperties(information, productInformationDto);
		BeanUtils.copyProperties(productAccessory, productInformationDto);
		productInformationDto.setSize(productAccessory.getSize() + " " + productAccessory.getUnit());
		if (!productAccessory.getImages().isEmpty()) {
			productInformationDto.setProductImage(productAccessory.getImages().get(0));
		}
		productInformationDto.setVariantType("ACCESSORY");
		productInformationDto.setProductSizeStocks(sizeStockDtos);
		productInformationDto.setVariantId(productAccessory.getAccessoryVariantId());
		return productInformationDto;
	}

	/**
	 * This method is implemented to delete Cart Products
	 * 
	 * @param userId
	 * @param cartProductId
	 */
	@Override
	@Transactional
	public boolean deleteCartProuduct(int userId, int cartProductId) {
		if (!userRepo.findById(userId).isPresent()) {
			throw new UserNotExistException();
		}
		CartProduct cartProduct = cartProductRepo.findById(cartProductId)
				.orElseThrow(() -> new ProductException("Cart Product Not Found"));
		if (cartProduct.isOrderd()) {
			throw new ProductException("Product Already been Placed");
		}
		cartProductRepo.deleteById(cartProductId);
		return false;
	}

	@Override
	@Transactional
	public void emptyCart(int userId) {
		UserInformation userInformation = userRepo.findById(userId).orElseThrow(UserNotExistException::new);
		cartProductRepo.deleteAllCartProducts(userInformation.getUserId(), false);

	}

	/**
	 * This method is implemented by its implementation class to update product
	 * quantities if payment is failed
	 * 
	 * @param userId
	 */
	@Override
	@Transactional
	public void updateProductQuantites(int userId) {
		UserInformation userInformation = userRepo.findById(userId).orElseThrow(UserNotExistException::new);
		List<CartProduct> cartProducts = userInformation.getCartProducts().stream().filter(x -> !x.isOrderd())
				.collect(Collectors.toList());
		for (CartProduct cartProduct : cartProducts) {
			updateStock(cartProduct);
		}
	}

	public void updateStock(CartProduct cartProduct) {
		ProductAccessoryVariant productAccessory = cartProduct.getProductAccessory();
		ProductClothVariant productCloth = cartProduct.getProductCloth();
		ProductSupplementVariant productSupplement = cartProduct.getProductSupplement();
		if (productAccessory != null) {
			productAccessory.setQuantity(productAccessory.getQuantity() + cartProduct.getQuantity());
		} else if (productCloth != null) {
			productCloth.setQuantity(productCloth.getQuantity() + cartProduct.getQuantity());
		} else if (productSupplement != null) {
			productSupplement.setQuantity(productSupplement.getQuantity() + cartProduct.getQuantity());
		}
	}

}

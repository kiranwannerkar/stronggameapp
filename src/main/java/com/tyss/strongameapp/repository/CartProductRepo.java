package com.tyss.strongameapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.tyss.strongameapp.entity.CartProduct;
import com.tyss.strongameapp.entity.ProductAccessoryVariant;
import com.tyss.strongameapp.entity.ProductClothVariant;
import com.tyss.strongameapp.entity.ProductSupplementVariant;
import com.tyss.strongameapp.entity.UserInformation;

public interface CartProductRepo extends JpaRepository<CartProduct, Integer> {

	List<CartProduct> findAllByProductAccessoryAndCartProudutUser(ProductAccessoryVariant productAccessoryVariant,
			UserInformation userInformation);

	List<CartProduct> findAllByProductSupplementAndCartProudutUser(ProductSupplementVariant productSupplementVariant,
			UserInformation userInformation);

	List<CartProduct> findAllByProductClothAndCartProudutUser(ProductClothVariant productClothVariant,
			UserInformation userInformation);

	@Modifying
	@Query(value = "delete from cart_product where user_id = :userId and is_ordered=:flag", nativeQuery = true)
	void deleteAllCartProducts(int userId, boolean flag);

	@Modifying
	@Query(value = "delete from cart_product where cart_product_id = :cartId and is_ordered=:flag", nativeQuery = true)
	void deleteCartProduct(int cartId, boolean flag);

}

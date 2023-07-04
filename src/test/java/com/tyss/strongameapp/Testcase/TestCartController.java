package com.tyss.strongameapp.Testcase;

import static com.tyss.strongameapp.constants.CartConstants.SUCCESSFULLY_REMOVED_FROM_CART;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tyss.strongameapp.constants.CartConstants;
import com.tyss.strongameapp.dto.CartDto;
import com.tyss.strongameapp.dto.CartProductDto;
import com.tyss.strongameapp.dto.ResponseDto;
import com.tyss.strongameapp.dto.UpdateCartProductDto;
import com.tyss.strongameapp.service.CartService;
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TestCartController {
	
	private MockMvc mockmvc;
	
	@Autowired
	WebApplicationContext contex;
	
	ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	public void setup() {
		 mockmvc = MockMvcBuilders.webAppContextSetup(contex).build();
	}
	
	@MockBean
	private CartService cartService;
	
	
	
	
	/**
	 *      TEST CASE FOR CartController.java
	 */
	
	@Test
	void testAddToCartWhenSuccess() throws Exception {
		CartProductDto quantityDto = new CartProductDto();
		quantityDto.setQuantity(10);
		quantityDto.setUsedCoins(6);
		quantityDto.setVariantId(10);
		String writeValueAsString = objectMapper.writeValueAsString(quantityDto);
		
		String responseMsg =  "Successfully Added to Cart";
		
		Mockito.when(cartService.addToCart(quantityDto, 10, 20)).thenReturn(CartConstants.SUCCESSFULLY_ADD_TO_CART,CartConstants.PRODUCT_REPEAT_IN_CART);
		
		String contentAsString = mockmvc.perform(post("/mycart/addtocart/{userId}/{productId}", 10, 20)/*("/mycart/addtocart/10/20")*/
				.contentType(MediaType.APPLICATION_JSON)
				.content(writeValueAsString))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
//		         .andDo(print())
//				.andExpect(jsonPath("$.error").value(false)).andExpect(jsonPath("$.data").value(responseMsg));
	
	    ResponseDto readValue = objectMapper.readValue(contentAsString, ResponseDto.class);
	    assertEquals(false, readValue.isError());
	}
	
	
	@Test
	void testAddToCartWhenFail() throws Exception {
		CartProductDto quantityDto = new CartProductDto();
		quantityDto.setQuantity(10);
		quantityDto.setUsedCoins(6);
		quantityDto.setVariantId(10);
		String writeValueAsString = objectMapper.writeValueAsString(quantityDto);
		
		Mockito.when(cartService.addToCart(quantityDto, 10, 20)).thenReturn(CartConstants.PRODUCT_NOT_FOUND);
		
		String contentAsString = mockmvc.perform(post("/mycart/addtocart/{userId}/{productId}", 10, 20)/*("/mycart/addtocart/10/20")*/
				.contentType(MediaType.APPLICATION_JSON)
				.content(writeValueAsString))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
//		         .andDo(print())
//				.andExpect(jsonPath("$.error").value(false)).andExpect(jsonPath("$.data").value(responseMsg));
	
	    ResponseDto readValue = objectMapper.readValue(contentAsString, ResponseDto.class);
	    assertEquals(true, readValue.isError());
	
	
	}
	
	
	@Test
	void testupdateCartwithSucess() throws Exception {
		UpdateCartProductDto updatecard=new UpdateCartProductDto();
		updatecard.setCartProductId(1);
		updatecard.setProductId(1);
		updatecard.setQuantity(7);
		updatecard.setUsedCoins(7);
		updatecard.setVariantId(4);
		String writeValueAsString = objectMapper.writeValueAsString(updatecard);
		UpdateCartProductDto dto=new UpdateCartProductDto();//new dto object
		
		Mockito.when(cartService.updateCart(updatecard, 1)).thenReturn(updatecard);
		String contentAsString = mockmvc
		       .perform(put("/mycart/updatecart/{userId}",1)
		      // .contentType(MediaType.ALL)
		       .contentType(MediaType.APPLICATION_JSON_VALUE)
		       .content(writeValueAsString)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
				
		
			  ResponseDto readValue = objectMapper.readValue(contentAsString, ResponseDto.class);
				assertEquals(false,readValue.isError());
	}

	
	@Test
	void testupdateCartwithFail() throws Exception {
		UpdateCartProductDto updatecard=new UpdateCartProductDto();
		updatecard.setCartProductId(1);
		updatecard.setProductId(1);
		updatecard.setQuantity(7);
		updatecard.setUsedCoins(7); 
		updatecard.setVariantId(4);
		String writeValueAsString = objectMapper.writeValueAsString(updatecard);
		UpdateCartProductDto dto=new UpdateCartProductDto();//new dto object
		 String writeValueAsString2 = objectMapper.writeValueAsString(dto);
		
		Mockito.when(cartService.updateCart(updatecard, 1)).thenReturn(null);
	
        String contentAsString = mockmvc
		       .perform(put("/mycart/updatecart/{userId}",0)
		       .accept(MediaType.ALL)
		       .contentType(MediaType.APPLICATION_JSON_VALUE).content(writeValueAsString2))
		       .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
			  ResponseDto readValue = objectMapper.readValue(contentAsString, ResponseDto.class);
				//assertEquals(CartConstants.INVALID_INPUT,readValue.getData());
			   assertEquals(true,readValue.isError());
	
	}
	
	
	@Test
	void TestdeleteCartProuductSucess() throws Exception {
//		 userId and userId
		boolean flag=true;
		Mockito.when(cartService.deleteCartProuduct(Mockito.anyInt(), Mockito.anyInt())).thenReturn(flag);
		
		String contentAsString = mockmvc.perform(delete("/mycart/removeproduct/{userId}/{cartProductId}",1,2)
				.accept(MediaType.ALL)
				.contentType(MediaType.APPLICATION_NDJSON_VALUE))
				.andExpect(status().isNotFound())
				.andReturn()
				.getResponse()
				.getContentAsString();
		 ResponseDto readValue = objectMapper.readValue(contentAsString, ResponseDto.class);
		assertEquals(true,readValue.isError());
	
	}
	
	@Test
	void TestdeleteCartProuductfail() throws Exception {
//		 userId and userId
		boolean flag=false;
		Mockito.when(cartService.deleteCartProuduct(Mockito.anyInt(), Mockito.anyInt())).thenReturn(flag);
		
		String contentAsString = mockmvc.perform(delete("/mycart/removeproduct/{userId}/{cartProductId}",1,2)
				.accept(MediaType.ALL)
				.contentType(MediaType.APPLICATION_NDJSON_VALUE))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
		 ResponseDto readValue = objectMapper.readValue(contentAsString, ResponseDto.class);
		assertEquals(CartConstants.SUCCESSFULLY_REMOVED_FROM_CART,readValue.getData());
	
	}
	
	@Test
	void TestviewCartsucess() throws  Exception {
		CartDto obj=new CartDto();
		Mockito.when(cartService.viewCart(Mockito.anyInt())).thenReturn(obj);
		
		String contentAsString = mockmvc.perform(get("/mycart/emptycart/{userId}",1)
				.accept(MediaType.ALL)
				.content(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
		ResponseDto readValue = objectMapper.readValue(contentAsString, ResponseDto.class);
		assertEquals(false, readValue.isError());
		
	}
	
	
	@Test
	void TestviewCartFail() throws  Exception {
		
		Mockito.when(cartService.viewCart(Mockito.anyInt())).thenReturn(null);
		
		String contentAsString = mockmvc.perform(get("/mycart/emptycart/{userId}",1)
				.accept(MediaType.ALL)
				.content(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
		ResponseDto readValue = objectMapper.readValue(contentAsString, ResponseDto.class);
		//assertEquals(CartConstants.EMPTY_CART, readValue.getData());
		assertEquals(false, readValue.getData());
	}
	
}

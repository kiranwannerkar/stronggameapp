package com.tyss.strongameapp.Testcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tyss.strongameapp.constants.CartConstants;
import com.tyss.strongameapp.controller.BuyCartProductController;
import com.tyss.strongameapp.dto.CartProductDto;
import com.tyss.strongameapp.dto.PlaceOrderDto;
import com.tyss.strongameapp.dto.ResponseDto;
import com.tyss.strongameapp.dto.UpdateCartProductDto;
import com.tyss.strongameapp.service.BuyCartProductService;
import com.tyss.strongameapp.service.CartService;
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TestBuyCartProductController {

	
	private MockMvc mockmvc;

	@Autowired
	WebApplicationContext contex;
	
	
    //NON USE
	@Mock 
	BuyCartProductController buyCartProductController;
	
	@MockBean
	private BuyCartProductService buyCartProductService;

	ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	public void setup() {
		mockmvc = MockMvcBuilders.webAppContextSetup(contex).build();
	}
	
	/**
	 1* TEST CASE FOR BuyCartProductController.java
	 */
	
	@Test
	//@Disabled
	public void TestplaceCartOrderTwo() throws Exception {
		PlaceOrderDto placeholder = new PlaceOrderDto();
//		Date date = new Date();
//		placeholder.setAddressId(1);
//		placeholder.setOrderDate(date);
//		placeholder.setUserId(2);
		String AsString = objectMapper.writeValueAsString(placeholder); //CONVERT IN STRING
		
		
//		ResponseDto responseDTO = new ResponseDto();
//		responseDTO.setError(false);
//		responseDTO.setData(placeholder);
//		responseDTO.setMessage("test case run sucessfuly");
//		 String writeValueAsString = objectMapper.writeValueAsString(responseDTO);
		 
		 
	//	Mockito.when(buyCartProductService.placeAnOrder(placeholder)).thenReturn(writeValueAsString);
		
	 String contentAsString = mockmvc.perform(put("/placeanorder")
				.accept(MediaType.ALL)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(AsString))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()     //.getContentType();
				.getContentAsString();
		
//		assertEquals(AsString, writeValueAsString);
		ResponseDto readValue = objectMapper.readValue(contentAsString, ResponseDto.class);//STRING TO OBJECT
		assertEquals(false, readValue.isError());

	}
	
	


}

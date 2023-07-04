package com.tyss.strongameapp.Testcase;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
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
import com.tyss.strongameapp.controller.MyShopController;
import com.tyss.strongameapp.dto.MyShopDTO;
import com.tyss.strongameapp.dto.ProductTypeDTO;
import com.tyss.strongameapp.dto.ResponseDto;
import com.tyss.strongameapp.dto.VariantTypeDTO;
import com.tyss.strongameapp.service.MyShopService;

import ch.qos.logback.core.status.Status;
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TestMyShopController {
	
	@Autowired
	WebApplicationContext contex;

	private MockMvc mockmvc;
	
	ObjectMapper objectmapper; 
	
	@MockBean
	MyShopService myshopservice;
	
	@MockBean
	MyShopController myShopController;
	
	@MockBean
	ProductTypeDTO productTypeDTO;
	
	@BeforeEach
	public void setup() {
		mockmvc = MockMvcBuilders.webAppContextSetup(contex).build();
		// this.mockmvc = MockMvcBuilders.standaloneSetup(ramController).build();
		 objectmapper = new ObjectMapper();
	}
	
	@Test
	public void getFilteredProducts() throws UnsupportedEncodingException, Exception{
		VariantTypeDTO variantTypeDTO=new VariantTypeDTO();	
	
		List<MyShopDTO> dtos=new ArrayList<MyShopDTO>(); 
		MyShopDTO dto= new MyShopDTO();
		dto.setProductId(2);
		dto.setProductName("any_string");
		String productType="any_String";
		dtos.add(dto);
		when(myshopservice.getFilteredProducts(productType, variantTypeDTO)).thenReturn(dtos);
		
		String writeValueAsString = objectmapper.writeValueAsString(variantTypeDTO);
		
		String contentAsString =
				mockmvc.perform(post("/myshop/filter/product/{productType}","any_String")
				.accept(MediaType.ALL)
				.content(MediaType.APPLICATION_JSON_VALUE)
				.content(writeValueAsString))
		        .andExpect(status().isOk())
		        .andReturn().getResponse().getContentAsString();
	    ResponseDto readValue = objectmapper.readValue(contentAsString, ResponseDto.class);//STRING TO OBJECT
		
	    assertEquals(false, readValue.isError());

	}
	
	
	@Test
	public void getFilteredProductsfail() throws Exception {
		VariantTypeDTO variantTypeDTO=new VariantTypeDTO();	
	String writeValueAsString = objectmapper.writeValueAsString(variantTypeDTO);

		
	mockmvc.perform(post("/myshop/filter/product/{productType}","any__string")
		.accept(MediaType.ALL)
		.content(MediaType.APPLICATION_JSON_VALUE)
		.content(writeValueAsString)).andExpect(status().isNotFound());
	}
	
	
	
	@Test
	public void TestgetAllProducts() throws Exception {
		 
		mockmvc.perform(get("/myshop/product/types")
				.accept(MediaType.ALL)
				.content(MediaType.APPLICATION_JSON_VALUE))
			    .andExpect(status().isOk());
	}
	
	
	
	@Test
	public void TestgetAllProductsfail() throws Exception {
//		List<ProductTypeDTO> dtos=new ArrayList<ProductTypeDTO>();
//		when(myshopservice.getProductTypes()).thenReturn(dtos);
		 
		mockmvc.perform(get("/myshop/product/types")
				.accept(MediaType.ALL)
				.content(MediaType.APPLICATION_JSON_VALUE))
			    .andExpect(status().isNotFound());
	
	}
	
	

}

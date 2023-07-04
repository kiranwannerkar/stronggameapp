package com.tyss.strongameapp.Testcase;

import org.junit.jupiter.api.BeforeEach;
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
import com.google.firebase.messaging.FirebaseMessagingException;
import com.tyss.strongameapp.controller.GetSingleCoachController;
import com.tyss.strongameapp.dto.PlanInformationDto;
import com.tyss.strongameapp.dto.ResponseDto;
import com.tyss.strongameapp.dto.SingleCoachDetailsDto;
import com.tyss.strongameapp.service.GetSingleCoachService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockitoSession;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TestGetSingleCoachController {
	

	private MockMvc mockmvc;
	
	@Autowired
	WebApplicationContext contex;
	
	ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	public void setup() {
		 mockmvc = MockMvcBuilders.webAppContextSetup(contex).build();
	}
	@MockBean
	private GetSingleCoachService getSingleCoachService;
	
	@Mock
	GetSingleCoachController getSingleCoachController;
	
    @Test
	void TestgetCoachById() throws  Exception {
//    	 coachId and  userId;
    	SingleCoachDetailsDto obj =new SingleCoachDetailsDto();
    	
    	Mockito.when(getSingleCoachService.getCoachByIdAndValidateExpiry(Mockito.anyInt(),Mockito.anyInt())).thenReturn(obj);
    	String contentAsString = mockmvc.perform(put("/singlecoach/getcoach/{coachId}/{userId}",1,2)
    			.contentType(MediaType.ALL)
    			.contentType(MediaType.APPLICATION_JSON_VALUE))
    			.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    	ResponseDto readValue = objectMapper.readValue(contentAsString,ResponseDto.class);
    	assertEquals(false, readValue.isError());
	}
    
    @Test
    void TestenrollPlanSucess() throws Exception {
//   	 coachId and  userId;
    	PlanInformationDto obj=new PlanInformationDto();
    	String writeValueAsString = objectMapper.writeValueAsString(obj);
    	String flag=" Plan Enrolled Successfully";
    	Mockito.when(getSingleCoachService.enrollPlan(Mockito.anyInt(), Mockito.anyInt(),Mockito.any())).thenReturn(flag);
    	
    	String contentAsString = mockmvc.perform(put("/singlecoach/enrollplan/{userId}/{coachId}",1,2)
    			.accept(MediaType.ALL).contentType(MediaType.APPLICATION_JSON_VALUE)
    			.content(writeValueAsString)).andExpect(status().isOk())
    			.andReturn().getResponse().getContentAsString();
    	
    	 ResponseDto readValue = objectMapper.readValue(contentAsString, ResponseDto.class);
        	assertEquals(false, readValue.isError());
    	
     }
    
    @Test
    void TestenrollPlanFail() throws Exception {
//   	 coachId and  userId;
    	PlanInformationDto obj=new PlanInformationDto();
    	String writeValueAsString = objectMapper.writeValueAsString(obj);
    	String flag= " Coach Slots are Not Availabel.";
    	Mockito.when(getSingleCoachService.enrollPlan(Mockito.anyInt(), Mockito.anyInt(),Mockito.any())).thenReturn(flag);
    	
    	String contentAsString = mockmvc.perform(put("/singlecoach/enrollplan/{userId}/{coachId}",0,0)
    			.accept(MediaType.ALL).contentType(MediaType.APPLICATION_JSON_VALUE)
    			.content(writeValueAsString)).andExpect(status().isNotFound())
    			.andReturn().getResponse().getContentAsString();
    	
    	 ResponseDto readValue = objectMapper.readValue(contentAsString, ResponseDto.class);
        	assertEquals(true, readValue.isError());
    	
     }
    
    @Test
    void TestgetPlanByIdPass() throws Exception {
    //	planId
    	PlanInformationDto obj=new PlanInformationDto();
    	String writeValueAsString = objectMapper.writeValueAsString(obj);
    	Mockito.when(getSingleCoachService.getPlanById(Mockito.anyInt())).thenReturn(obj);
    	String contentAsString = mockmvc.perform(get("/singlecoach/getplanbyid/{planId}",1)
    			.accept(MediaType.ALL)
    			.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk())
    			.andReturn()
    			.getResponse()
    			.getContentAsString();
    	ResponseDto readValue = objectMapper.readValue(contentAsString,ResponseDto.class);
    	assertEquals(false, readValue.isError());
    }
    
    
    @Test
    void TestgetPlanByIdFail() throws Exception {
    //	planId
    	PlanInformationDto obj=new PlanInformationDto();
    	String writeValueAsString = objectMapper.writeValueAsString(obj);
    	Mockito.when(getSingleCoachService.getPlanById(Mockito.anyInt())).thenReturn(null);
    	String contentAsString = mockmvc.perform(get("/singlecoach/getplanbyid/{planId}",0)
    			.accept(MediaType.ALL)
    			.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNotFound())
    			.andReturn()
    			.getResponse()
    			.getContentAsString();
    	   ResponseDto readValue = objectMapper.readValue(contentAsString,ResponseDto.class);
    	   assertEquals(true, readValue.isError());
    }

}

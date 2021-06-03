package esanchez.devel.app.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import esanchez.devel.app.model.dto.TransferDTO;

@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AccountControllerRestTemplateTest {

	@Autowired
	private TestRestTemplate testRestTemplate;
	
	private ObjectMapper objectMapper;
	
	@LocalServerPort
	private Integer port;
	
	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
	}
	
	@Test
	@Order(1)
	void testTransfer() throws JsonMappingException, JsonProcessingException {
		TransferDTO transferDTO = new TransferDTO();
		transferDTO.setOriginAccountId(1L);
		transferDTO.setDestinyAccountId(2L);
		transferDTO.setBankId(1L);
		transferDTO.setAmount(new BigDecimal("100"));
		
		/*
		 * make the request with restTemplate
		 * with the "port" variable we can test without launch the application.
		 * the request also works without the http://localhost:port, only with the endpoint different ways to make the same
		 */
		ResponseEntity<String> response = testRestTemplate.postForEntity("http://localhost:" + port + "/v1/account/transfer", transferDTO, String.class);
		
		//get the response body received
		String json = response.getBody();
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
		assertNotNull(json);
		assertTrue(json.contains("transfer done successfully"));
		
		/*
		 * we can also convert the json string to a jsonNode for make assertions by 
		 * parameter
		 */
		JsonNode jsonNode = objectMapper.readTree(json);
		
		assertEquals("transfer done successfully", jsonNode.path("message").asText());
		assertEquals(LocalDate.now().toString(), jsonNode.path("date").asText());
		assertEquals("100", jsonNode.path("transaction").path("amount").asText());
		assertEquals(1L, jsonNode.path("transaction").path("originAccountId").asLong());

		Map<String, Object> response2 = new HashMap<>();
		response2.put("date", LocalDate.now().toString());
		response2.put("status", "success");
		response2.put("message", "transfer done successfully");
		response2.put("transaction", transferDTO);
		
		assertEquals(objectMapper.writeValueAsString(response2), json);
		
	}
}

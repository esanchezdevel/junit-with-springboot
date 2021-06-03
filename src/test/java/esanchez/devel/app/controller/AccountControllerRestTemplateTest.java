package esanchez.devel.app.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import esanchez.devel.app.model.Account;
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
	
	@Test
	@Order(2)
	void testDetail() {
		ResponseEntity<Account> response = testRestTemplate.getForEntity("/v1/account/1", Account.class);
		Account account = response.getBody();
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
		assertNotNull(account);
		assertEquals(1L, account.getId());
		assertEquals("Andres", account.getName());
		assertEquals("900.00", account.getBalance().toPlainString());
		
		assertEquals(new Account(1L, "Andres", new BigDecimal("900.00")), account);
	}
	
	@Test
	@Order(3)
	void testGet() throws JsonMappingException, JsonProcessingException {
		ResponseEntity<Account[]> response = testRestTemplate.getForEntity("/v1/account", Account[].class);
		
		List<Account> accounts = Arrays.asList(response.getBody());
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
		
		assertEquals(2, accounts.size());
		assertEquals("Andres", accounts.get(0).getName());
		assertEquals(1L, accounts.get(0).getId());
		assertEquals("900.00", accounts.get(0).getBalance().toPlainString());
		assertEquals("John", accounts.get(1).getName());
		assertEquals(2L, accounts.get(1).getId());
		assertEquals("2100.00", accounts.get(1).getBalance().toPlainString());
		
		/*
		 * convert the list in a json for check also with jsonNode
		 */
		JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(accounts));
		assertEquals(1L, jsonNode.get(0).path("id").asLong());
		assertEquals("Andres", jsonNode.get(0).path("name").asText());
		assertEquals("900.0", jsonNode.get(0).path("balance").asText());
		
		assertEquals(2L, jsonNode.get(1).path("id").asLong());
		assertEquals("John", jsonNode.get(1).path("name").asText());
		assertEquals("2100.0", jsonNode.get(1).path("balance").asText());
	}
	
	@Test
	@Order(4)
	void testSave() {
		Account account = new Account(null, "Rob", new BigDecimal("3800"));
		ResponseEntity<Account> response = testRestTemplate.postForEntity("/v1/account", account, Account.class);
		
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
		
		Account responseAccount = response.getBody();
		
		assertEquals("Rob", responseAccount.getName());
		assertEquals("3800", responseAccount.getBalance().toPlainString());
		assertEquals(3L, responseAccount.getId());
	}
}

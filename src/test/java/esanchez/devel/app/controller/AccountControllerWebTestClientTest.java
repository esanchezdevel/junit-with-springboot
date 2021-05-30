package esanchez.devel.app.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import esanchez.devel.app.model.Account;
import esanchez.devel.app.model.dto.TransferDTO;

/*
 * For tests with WebTestClient we need to have our application running, because
 * we will make real integration tests, requesting to the real url of our services.
 */
@TestMethodOrder(OrderAnnotation.class) //for order the execution of the tests
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) //for get a free port
public class AccountControllerWebTestClientTest {

	@Autowired
	private WebTestClient webTestClient;
	
	private ObjectMapper objectMapper;
	
	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
	}
	
	@Test
	@Order(1)
	void testTransfer() throws JsonProcessingException {
		
		TransferDTO dto = new TransferDTO();
		dto.setOriginAccountId(1L);
		dto.setDestinyAccountId(2L);
		dto.setBankId(1L);
		dto.setAmount(new BigDecimal("100"));
		
		Map<String, Object> response = new HashMap<>();
		response.put("date", LocalDate.now().toString());
		response.put("status", "success");
		response.put("message", "transfer done successfully");
		response.put("transaction", dto);
				
		webTestClient.post().uri("/v1/account/transfer")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(dto) //The object is parsed automatically to json
			.exchange()
			//after the exchange() starts the expects for check the results
			.expectStatus().isOk()
			.expectBody()
			//new way to check the response body received
			.consumeWith(resp -> {
				try {
					JsonNode json = objectMapper.readTree(resp.getResponseBody());
					
					assertEquals("transfer done successfully", json.path("message").asText());
					assertEquals(1L, json.path("transaction").path("originAccountId").asLong());
					assertEquals(LocalDate.now().toString(), json.path("date").asText());
					assertEquals("100", json.path("transaction").path("amount").asText());
				} catch (IOException e) {
					e.printStackTrace();
				}
			})
			.jsonPath("$.message").isNotEmpty()
			//different ways to check the value of the json parameter
			.jsonPath("$.message").value(is("transfer done successfully"))
			.jsonPath("$.message").value(value -> {
				assertEquals("transfer done successfully", value);
			})
			.jsonPath("$.message").isEqualTo("transfer done successfully")
			.jsonPath("$.transaction.originAccountId").isEqualTo(1L)
			.jsonPath("$.date").isEqualTo(LocalDate.now().toString())
			.json(objectMapper.writeValueAsString(response));
	}
	
	
	@Test
	@Order(2)
	void testDetail() throws JsonProcessingException {
		
		Account account = new Account(1L, "Andres", new BigDecimal("900"));
		
		
		webTestClient.get().uri("/v1/account/1")
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.name").isEqualTo("Andres")
			.jsonPath("$.balance").isEqualTo(900)
			.json(objectMapper.writeValueAsString(account));
	}
	
	/*
	 * test using consumeWith
	 */
	@Test
	@Order(3)
	void testDetail2() {
		
		webTestClient.get().uri("/v1/account/2")
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody(Account.class)
			.consumeWith(resp -> {
				Account account = resp.getResponseBody();
				
				assertEquals("John", account.getName());
				assertEquals("2100.00", account.getBalance().toPlainString());
			});
	}
	
	@Test
	@Order(4)
	void testGet() {
		
		webTestClient.get().uri("/v1/account").exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody()
			.jsonPath("$[0].name").isEqualTo("Andres")
			.jsonPath("$[0].id").isEqualTo(1)
			.jsonPath("$[0].balance").isEqualTo(900)
			.jsonPath("$[1].name").isEqualTo("John")
			.jsonPath("$[1].id").isEqualTo(2)
			.jsonPath("$[1].balance").isEqualTo(2100)
			.jsonPath("$").isArray()
			.jsonPath("$").value(hasSize(2));
	}
	
	@Test
	@Order(5)
	void testGet2() {
		
		webTestClient.get().uri("/v1/account").exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBodyList(Account.class) //when we will receive a list, the expectBodyList is needed
			.consumeWith(resp -> {
				List<Account> accounts = resp.getResponseBody();
				
				assertEquals(2, accounts.size());
				assertEquals("Andres", accounts.get(0).getName());
				assertEquals(1L, accounts.get(0).getId());
				assertEquals(900, accounts.get(0).getBalance().intValue());
				assertEquals("John", accounts.get(1).getName());
				assertEquals(2L, accounts.get(1).getId());
				assertEquals(2100, accounts.get(1).getBalance().intValue());
			})
			//other methods for check the list size
			.hasSize(2)
			.value(hasSize(2));
	}
}

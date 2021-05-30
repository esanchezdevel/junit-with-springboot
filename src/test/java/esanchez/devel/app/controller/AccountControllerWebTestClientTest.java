package esanchez.devel.app.controller;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import esanchez.devel.app.model.dto.TransferDTO;

/*
 * For tests with WebTestClient we need to have our application running, because
 * we will make real integration tests, requesting to the real url of our services.
 */
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
}

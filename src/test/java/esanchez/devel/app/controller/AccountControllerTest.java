package esanchez.devel.app.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import esanchez.devel.app.data.Data;
import esanchez.devel.app.model.Account;
import esanchez.devel.app.model.dto.TransferDTO;
import esanchez.devel.app.service.AccountService;

/*
 * for test a controller we need to use the annotation @WebMvcTest 
 */
@WebMvcTest(AccountController.class)
public class AccountControllerTest {

	/*
	 * for test a controller we need this MockMvc
	 */
	@Autowired
	private MockMvc mvc;

	@MockBean
	private AccountService accountService;
	
	ObjectMapper objectMapper;
	
	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
	}
	
	@Test
	void testGetAccount() throws Exception {
		when(accountService.findById(1L)).thenReturn(Data.createAccount001());
		
		mvc.perform(get("/v1/account/1").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.name").value("Andres"))
			.andExpect(jsonPath("$.balance").value("1000"));
		
		verify(accountService).findById(1L);
	}
	
	@Test
	void testTransfer() throws Exception {
		TransferDTO transfer = new TransferDTO();
		transfer.setOriginAccountId(1L);
		transfer.setDestinyAccountId(2L);
		transfer.setBankId(1L);
		transfer.setAmount(new BigDecimal("100"));
		
		Map<String, Object> response = new HashMap<>();
		response.put("date", LocalDate.now().toString());
		response.put("status", "success");
		response.put("message", "transfer done successfully");
		response.put("transaction", transfer);
		
		System.out.println(objectMapper.writeValueAsString("Response: " + response));

		
		mvc.perform(post("/v1/account/transfer").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(transfer))) //Request
			.andExpect(status().isOk()) //check the http status
			.andExpect(content().contentType(MediaType.APPLICATION_JSON)) //check the contentType received
			.andExpect(jsonPath("$.date").value(LocalDate.now().toString())) //check that one value of the json is the expected
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.message").value("transfer done successfully"))
			.andExpect(jsonPath("$.transaction.originAccountId").value(transfer.getOriginAccountId()))
			.andExpect(content().json(objectMapper.writeValueAsString(response))); //check that the full json is the one expected
	}
	
	@Test
	void testGet() throws Exception {
		
		//mock list of accounts
		List<Account> accounts = List.of(Data.createAccount001(), Data.createAccount002());
		
		when(accountService.findAll()).thenReturn(accounts);
		
		mvc.perform(get("/v1/account").contentType(MediaType.APPLICATION_JSON))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$[0].name").value("Andres"))
			.andExpect(jsonPath("$[1].name").value("John"))
			.andExpect(jsonPath("$[0].balance").value("1000"))
			.andExpect(jsonPath("$[1].balance").value("2000"))
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(content().json(objectMapper.writeValueAsString(accounts)));
	}
	
	@Test
	void testCreate() throws Exception {
		
		Account account = new Account(null, "Tom", new BigDecimal("1000"));
		
		when(accountService.save(any())).then(invocation -> {
			/*
			 * for return an account with an specific id use this lambda expression
			 * because the account that we have mocked have a null id.
			 */
			Account c = invocation.getArgument(0);
			c.setId(3L);
			return c;
		});
		
		//the request
		mvc.perform(post("/v1/account").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(account)))
			//the result expectations
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id", is(3)))
			.andExpect(jsonPath("$.name", is("Tom")))
			.andExpect(jsonPath("$.balance", is(1000)));
		
		verify(accountService).save(any());
	}
}

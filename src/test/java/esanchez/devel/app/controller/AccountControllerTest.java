package esanchez.devel.app.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import esanchez.devel.app.data.Data;
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
}

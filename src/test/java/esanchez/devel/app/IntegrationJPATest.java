package esanchez.devel.app;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import esanchez.devel.app.model.Account;
import esanchez.devel.app.repository.AccountRepository;

@DataJpaTest //Important this tag for test jpa integration
public class IntegrationJPATest {

	@Autowired
	AccountRepository accountRepository;
	
	@Test
	void testFindById() {
		/*
		 * remember we can test the data because the mock data is inserted with the 
		 * file import.sql in /src/test/resources
		 */
		Optional<Account> account = accountRepository.findById(1L);
		
		assertTrue(account.isPresent());
		assertEquals("Andres", account.orElseThrow().getName());
	}
	
	@Test
	void testFindByName() {
		/*
		 * remember we can test the data because the mock data is inserted with the 
		 * file import.sql in /src/test/resources
		 */
		Optional<Account> account = accountRepository.findByName("Andres");
		
		assertTrue(account.isPresent());
		assertEquals("1000.00", account.orElseThrow().getBalance().toPlainString());
	}
	
	@Test
	void testFindByNameThrowException() {
		/*
		 * remember we can test the data because the mock data is inserted with the 
		 * file import.sql in /src/test/resources
		 */
		Optional<Account> account = accountRepository.findByName("Rob");
		
		assertThrows(NoSuchElementException.class, account::orElseThrow);
		assertFalse(account.isPresent());
	}
	
	@Test
	void testFindAll() {
		/*
		 * remember we can test the data because the mock data is inserted with the 
		 * file import.sql in /src/test/resources
		 */
		List<Account> accounts = accountRepository.findAll();
		
		assertFalse(accounts.isEmpty());
		assertEquals(2, accounts.size());
	}
}

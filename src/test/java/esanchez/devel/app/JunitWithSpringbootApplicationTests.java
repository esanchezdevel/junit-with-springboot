package esanchez.devel.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import esanchez.devel.app.data.Data;
import esanchez.devel.app.exception.InsufficientBalanceException;
import esanchez.devel.app.model.Account;
import esanchez.devel.app.model.Bank;
import esanchez.devel.app.repository.AccountRepository;
import esanchez.devel.app.repository.BankRepository;
import esanchez.devel.app.service.AccountService;
import esanchez.devel.app.service.AccountServiceImpl;

@SpringBootTest
class JunitWithSpringbootApplicationTests {

	/*
	 * Define our mocks and inject them into the service that 
	 * will use them.
	 */
//	@Mock
//	AccountRepository accountRepository;
//	
//	@Mock
//	BankRepository bankRepository;

	@MockBean
	AccountRepository accountRepository;
	
	@MockBean
	BankRepository bankRepository;
	
	/*
	 * for use the @InjectMocks injection annotation we have to use the implementation of
	 * the class instead of the Interface, because is the one that have the constructor with the
	 * parameters that we are mocking
	 */
//	@InjectMocks
//	AccountServiceImpl accountService;

	/*
	 * With spring boot we can inject the mocks using the @Autowired annotation instead of the @InjectMocks
	 * For use this method is needed that the mocks are annotated as @MockBean, and the service class is annotated
	 * with @Service or @Component annotations.
	 * Also in that way we can use the interface instead of the implementation of the class
	 */
	@Autowired
	AccountService accountService;
	
	@BeforeEach
	void setUp() {
	}
	
	@Test
	void contextLoads() {
		/*
		 * "when" one method is executed, "then return" a fixed data
		 */
		when(accountRepository.findById(1L)).thenReturn(Optional.of(Data.createAccount001()));
		when(accountRepository.findById(2L)).thenReturn(Optional.of(Data.createAccount002()));
		
		when(bankRepository.findById(1L)).thenReturn(Optional.of(Data.createBank()));
		
		/*
		 * use the methods of our service code
		 */
		BigDecimal originBalance = accountService.reviewBalance(1L);
		BigDecimal destinyBalance = accountService.reviewBalance(2L);
		
		/*
		 * assert the things that we want to check
		 */
		assertEquals("1000", originBalance.toPlainString());
		assertEquals("2000", destinyBalance.toPlainString());
		
		accountService.transfer(1L, 2L, new BigDecimal("100"), 1L);
		
		originBalance = accountService.reviewBalance(1L);
		destinyBalance = accountService.reviewBalance(2L);
		
		assertEquals("900", originBalance.toPlainString());
		assertEquals("2100", destinyBalance.toPlainString());
		
		int total = accountService.reviewTotalTransfers(1L);
		
		assertEquals(1, total);
		
		/*
		 * verify that the findById is executed 3 times with each id
		 */
		verify(accountRepository, times(3)).findById(1L);
		verify(accountRepository, times(3)).findById(2L);
		
		/*
		 * verify that the update method is executed 2 times
		 */
		verify(accountRepository, times(2)).save(any(Account.class));
		
		/*
		 * verify that the findById is executed 2 times and update methods is executed
		 * only 1 time (times is 1 by default)
		 */
		verify(bankRepository, times(2)).findById(1L);
		verify(bankRepository).save(any(Bank.class));
		
		verify(accountRepository, times(6)).findById(anyLong());
		verify(accountRepository, never()).findAll();
	}
	
	
	@Test
	void contextLoads2() {

		when(accountRepository.findById(1L)).thenReturn(Optional.of(Data.createAccount001()));
		when(accountRepository.findById(2L)).thenReturn(Optional.of(Data.createAccount002()));
		
		when(bankRepository.findById(1L)).thenReturn(Optional.of(Data.createBank()));
		
		BigDecimal originBalance = accountService.reviewBalance(1L);
		BigDecimal destinyBalance = accountService.reviewBalance(2L);
		
		assertEquals("1000", originBalance.toPlainString());
		assertEquals("2000", destinyBalance.toPlainString());
		
		/*
		 * test thst the transfer method throws the expected exception
		 */
		assertThrows(InsufficientBalanceException.class, () -> {
			accountService.transfer(1L, 2L, new BigDecimal("1200"), 1L);	
		});
		
		originBalance = accountService.reviewBalance(1L);
		destinyBalance = accountService.reviewBalance(2L);
		
		assertEquals("1000", originBalance.toPlainString());
		assertEquals("2000", destinyBalance.toPlainString());
		
		int total = accountService.reviewTotalTransfers(1L);
		
		assertEquals(0, total);
		
		verify(accountRepository, times(3)).findById(1L);
		verify(accountRepository, times(2)).findById(2L);
		
		verify(accountRepository, never()).save(any(Account.class));
		
		verify(bankRepository, times(1)).findById(1L);
		verify(bankRepository, never()).save(any(Bank.class));
		
		verify(accountRepository, times(5)).findById(anyLong());
		verify(accountRepository, never()).findAll();
	}
	
	@Test
	void contextLoads3() {
		when(accountRepository.findById(1L)).thenReturn(Optional.of(Data.createAccount001()));
		
		Account account1 = accountService.findById(1L);
		Account account2 = accountService.findById(1L);
		
		/*
		 * 2 ways for check that 2 objects are equals
		 */
		assertSame(account1, account2);
		assertTrue(account1 == account2);
		assertEquals("Andres", account1.getName());
		assertEquals("Andres", account2.getName());
		
		verify(accountRepository, times(2)).findById(1L);
	}
	
	@Test
	void testFindAll() {
		List<Account> data = List.of(Data.createAccount001(), Data.createAccount002());
		when(accountRepository.findAll()).thenReturn(data);
		
		List<Account> accounts = accountService.findAll();
		
		assertFalse(accounts.isEmpty());
		assertEquals(2, accounts.size());
		assertTrue(accounts.contains(Data.createAccount001()));
		
		verify(accountRepository).findAll();
	}
	
	@Test
	void testSave() {
		
		Account data = new Account(null, "Tom", new BigDecimal("1000"));
		
		when(accountService.save(any())).then(invocation -> {
			/*
			 * As the account can have an incremental id, with this 
			 * lambda expression we can set the id returned when save it 
			 */
			Account a = invocation.getArgument(0);
			a.setId(3L);
			return a;
		});
		
		Account account = accountService.save(data);

		assertTrue(account != null);
		assertEquals(3L, account.getId());
		assertEquals("Tom", account.getName());
		assertEquals("1000", account.getBalance().toPlainString());
		
		verify(accountRepository).save(any());
	}
}

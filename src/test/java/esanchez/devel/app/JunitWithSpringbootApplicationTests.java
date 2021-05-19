package esanchez.devel.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import esanchez.devel.app.data.Data;
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
	@Mock
	AccountRepository accountRepository;
	
	@Mock
	BankRepository bankRepository;
	
	AccountService accountService;
	
	@BeforeEach
	void setUp() {
		/*
		 * create the object of the accountServiceImpl using our mocks
		 */
		accountService = new AccountServiceImpl(accountRepository, bankRepository);
	}
	
	@Test
	void contextLoads() {
		/*
		 * "when" one method is exuted, "then return" a fixed data
		 */
		when(accountRepository.findById(1L)).thenReturn(Data.ACCOUNT_001);
		when(accountRepository.findById(2L)).thenReturn(Data.ACCOUNT_002);
		
		when(bankRepository.findById(1L)).thenReturn(Data.BANK);
		
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
		verify(accountRepository, times(2)).update(any(Account.class));
		
		/*
		 * verify that the findById is executed 2 times and update methods is executed
		 * only 1 time (times is 1 by default)
		 */
		verify(bankRepository, times(2)).findById(1L);
		verify(bankRepository).update(any(Bank.class));
	}

}

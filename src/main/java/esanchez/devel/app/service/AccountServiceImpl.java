package esanchez.devel.app.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import esanchez.devel.app.model.Account;
import esanchez.devel.app.model.Bank;
import esanchez.devel.app.repository.AccountRepository;
import esanchez.devel.app.repository.BankRepository;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private BankRepository bankRepository;
	
	/*
	 * the constructor with parameters is needed for inject the Mocks in the junit tests
	 */
	public AccountServiceImpl(AccountRepository accountRepository, BankRepository bankRepository) {
		super();
		this.accountRepository = accountRepository;
		this.bankRepository = bankRepository;
	}

	@Override
	public Account findById(Long id) {
		return accountRepository.findById(id);
	}

	@Override
	public int reviewTotalTransfers(Long bankId) {
		Bank bank = bankRepository.findById(bankId);
		
		return bank.getTotalTransfer();
	}

	@Override
	public BigDecimal reviewBalance(Long accountId) {
		Account account = accountRepository.findById(accountId);
		return account.getBalance();
	}

	@Override
	public void transfer(Long accountNumberOrigin, Long accountNumberDestiny, BigDecimal amount, Long bankId) {
		Account originAccount = accountRepository.findById(accountNumberOrigin);
		originAccount.debit(amount);
		accountRepository.update(originAccount);
		
		Account destinyAccount = accountRepository.findById(accountNumberDestiny);
		destinyAccount.credit(amount);
		accountRepository.update(destinyAccount);
		
		Bank bank = bankRepository.findById(bankId);
		int totalTransfer = bank.getTotalTransfer();
		bank.setTotalTransfer(++totalTransfer);
		bankRepository.update(bank);
	}

}

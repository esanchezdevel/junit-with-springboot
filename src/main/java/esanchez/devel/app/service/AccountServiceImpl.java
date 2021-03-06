package esanchez.devel.app.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import esanchez.devel.app.exception.EntityNotFoundException;
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
	@Transactional(readOnly = true)
	public Account findById(Long id) throws EntityNotFoundException {
		return accountRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("entity not found"));
	}

	@Override
	@Transactional(readOnly = true)
	public int reviewTotalTransfers(Long bankId) throws EntityNotFoundException {
		Bank bank = bankRepository.findById(bankId).orElseThrow(() -> new EntityNotFoundException("entity not found"));
		
		return bank.getTotalTransfer();
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal reviewBalance(Long accountId) throws EntityNotFoundException {
		Account account = accountRepository.findById(accountId).orElseThrow(() -> new EntityNotFoundException("entity not found"));
		return account.getBalance();
	}

	@Override
	@Transactional
	public void transfer(Long accountNumberOrigin, Long accountNumberDestiny, BigDecimal amount, Long bankId) throws EntityNotFoundException {
		Account originAccount = accountRepository.findById(accountNumberOrigin).orElseThrow(() -> new EntityNotFoundException("entity not found"));
		originAccount.debit(amount);
		accountRepository.save(originAccount);
		
		Account destinyAccount = accountRepository.findById(accountNumberDestiny).orElseThrow(() -> new EntityNotFoundException("entity not found"));
		destinyAccount.credit(amount);
		accountRepository.save(destinyAccount);
		
		Bank bank = bankRepository.findById(bankId).orElseThrow(() -> new EntityNotFoundException("entity not found"));
		int totalTransfer = bank.getTotalTransfer();
		bank.setTotalTransfer(++totalTransfer);
		bankRepository.save(bank);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Account> findAll() {
		return accountRepository.findAll();
	}

	@Override
	@Transactional
	public Account save(Account account) {
		return accountRepository.save(account);
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		accountRepository.deleteById(id);
	}

}

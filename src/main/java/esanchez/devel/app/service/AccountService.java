package esanchez.devel.app.service;

import java.math.BigDecimal;

import esanchez.devel.app.model.Account;

public interface AccountService {

	Account findById(Long id);
	
	int reviewTotalTransfers(Long bankId);
	
	BigDecimal reviewBalance(Long accountId);
	
	void transfer(Long accountNumberOrigin, Long accountNumberDestiny, BigDecimal amount);
}

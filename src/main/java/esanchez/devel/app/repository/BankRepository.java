package esanchez.devel.app.repository;

import java.util.List;

import esanchez.devel.app.model.Bank;

public interface BankRepository {

	List<Bank> findAll();
	
	Bank findById(Long id);
	
	void update(Bank bank);
	
	Bank save(Bank bank);
	
	void deleteById(Long id);
}

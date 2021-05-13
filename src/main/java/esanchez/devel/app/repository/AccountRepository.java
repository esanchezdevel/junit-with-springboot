package esanchez.devel.app.repository;

import java.util.List;

import esanchez.devel.app.model.Account;

public interface AccountRepository {

	List<Account> findAll();
	
	Account findById(Long id);
	
	void update(Account account);
	
	Account save(Account account);
	
	void deleteById(Long id);
}

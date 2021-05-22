package esanchez.devel.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import esanchez.devel.app.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{

	Optional<Account> findByName(String name);
	
	@Query(value = "SELECT * FROM accounts WHERE name=?1", nativeQuery = true)
	Optional<Account> findByNameWithQuery(String name);
}

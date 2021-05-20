package esanchez.devel.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import esanchez.devel.app.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{

}

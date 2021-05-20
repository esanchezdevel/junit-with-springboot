package esanchez.devel.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import esanchez.devel.app.model.Bank;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long>{

}

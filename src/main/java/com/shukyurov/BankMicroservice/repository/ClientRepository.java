package com.shukyurov.BankMicroservice.repository;

import com.shukyurov.BankMicroservice.model.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByBankAccountNumber(String accountNumber);

}

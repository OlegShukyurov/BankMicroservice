package com.shukyurov.BankMicroservice.repository;

import com.shukyurov.BankMicroservice.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // TODO

}

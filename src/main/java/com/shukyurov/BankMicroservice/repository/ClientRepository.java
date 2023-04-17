package com.shukyurov.BankMicroservice.repository;

import com.shukyurov.BankMicroservice.model.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}

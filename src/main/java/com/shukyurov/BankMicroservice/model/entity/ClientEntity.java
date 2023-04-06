package com.shukyurov.BankMicroservice.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "client")
public class ClientEntity {

    @Id
    @Column(name = "client_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bank_account_number")
    private int bankAccountNumber;

    @OneToMany(mappedBy = "transactionClient")
    private List<TransactionEntity> clientTransactions;

    @OneToMany(mappedBy = "limitClient")
    private List<LimitEntity> clientLimits;
}

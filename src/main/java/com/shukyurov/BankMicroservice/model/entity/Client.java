package com.shukyurov.BankMicroservice.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int bankAccountNumber;

    @OneToMany(mappedBy = "transactionClient")
    private List<Transaction> clientTransactions;

    @OneToMany(mappedBy = "limitClient")
    private List<Limit> clientLimits;

}

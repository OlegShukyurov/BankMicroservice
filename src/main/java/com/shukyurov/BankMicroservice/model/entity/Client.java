package com.shukyurov.BankMicroservice.model.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Account number should not be empty")
    @NotNull(message = "Account number should not be null")
    @Pattern(regexp = "^[0-9]{10}$",
            message = "Account number should be in this format: '0000000000' for example: '1234567890'")
    private String bankAccountNumber;

    @OneToMany(mappedBy = "transactionClient")
    private List<Transaction> clientTransactions;

    @OneToMany(mappedBy = "limitClient")
    private List<Limit> clientLimits;

}

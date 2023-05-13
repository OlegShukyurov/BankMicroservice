package com.shukyurov.BankMicroservice.repository;

import com.shukyurov.BankMicroservice.model.CurrencyType;
import com.shukyurov.BankMicroservice.model.ExpenseCategoryType;
import com.shukyurov.BankMicroservice.model.entity.Client;
import com.shukyurov.BankMicroservice.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByTransactionClient(Client client);

    @Query(value = "SELECT t FROM Transaction t " +
            "INNER JOIN t.transactionClient " +
            "INNER JOIN t.transactionLimit " +
            "WHERE t.transactionClient = :client AND t.limitExceeded = true " +
            "AND t.currencyShortname = :currency AND t.expenseCategory = :expense")
    List<Transaction> findAllLimitExceededTransactions(Client client, CurrencyType currency, ExpenseCategoryType expense);

    @Query(value = "SELECT t FROM Transaction t " +
            "INNER JOIN t.transactionClient " +
            "INNER JOIN t.transactionLimit " +
            "WHERE t.transactionClient = :client AND t.limitExceeded = true " +
            "AND t.currencyShortname = :currency")
    List<Transaction> findAllLimitExceededTransactions(Client client, CurrencyType currency);

    @Query(value = "SELECT t FROM Transaction t " +
            "INNER JOIN t.transactionClient " +
            "INNER JOIN t.transactionLimit " +
            "WHERE t.transactionClient = :client AND t.limitExceeded = true " +
            "AND t.expenseCategory = :expense")
    List<Transaction> findAllLimitExceededTransactions(Client client, ExpenseCategoryType expense);

    @Query(value = "SELECT t FROM Transaction t " +
            "INNER JOIN t.transactionClient " +
            "INNER JOIN t.transactionLimit " +
            "WHERE t.transactionClient = :client AND t.limitExceeded = true")
    List<Transaction> findAllLimitExceededTransactions(Client client);

}

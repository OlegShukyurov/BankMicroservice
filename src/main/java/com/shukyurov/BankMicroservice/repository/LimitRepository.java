package com.shukyurov.BankMicroservice.repository;

import com.shukyurov.BankMicroservice.model.ExpenseCategoryType;
import com.shukyurov.BankMicroservice.model.entity.Client;
import com.shukyurov.BankMicroservice.model.entity.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LimitRepository extends JpaRepository<Limit, Long> {

    @Query(nativeQuery = true, value =
            "SELECT * FROM limits l " +
            "WHERE l.client_id = ?1 AND l.limit_expense_category = ?2 " +
            "ORDER BY l.limit_date_time DESC FETCH FIRST ROW ONLY")
    Optional<Limit> findLastLimitByClientIdAndExpenseCategory(Long clientId, String expenseCategory);

    List<Limit> findAllByLimitClientAndLimitExpenseCategory(Client client, ExpenseCategoryType expense);

    List<Limit> findAllByLimitClient(Client client);

}

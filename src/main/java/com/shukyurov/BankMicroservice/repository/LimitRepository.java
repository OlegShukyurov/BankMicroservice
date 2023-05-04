package com.shukyurov.BankMicroservice.repository;

import com.shukyurov.BankMicroservice.model.entity.Client;
import com.shukyurov.BankMicroservice.model.entity.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LimitRepository extends JpaRepository<Limit, Long> {

    Optional<Limit> findTopByLimitClientOrderByLimitDateTimeDesc(Client client);

//    @Query(nativeQuery = true, value = "SELECT * FROM limits l WHERE l.client_id = ?1 ORDER BY l.limit_date_time DESC LIMIT 1")
//    Optional<Limit> findLastLimitByClientId(Long clientId);

//    @Query(value = "SELECT l FROM Limit l" +
//            " WHERE l.limitDateTime = (SELECT MAX(l2.limitDateTime) FROM Limit l2 WHERE l2.limitClient = ?1)")
//    Optional<Limit> findLastLimitByClient(Client client);

//    What is better?

}

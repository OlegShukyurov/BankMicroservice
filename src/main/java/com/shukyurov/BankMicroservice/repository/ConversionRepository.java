package com.shukyurov.BankMicroservice.repository;

import com.shukyurov.BankMicroservice.model.ExchangeType;
import com.shukyurov.BankMicroservice.model.entity.Conversion;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversionRepository extends CassandraRepository<Conversion, UUID> {

    Optional<Conversion> findTopBySymbolOrderByMadeAtDesc(ExchangeType symbol);

}

package com.shukyurov.BankMicroservice.repository;

import com.shukyurov.BankMicroservice.model.entity.ConversionEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversionRepository extends CassandraRepository<ConversionEntity, Long> {
}

package com.banking.fraud.repository;

import com.banking.fraud.entity.FraudLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FraudLogRepository extends MongoRepository<FraudLog, String> {

    List<FraudLog> findByTransactionId(Long transactionId);

    List<FraudLog> findByFromAccount(String fromAccount);

    List<FraudLog> findByFlagged(boolean flagged);

    List<FraudLog> findByRiskLevel(String riskLevel);

    List<FraudLog> findByFromAccountAndTimestampBetween(String fromAccount,
            LocalDateTime start,
            LocalDateTime end);

    long countByFromAccountAndTimestampAfter(String fromAccount, LocalDateTime timestamp);
}

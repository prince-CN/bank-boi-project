package com.banking.transaction.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topics.transaction-initiated}")
    private String transactionInitiated;

    @Value("${kafka.topics.transaction-success}")
    private String transactionSuccess;

    @Value("${kafka.topics.transaction-failed}")
    private String transactionFailed;

    @Bean
    public NewTopic transactionInitiatedTopic() {
        return TopicBuilder.name(transactionInitiated)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic transactionSuccessTopic() {
        return TopicBuilder.name(transactionSuccess)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic transactionFailedTopic() {
        return TopicBuilder.name(transactionFailed)
                .partitions(3)
                .replicas(1)
                .build();
    }
}

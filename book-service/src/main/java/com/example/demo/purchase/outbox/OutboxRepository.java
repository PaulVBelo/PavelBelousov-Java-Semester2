package com.example.demo.purchase.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("outboxRepository")
public interface OutboxRepository extends JpaRepository<OutboxRecord, Long> {
}

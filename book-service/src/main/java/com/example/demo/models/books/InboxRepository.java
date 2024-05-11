package com.example.demo.models.books;

import com.example.demo.models.books.InboxRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InboxRepository extends JpaRepository<InboxRecord, Long> {
}

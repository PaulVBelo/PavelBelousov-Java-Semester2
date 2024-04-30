package com.example.demo.models.authors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("authorRepository")
public interface AuthorRepository extends JpaRepository<Author, Long> {
}

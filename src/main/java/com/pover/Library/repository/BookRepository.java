package com.pover.Library.repository;

import com.pover.Library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitle(String title);

    List<Book> findByAvailable(boolean available);

    @Query("SELECT b FROM Book b " +
            "JOIN b.author a " +
            "LEFT JOIN b.genres g " +
            "WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(a.first_name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(a.last_name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(g.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "CAST(b.publication_year AS string) LIKE CONCAT('%', :query, '%')")
    List<Book> searchBooks(@Param("query") String query);
}


package com.pover.Library;

import com.pover.Library.model.Author;
import com.pover.Library.model.Book;
import com.pover.Library.repository.AuthorRepository;
import com.pover.Library.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    public void setup() {

        // Insert a new book with a known title and an associated author
        Author author = new Author();
        author.setFirst_name("George");
        author.setLast_name("Orwell");
        author.setBirth_date(LocalDate.of(1994, 05, 31));
        authorRepository.save(author);

        Book book = new Book();
        book.setTitle("1984");
        book.setPublication_year(1949);
        book.setAuthor(author);
        book.setAvailable(true);
        bookRepository.save(book);
    }

    @Test
    public void testGetBookByTitle() throws Exception {

        mockMvc.perform(get("/book/get/{title}", "1984"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("1984"));
    }
}

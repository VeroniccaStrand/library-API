package com.pover.Library.controller;

import com.pover.Library.dto.BookRequestDto;
import com.pover.Library.dto.BookResponseDto;
import com.pover.Library.model.Book;
import com.pover.Library.repository.BookRepository;
import com.pover.Library.service.BookService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/book")
public class BookController {
    private final BookService bookService;
    private final BookRepository bookRepository;

    public BookController(BookService bookService, BookRepository bookRepository) {
        this.bookService = bookService;
        this.bookRepository = bookRepository;
    }

    @GetMapping("public/get")
    public ResponseEntity<List<BookResponseDto>> getBooks() {
        List<BookResponseDto> books = bookService.getBooks();
        if (books.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(books, HttpStatus.OK);
        }
    }

    @GetMapping("public/get/{title}")
    public ResponseEntity<List<BookResponseDto>> getBooksByTitle(@PathVariable String title) {
        List<BookResponseDto> books = bookService.getBooksByTitle(title);
        if (books.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(books, HttpStatus.OK);
        }
    }

    @GetMapping("public/get/search")
    public List<BookResponseDto> searchBooks(@RequestParam("query") String query) {
        return bookService.searchBooks(query);
    }

    @PostMapping("/admin/add")
    public ResponseEntity<BookResponseDto> addBook(@Valid @RequestBody BookRequestDto bookRequestDto) {
        BookResponseDto bookResponseDto = bookService.addBook(bookRequestDto);
        return new ResponseEntity<>(bookResponseDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();

            if (!book.isAvailable()) {
                return new ResponseEntity<>("Cannot delete a book that is currently loaned out", HttpStatus.BAD_REQUEST);
            }
            bookRepository.deleteById(id);
            return new ResponseEntity<>("Book deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Book not found", HttpStatus.NOT_FOUND);
        }
    }
}


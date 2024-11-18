package com.pover.Library.service;

import com.pover.Library.dto.BookRequestDto;
import com.pover.Library.dto.BookResponseDto;
import com.pover.Library.model.Author;
import com.pover.Library.model.Book;
import com.pover.Library.model.Genre;
import com.pover.Library.repository.BookRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final GenreService genreService;

    public BookService(BookRepository bookRepository, AuthorService authorService, GenreService genreService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.genreService = genreService;
    }

    public BookResponseDto convertToBookResponseDto(Book book) {

        String authorFirstName = book.getAuthor().getFirst_name();
        String authorLastName = book.getAuthor().getLast_name();

        String genreName = book.getGenres()
                .stream()
                .map(Genre::getName)
                .collect(Collectors.joining(", "));

        return new BookResponseDto(
                book.getBook_id(),
                book.getTitle(),
                book.getPublication_year(),
                book.isAvailable(),
                genreName,
                authorFirstName,
                authorLastName
        );
    }

    public BookResponseDto addBook(@Valid BookRequestDto bookRequestDto) {

        Author author = authorService.findById(bookRequestDto.getAuthor_id())
                .orElseThrow(() -> new IllegalArgumentException("Author not found"));

        Set<Genre> genres = bookRequestDto.getGenre_id().stream()
                .map(genreService::findById)
                .map(Optional::get)
                .collect(Collectors.toSet());

        Book book = new Book();
        book.setTitle(bookRequestDto.getTitle());
        book.setPublication_year(bookRequestDto.getPublication_year());
        book.setAuthor(author);
        book.setGenres(genres);
        book.setAvailable(bookRequestDto.isAvailable());

        Book savedBook = bookRepository.save(book);

        return convertToBookResponseDto(savedBook);
    }

    public List<BookResponseDto> getBooks() {

        List<Book> books = bookRepository.findAll();

        return books.stream()
                .map(this::convertToBookResponseDto)
                .collect(Collectors.toList());
    }

    public List<BookResponseDto> getBooksByTitle(String title) {

        List<Book> books = bookRepository.findByTitle(title);
        if (books == null || books.isEmpty()) {
            System.out.println("No books found with title: " + title);  // Add logging for troubleshooting
        }

        return books.stream()
                .map(this::convertToBookResponseDto)
                .collect(Collectors.toList());
    }
}

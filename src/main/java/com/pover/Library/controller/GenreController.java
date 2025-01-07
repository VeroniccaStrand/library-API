package com.pover.Library.controller;

import com.pover.Library.model.Genre;
import com.pover.Library.service.GenreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreController {
    private final GenreService genreService;
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public ResponseEntity<List<Genre>> findAll() {
        List<Genre> genres = genreService.findAllGenres();
        return ResponseEntity.ok(genres);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Genre> findById(@PathVariable Long id) {
        Genre genre = genreService.findGenreById(id);
        return ResponseEntity.ok(genre);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    @PostMapping
    public ResponseEntity<Genre> save(@RequestBody Genre genre) {
        Genre createdGenre = genreService.create(genre);
        return ResponseEntity.ok(createdGenre);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

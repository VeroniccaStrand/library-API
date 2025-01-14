package com.pover.Library.service;

import com.pover.Library.model.Genre;
import com.pover.Library.repository.GenreRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GenreService {
    private final GenreRepository genreRepository;
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public Optional<Genre> findById(long id) {
        return genreRepository.findById(id);
    }

    public List<Genre> findAllGenres(){
        return genreRepository.findAll();
    }

    public Genre findGenreById(Long id){
        return genreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Genre not found"));
    }
    public Genre create(Genre genre){
        return genreRepository.save(genre);
    }

    public Genre updateGenre(Long id, Genre genreDetails){
        Genre genre = findGenreById(id);
        genre.setName(genreDetails.getName());
        return genreRepository.save(genre);
    }


    public void deleteGenre(Long id){
        Genre genre = findGenreById(id);
        genreRepository.delete(genre);
    }
}

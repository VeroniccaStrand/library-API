package com.pover.Library.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;


@Entity
@Getter
@Setter
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long book_id;

    @NotNull(message = "Title is required")
    private String title;

    private int publication_year;

    @ManyToOne
    //(cascade = CascadeType.PERSIST)
    //ev. Cascade
    @JoinColumn(name = "author_id")
    private Author author;

    @NotNull
    private boolean available = true;

    @ManyToMany
    @JoinTable(name = "books_genres",
    joinColumns = @JoinColumn(name = "book_id"),
    inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private Set<Genre> genres;


}

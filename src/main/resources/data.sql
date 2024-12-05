-- Create tables if they don't exist
CREATE TABLE IF NOT EXISTS authors (
                                       author_id BIGSERIAL PRIMARY KEY,
                                       first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    birth_date DATE
    );

CREATE TABLE IF NOT EXISTS books (
                                     book_id BIGSERIAL PRIMARY KEY,
                                     title VARCHAR(255) NOT NULL,
    publication_year INT,
    author_id BIGINT,
    available BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (author_id) REFERENCES authors(author_id)
    );

CREATE TABLE IF NOT EXISTS genres (
                                      genre_id BIGSERIAL PRIMARY KEY,
                                      name VARCHAR(50) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS books_genres (
                                            book_id BIGINT,
                                            genre_id BIGINT,
                                            PRIMARY KEY (book_id, genre_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id),
    FOREIGN KEY (genre_id) REFERENCES genres(genre_id)
    );

CREATE TABLE IF NOT EXISTS users (
                                     user_id BIGSERIAL PRIMARY KEY,
                                     first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    member_number VARCHAR(10) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS loans (
                                     loan_id BIGSERIAL PRIMARY KEY,
                                     book_id BIGINT,
                                     user_id BIGINT,
                                     loan_date DATE NOT NULL,
                                     due_date DATE NOT NULL,
                                     returned_date DATE,
                                     FOREIGN KEY (book_id) REFERENCES books(book_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

CREATE TABLE IF NOT EXISTS admins (
                                      admin_id BIGSERIAL PRIMARY KEY,
                                      username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL
    );

-- Clear existing data
DELETE FROM books_genres;
DELETE FROM genres;
DELETE FROM loans;
DELETE FROM books;
DELETE FROM authors;
DELETE FROM users;
DELETE FROM admins;

-- Reset auto-increment counters
ALTER SEQUENCE loans_loan_id_seq RESTART WITH 1;
ALTER SEQUENCE books_book_id_seq RESTART WITH 1;
ALTER SEQUENCE authors_author_id_seq RESTART WITH 1;
ALTER SEQUENCE users_user_id_seq RESTART WITH 1;
ALTER SEQUENCE admins_admin_id_seq RESTART WITH 1;
ALTER SEQUENCE genres_genre_id_seq RESTART WITH 1;

-- Populate authors
INSERT INTO authors (first_name, last_name, birth_date) VALUES
                                                            ('Astrid', 'Lindgren', '1907-11-14'),
                                                            ('J.K.', 'Rowling', '1965-07-31'),
                                                            ('Stephen', 'King', '1947-09-21'),
                                                            ('Stieg', 'Larsson', '1954-08-15'),
                                                            ('Virginia', 'Woolf', '1882-01-25');

-- Populate books
INSERT INTO books (title, publication_year, author_id, available) VALUES
                                                                      ('Pippi Långstrump', 1945, 1, TRUE),
                                                                      ('Bröderna Lejonhjärta', 1973, 1, FALSE),
                                                                      ('Harry Potter and the Philosopher''s Stone', 1997, 2, TRUE),
                                                                      ('Harry Potter and the Chamber of Secrets', 1998, 2, TRUE),
                                                                      ('Harry Potter and the Prisoner of Azkaban', 1999, 2, FALSE),
                                                                      ('The Shining', 1977, 3, TRUE),
                                                                      ('Pet Sematary', 1983, 3, TRUE),
                                                                      ('Män som hatar kvinnor', 2005, 4, TRUE),
                                                                      ('Flickan som lekte med elden', 2006, 4, FALSE),
                                                                      ('Mrs. Dalloway', 1925, 5, TRUE);

-- Populate genres
INSERT INTO genres (name) VALUES
                              ('Fantasy'),
                              ('Horror'),
                              ('Crime'),
                              ('Classic'),
                              ('Children');

-- Connect books with genres (many-to-many)
INSERT INTO books_genres (book_id, genre_id) VALUES
                                                 (1, 5), -- Pippi: Children
                                                 (1, 1), -- Pippi: Fantasy
                                                 (2, 5), -- Bröderna Lejonhjärta: Children
                                                 (2, 1), -- Bröderna Lejonhjärta: Fantasy
                                                 (3, 1), -- Harry Potter 1: Fantasy
                                                 (4, 1), -- Harry Potter 2: Fantasy
                                                 (5, 1), -- Harry Potter 3: Fantasy
                                                 (6, 2), -- The Shining: Horror
                                                 (7, 2), -- Pet Sematary: Horror
                                                 (8, 3), -- Män som hatar kvinnor: Crime
                                                 (9, 3), -- Flickan som lekte med elden: Crime
                                                 (10, 4); -- Mrs. Dalloway: Classic

-- Populate users
INSERT INTO users (first_name, last_name, email, member_number, password, role) VALUES
                                                                    ('Anna', 'Andersson', 'anna.andersson@email.com', 'M20230001', '1234', 'USER'),
                                                                    ('Erik', 'Eriksson', 'erik.eriksson@email.com', 'M20230002', '1234', 'USER'),
                                                                    ('Maria', 'Svensson', 'maria.svensson@email.com', 'M20230003', '1234', 'USER'),
                                                                    ('Johan', 'Johansson', 'johan.johansson@email.com', 'M20230004', '1234', 'USER'),
                                                                    ('Eva', 'Larsson', 'eva.larsson@email.com', 'M20230005', '1234', 'USER'),
                                                                    ('user1', 'superuser', 'superuser@user.com', '200203040345', '$2a$10$nHC0aQP7cGKP2O5aWIrMKOVe1cdST2RG9BxaE1GLk44jQn3fy0Mka', 'USER'),
                                                                    ('pew', 'meow', 'pew@gmail.com', '199302053445', '$2a$10$mHjhvQIrukV.KD5ejRyyiOorCsGoRLIcRsxO5cpr9sFF3TVO.DQNC', 'USER');

-- Populate loans
INSERT INTO loans (book_id, user_id, loan_date, due_date, returned_date) VALUES
                                                                             (2, 1, '2024-01-15', '2024-02-15', NULL),
                                                                             (5, 2, '2024-01-20', '2024-02-20', NULL),
                                                                             (9, 3, '2024-01-25', '2024-02-25', NULL),
                                                                             (1, 4, '2023-12-15', '2024-01-15', '2024-01-14'),
                                                                             (3, 6, '2023-12-20', '2024-01-20', '2024-01-18'),
                                                                             (6, 1, '2023-12-25', '2024-01-25', '2024-01-23');
-- Populate admins
INSERT INTO admins (username, password, role) VALUES
                                                  ('admin', 'admin123', 'ADMIN'),
                                                  ('lisa', 'lisa123', 'LIBRARIAN'),
                                                  ('lars', 'lars123', 'LIBRARIAN');

package com.pover.Library.repository;

import com.pover.Library.model.Admin;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    boolean existsByUsername( String username);


    Admin findByUsername(@NotNull(message = "Username is required and unique") String username);
}

package com.pover.Library.repository;

import com.pover.Library.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByMemberNumber(String memberNumber);

    Optional<User> findByMemberNumber(String memberNumber);
    Optional<User> findByPersonalNumber(String personalNumber);

}

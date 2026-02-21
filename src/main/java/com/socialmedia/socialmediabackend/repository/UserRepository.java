package com.socialmedia.socialmediabackend.repository;

import com.socialmedia.socialmediabackend.User;
import com.socialmedia.socialmediabackend.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByVerificationToken(String verificationToken);

    @Query("SELECT u FROM User u WHERE u.verificationStatus = :status")
    Page<User> findByVerificationStatus(@Param("status") VerificationStatus status, Pageable pageable);

    @Query("SELECT u FROM User u WHERE lower(u.email) LIKE lower(concat('%', :keyword, '%'))")
    Page<User> searchByEmail(@Param("keyword") String keyword, Pageable pageable);
}

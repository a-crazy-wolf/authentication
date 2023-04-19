package com.learning.authentication.repository;

import com.learning.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDetailRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmailId(String emailId);

    @Modifying
    @Query("UPDATE User SET failedAttempt= ?1 WHERE emailId = ?2")
    void updateFailedAttempt(int failedAttempt, String emailId);
}

package com.jiwon.mylog.domain.user.repository;

import com.jiwon.mylog.domain.user.entity.User;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsByAccountId(String accountId);

    @Query("select u from User u where u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("select u from User u where u.accountId = :accountId")
    Optional<User> findByAccountId(@Param("accountId") String accountId);

    @Query(value = "select u from User u left join fetch u.profileImage where u.id = :userId")
    Optional<User> findUserWithProfileImage(@Param("userId") Long userId);

    Page<User> findByUsernameContaining(String username, Pageable pageable);
}

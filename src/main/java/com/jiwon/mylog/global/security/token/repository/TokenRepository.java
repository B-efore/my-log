package com.jiwon.mylog.global.security.token.repository;

import java.util.Optional;

import com.jiwon.mylog.global.security.token.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<RefreshToken, Long> {
    @Query("select rt from RefreshToken rt where rt.user.id = :userId")
    Optional<RefreshToken> findByUserId(Long userId);
}

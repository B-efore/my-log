package com.jiwon.mylog.domain.user.repository;

import com.jiwon.mylog.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    @Query(value = "select u from User u left join fetch u.userRoles where u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query(value = "select u from User u left join fetch u.userRoles where u.provider = :provider and u.providerId = :providerId")
    Optional<User> findByProviderAndProviderId(@Param("provider") String provider, @Param("providerId") String providerId);
}

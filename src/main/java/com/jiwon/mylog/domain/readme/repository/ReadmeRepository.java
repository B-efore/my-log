package com.jiwon.mylog.domain.readme.repository;

import com.jiwon.mylog.domain.readme.entity.Readme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReadmeRepository extends JpaRepository<Readme, Long> {
    Optional<Readme> findByUserId(Long userId);
}

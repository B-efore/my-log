package com.jiwon.mylog.repository;

import com.jiwon.mylog.entity.category.Category;
import com.jiwon.mylog.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByUserAndName(User user, String name);
    Optional<Category> findByUserAndId(User user, Long id);
}

package com.jiwon.mylog.domain.item.repository;

import com.jiwon.mylog.domain.item.entity.UserItemGrowth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserItemGrowthRepository extends JpaRepository<UserItemGrowth, Long> {
}

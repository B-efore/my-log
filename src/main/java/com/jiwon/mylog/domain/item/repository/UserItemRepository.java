package com.jiwon.mylog.domain.item.repository;

import com.jiwon.mylog.domain.item.entity.UserItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserItemRepository extends JpaRepository<UserItem, Long> {
}

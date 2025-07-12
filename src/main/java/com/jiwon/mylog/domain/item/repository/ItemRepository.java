package com.jiwon.mylog.domain.item.repository;

import com.jiwon.mylog.domain.item.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i order by i.id desc")
    Page<Item> findAll(Pageable pageable);
}

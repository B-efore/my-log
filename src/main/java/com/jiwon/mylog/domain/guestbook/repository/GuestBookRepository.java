package com.jiwon.mylog.domain.guestbook.repository;

import com.jiwon.mylog.domain.guestbook.entity.GuestBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestBookRepository extends JpaRepository<GuestBook, Long>, GuestBookCustom {

}

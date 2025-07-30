package com.jiwon.mylog.domain.guestbook.repository;

import com.jiwon.mylog.domain.guestbook.entity.GuestBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestBookRepository extends JpaRepository<GuestBook, Long>, GuestBookCustom {

    /***
     *     @Query("select new com.jiwon.mylog.domain.post.dto.response.PinnedPostResponse(p.id, p.title, p.contentPreview) " +
     *             "from Post p " +
     *             "where p.user.id = :userId and p.pinned = true and p.deletedAt is null")
     * @param receiverId
     * @return
     */

}

package com.jiwon.mylog.domain.notification.repository;

import com.jiwon.mylog.domain.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Modifying(clearAutomatically = true)
    @Query("update Notification n set n.isRead = true where n.receiver.id = :receiverId and n.isRead = false")
    void updateReadStateByReceiverId(@Param("receiverId") Long receiverId);

    @Query("select count(n.id) from Notification n where n.receiver.id = :receiverId and n.isRead = false")
    long countByReceiverIdAndReadIsFalse(@Param("receiverId") Long receiverId);

    Page<Notification> findAllByReceiverId(Long receiverId, Pageable pageable);
}

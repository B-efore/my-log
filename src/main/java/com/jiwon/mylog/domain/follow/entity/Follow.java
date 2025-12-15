package com.jiwon.mylog.domain.follow.entity;

import com.jiwon.mylog.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_follow_from_to",
                        columnNames = {"from_user_id", "to_user_id"}
                )
        }
)
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User toUser;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static Follow follow(User fromUser, User toUser) {
        return Follow.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .build();
    }
}

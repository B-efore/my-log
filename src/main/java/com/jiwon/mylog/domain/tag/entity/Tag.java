package com.jiwon.mylog.domain.tag.entity;

import com.jiwon.mylog.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(
        name = "tag",
        uniqueConstraints = @UniqueConstraint(
                name = "tag_uk",
                columnNames = {"user_id", "name"}
        )
)
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long usageCount = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void incrementUsage() {
        this.usageCount++;
    }

    public void decrementUsage() {
        this.usageCount = Math.max(0, this.usageCount - 1);
    }

    public static Tag create(User user, String name) {
        return Tag.builder()
                .user(user)
                .name(name)
                .usageCount(0L)
                .build();
    }
}

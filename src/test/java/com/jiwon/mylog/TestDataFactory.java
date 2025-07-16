package com.jiwon.mylog;

import com.jiwon.mylog.domain.category.entity.Category;
import com.jiwon.mylog.domain.image.entity.ProfileImage;
import com.jiwon.mylog.domain.item.entity.Item;
import com.jiwon.mylog.domain.notification.entity.Notification;
import com.jiwon.mylog.domain.notification.entity.NotificationType;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.entity.UserStatus;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class TestDataFactory {
    public static User createUser(String email, String accountId, String username) {
        return User.builder()
                .email(email)
                .accountId(accountId)
                .username(username)
                .status(UserStatus.ACTIVE)
                .build();
    }

    public static ProfileImage createImage(User user, String fileKey) {
        ProfileImage profileImage = ProfileImage.forUserProfile(user);
        profileImage.updateProfile(fileKey);
        return profileImage;
    }

    public static Category createCategory(User user, String name){
        return Category.builder()
                .user(user)
                .name(name)
                .postCount(0L)
                .build();
    }

    public static Post createPost(String title, String content, User user, Category category) {
        return Post.builder()
                .title(title)
                .content(content)
                .user(user)
                .category(category)
                .build();
    }

    public static Item createItem() {
        return Item.builder()
                .name("name")
                .description("description")
                .price(0)
                .build();
    }

    public static Notification createNotification(User receiver, boolean isRead) {
        return Notification.builder()
                .receiver(receiver)
                .content("테스트")
                .url("/test")
                .isRead(isRead)
                .type(NotificationType.SERVER)
                .build();
    }
}

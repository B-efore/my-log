package com.jiwon.mylog.domain.image.entity;

import com.jiwon.mylog.domain.user.entity.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
@DiscriminatorValue("PROFILE")
public class ProfileImage extends Image {

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void updateProfile(String key) {
        super.update(key);
    }

    public static ProfileImage forUserProfile(User user) {
        ProfileImage profileImage = new ProfileImage();
        profileImage.user = user;
        profileImage.user.updateProfileImage(profileImage);
        return profileImage;
    }
}

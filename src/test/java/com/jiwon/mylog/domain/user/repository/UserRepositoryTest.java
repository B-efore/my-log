package com.jiwon.mylog.domain.user.repository;

import com.jiwon.mylog.domain.image.repository.ProfileImageRepository;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.global.common.config.JpaAuditingConfiguration;
import com.jiwon.mylog.global.common.config.QueryDSLConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static com.jiwon.mylog.TestDataFactory.createImage;
import static com.jiwon.mylog.TestDataFactory.createUser;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QueryDSLConfig.class, JpaAuditingConfiguration.class})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileImageRepository profileImageRepository;

    @Test
    @DisplayName("사용자 조회 시 사용자와 프로필 이미지를 함께 조회한다.")
    void findUserWithProfileImage() {
        // given
        User user = createUser("test1@test.com", "testId1", "test1");
        createImage(user, "profileImage");
        User savedUser = userRepository.save(user);

        // when
        Optional<User> result = userRepository.findUserWithProfileImage(savedUser.getId());

        // then
        assertThat(result).isPresent();

        User findUser = result.get();
        assertThat(findUser.getProfileImage()).isNotNull();
        assertThat(findUser.getProfileImage().getFileKey()).isEqualTo("profileImage");
    }

    @Test
    @DisplayName("프로필 이미지가 없는 사용자 조회 시 프로필 이미지는 null을 반환한다.")
    void findUserWithProfileImage_ProfileNotFound() {
        // given
        User user = createUser("test1@test.com", "testId1", "test1");
        User savedUser = userRepository.save(user);

        // when
        Optional<User> result = userRepository.findUserWithProfileImage(savedUser.getId());

        // then
        assertThat(result).isPresent();

        User findUser = result.get();
        assertThat(findUser.getProfileImage()).isNull();
    }

    @Test
    @DisplayName("사용자 삭제 시 연관된 프로필 이미지도 함께 삭제한다.")
    void deleteUserCascadesProfileImage() {
        // given
        User user = createUser("test1@test.com", "testId1", "test1");
        createImage(user, "profileImage");
        User savedUser = userRepository.save(user);

        // when
        userRepository.delete(savedUser);

        // then
        assertThat(userRepository.findById(savedUser.getId())).isEmpty();
        assertThat(profileImageRepository.findByUserId(savedUser.getId())).isEmpty();
    }
}
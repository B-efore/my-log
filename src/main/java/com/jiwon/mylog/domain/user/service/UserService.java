package com.jiwon.mylog.domain.user.service;

import com.jiwon.mylog.domain.item.entity.Item;
import com.jiwon.mylog.domain.item.entity.UserItem;
import com.jiwon.mylog.domain.item.repository.ItemRepository;
import com.jiwon.mylog.domain.item.repository.UserItemRepository;
import com.jiwon.mylog.domain.point.service.PointService;
import com.jiwon.mylog.global.common.entity.PageResponse;
import com.jiwon.mylog.domain.user.dto.request.UserProfileRequest;
import com.jiwon.mylog.domain.user.dto.response.UserResponse;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.DuplicateException;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final UserItemRepository userItemRepository;
    private final PointService pointService;

    @Transactional(readOnly = true)
    public UserResponse getUserProfile(Long userId) {
        User user = userRepository.findUserWithProfileImage(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
        return UserResponse.fromUser(user);
    }

    @CacheEvict(value = "blog::home", key = "#userId", condition = "#userId != null")
    @Transactional
    public UserResponse updateUserProfile(Long userId, UserProfileRequest userProfileRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
        user.updateProfile(
                userProfileRequest.getUsername(),
                userProfileRequest.getBio()
        );
        return UserResponse.fromUser(user);
    }

    @Transactional(readOnly = true)
    public PageResponse searchWithUsername(String username, Pageable pageable) {
        Page<User> userPage = userRepository.findByUsernameContaining(username, pageable);
        List<UserResponse> users = userPage.stream()
                .map(UserResponse::fromUser)
                .toList();

        return PageResponse.from(
                users,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalPages(),
                (int) userPage.getTotalElements());
    }

    @Transactional
    public void purchaseItem(Long userId, Long itemId) {

        if (userItemRepository.existsByUserIdAndItemId(userId, itemId)) {
            throw new DuplicateException(ErrorCode.DUPLICATE);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));

        pointService.spendPoint(userId, item.getPrice(), item.getName() + "구매");

        UserItem userItem = UserItem.create(user, item, 1);
        userItemRepository.save(userItem);
    }
}

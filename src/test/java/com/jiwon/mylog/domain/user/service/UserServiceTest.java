package com.jiwon.mylog.domain.user.service;

import com.jiwon.mylog.TestDataFactory;
import com.jiwon.mylog.domain.item.entity.Item;
import com.jiwon.mylog.domain.item.entity.UserItem;
import com.jiwon.mylog.domain.item.repository.ItemRepository;
import com.jiwon.mylog.domain.item.repository.UserItemRepository;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserItemRepository userItemRepository;

    @Mock
    ItemRepository itemRepository;

    @DisplayName("유저에 아이템이 추가된다.")
    @Test
    void purchaseItem() {
        // given
        Long userId = 1L;
        Long itemId = 1L;

        User user = TestDataFactory.createUser("email", "accountId", "name");
        Item item = TestDataFactory.createItem();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(itemRepository.findById(itemId)).willReturn(Optional.of(item));

        // when
        userService.purchaseItem(userId, itemId);

        // then
        verify(userItemRepository).save(any(UserItem.class));
        Assertions.assertThat(user.getItems().size()).isEqualTo(1);
    }
}
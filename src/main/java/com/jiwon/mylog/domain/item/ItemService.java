package com.jiwon.mylog.domain.item;

import com.jiwon.mylog.domain.post.dto.response.PageResponse;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void createItem(ItemRequest request) {
        Item item = Item.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();
        itemRepository.save(item);
    }

    @Transactional
    public ItemResponse updateItem(Long itemId, ItemRequest request) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));
        item.update(request.getName(), request.getDescription(), request.getPrice());
        return ItemResponse.fromItem(item);
    }

    @Transactional
    public void deleteItem(Long itemId) {
        if (itemId == null || !itemRepository.existsById(itemId)) {
            throw new NotFoundException(ErrorCode.NOT_FOUND);
        }
        itemRepository.deleteById(itemId);
    }

    @Transactional(readOnly = true)
    public PageResponse getAllItems(Pageable pageable) {
        Page<Item> itemPage = itemRepository.findAll(pageable);
        List<ItemResponse> items = itemPage.stream()
                .map(ItemResponse::fromItem)
                .toList();

        return PageResponse.from(
                items,
                itemPage.getNumber(),
                itemPage.getSize(),
                itemPage.getTotalPages(),
                itemPage.getTotalElements());
    }
}

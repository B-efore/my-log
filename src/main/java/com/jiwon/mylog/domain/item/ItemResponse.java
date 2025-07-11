package com.jiwon.mylog.domain.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ItemResponse {
    private Long itemId;
    private String name;
    private String description;
    private int price;

    public static ItemResponse fromItem(Item item) {
        return ItemResponse.builder()
                .itemId(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .build();
    }
}

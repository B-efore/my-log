package com.jiwon.mylog.domain.item.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private int price;
}

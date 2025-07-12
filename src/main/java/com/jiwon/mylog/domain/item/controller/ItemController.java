package com.jiwon.mylog.domain.item.controller;

import com.jiwon.mylog.domain.item.dto.ItemRequest;
import com.jiwon.mylog.domain.item.dto.ItemResponse;
import com.jiwon.mylog.domain.item.service.ItemService;
import com.jiwon.mylog.domain.post.dto.response.PageResponse;
import com.jiwon.mylog.global.security.auth.annotation.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items")
    public ResponseEntity<PageResponse> getAllItems(
            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        PageResponse response = itemService.getAllItems(pageable);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/items")
    public ResponseEntity<?> createItem(@LoginUser Long userId, @Valid @RequestBody ItemRequest request) {
        itemService.createItem(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/items/{itemId}")
    public ResponseEntity<ItemResponse> updateItem(
            @LoginUser Long userId,
            @PathVariable("itemId") Long itemId,
            @Valid @RequestBody ItemRequest request) {
        ItemResponse response = itemService.updateItem(itemId, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/items/{itemId}")
    public ResponseEntity<Void> deleteItem(@LoginUser Long userId, @PathVariable("itemId") Long itemId) {
        itemService.deleteItem(itemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

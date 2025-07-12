package com.jiwon.mylog.domain.tag.controller;

import com.jiwon.mylog.global.common.entity.PageResponse;
import com.jiwon.mylog.domain.tag.dto.response.TagResponse;
import com.jiwon.mylog.domain.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class TagController {

    private final TagService tagService;

    @GetMapping("/users/{userId}/tags")
    public ResponseEntity<?> getAllTags(@PathVariable("userId") Long userId) {
        List<TagResponse> response = tagService.getAllTags(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/tags/with-counts")
    public ResponseEntity<PageResponse> getAllTagsWithCount(
            @PathVariable("userId") Long userId,
            @PageableDefault(size = 10, page = 0,
                    sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse response = tagService.getAllTagsWithCount(userId, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

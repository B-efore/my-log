package com.jiwon.mylog.domain.tag.controller;

import com.jiwon.mylog.domain.tag.dto.response.TagCountListResponse;
import com.jiwon.mylog.domain.tag.dto.response.TagResponse;
import com.jiwon.mylog.domain.tag.service.TagService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<?> getAllTagsWithCount(@PathVariable("userId") Long userId) {
        TagCountListResponse response = tagService.getAllTagsWithCount(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

package com.kirisamemarisa.blog.controller;

import com.kirisamemarisa.blog.common.ApiResponse;
import com.kirisamemarisa.blog.dto.*;
import com.kirisamemarisa.blog.service.BlogPostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blogpost")
public class BlogPostController {

    private final BlogPostService blogPostService;

    public BlogPostController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    @PostMapping
    public ApiResponse<Long> create(@RequestBody BlogPostCreateDTO dto) {
        return blogPostService.create(dto);
    }

    @GetMapping("/{id}")
    public BlogPostDTO get(@PathVariable Long id) {
        return blogPostService.getById(id);
    }

    @GetMapping
    public List<BlogPostDTO> list(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(required = false) Long currentUserId) {
        return blogPostService.list(page, size, currentUserId);
    }

    @PostMapping("/{id}/like")
    public ApiResponse<Boolean> toggleLike(@PathVariable Long id,
                                           @RequestParam Long userId) {
        return blogPostService.toggleLike(id, userId);
    }

    @PostMapping("/comment")
    public ApiResponse<Long> comment(@RequestBody CommentCreateDTO dto) {
        return blogPostService.addComment(dto);
    }

    @GetMapping("/{id}/comments")
    public List<CommentDTO> comments(@PathVariable Long id,
                                     @RequestParam(required = false) Long currentUserId) {
        return blogPostService.listComments(id, currentUserId);
    }

    @PostMapping("/comment/{id}/like")
    public ApiResponse<Boolean> toggleCommentLike(@PathVariable Long id,
                                                  @RequestParam Long userId) {
        return blogPostService.toggleCommentLike(id, userId);
    }

    @PostMapping("/repost")
    public ApiResponse<Long> repost(@RequestBody RepostCreateDTO dto) {
        return blogPostService.repost(dto);
    }
}

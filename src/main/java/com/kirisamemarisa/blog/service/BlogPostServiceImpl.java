package com.kirisamemarisa.blog.service;

import com.kirisamemarisa.blog.common.ApiResponse;
import com.kirisamemarisa.blog.dto.BlogPostCreateDTO;
import com.kirisamemarisa.blog.dto.BlogPostDTO;
import com.kirisamemarisa.blog.dto.BlogPostUpdateDTO;

public interface BlogPostServiceImpl {
    ApiResponse<Long> create(BlogPostCreateDTO dto);
    BlogPostDTO getById(Long id);
    boolean update(Long id, BlogPostUpdateDTO dto);
    boolean incrementLike(Long id);
    boolean incrementComment(Long id);
    boolean incrementShare(Long id);
}

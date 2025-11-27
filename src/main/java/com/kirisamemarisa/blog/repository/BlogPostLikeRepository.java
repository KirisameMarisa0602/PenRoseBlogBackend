package com.kirisamemarisa.blog.repository;

import com.kirisamemarisa.blog.model.BlogPostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BlogPostLikeRepository extends JpaRepository<BlogPostLike, Long> {
    Optional<BlogPostLike> findByBlogPostIdAndUserId(Long blogPostId, Long userId);
    long countByBlogPostId(Long blogPostId);
}

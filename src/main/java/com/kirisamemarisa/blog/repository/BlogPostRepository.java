package com.kirisamemarisa.blog.repository;

import com.kirisamemarisa.blog.model.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
}

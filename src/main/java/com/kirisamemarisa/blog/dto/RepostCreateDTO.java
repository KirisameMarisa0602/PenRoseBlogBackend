package com.kirisamemarisa.blog.dto;

public class RepostCreateDTO {
    private Long originalPostId;
    private Long userId;

    public Long getOriginalPostId() { return originalPostId; }
    public void setOriginalPostId(Long originalPostId) { this.originalPostId = originalPostId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}

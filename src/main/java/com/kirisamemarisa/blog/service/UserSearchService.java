// src/main/java/com/kirisamemarisa/blog/service/UserSearchService.java
package com.kirisamemarisa.blog.service;

import com.kirisamemarisa.blog.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 用户搜索服务（不修改现有 Repository，使用 JPQL）。
 */
@Service
public class UserSearchService {

    @PersistenceContext
    private EntityManager em;

    /**
     * 按用户名模糊查询。
     */
    public List<User> searchByUsername(String username) {
        if (username == null || username.isBlank()) return Collections.emptyList();
        return em.createQuery(
                        "SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :kw, '%'))",
                        User.class)
                .setParameter("kw", username.trim())
                .getResultList();
    }

    /**
     * 按昵称模糊查询（需存在 UserProfile.nickname；若不存在会返回空）。
     * 可在后续为 UserProfile 添加字段后自动工作。
     */
    public List<User> searchByNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) return Collections.emptyList();
        // 尝试执行 JOIN 查询，若映射不存在可能抛异常 -> 捕获返回空
        try {
            return em.createQuery(
                            "SELECT u FROM User u JOIN u.profile p " +
                                    "WHERE p.nickname IS NOT NULL AND LOWER(p.nickname) LIKE LOWER(CONCAT('%', :kw, '%'))",
                            User.class)
                    .setParameter("kw", nickname.trim())
                    .getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}

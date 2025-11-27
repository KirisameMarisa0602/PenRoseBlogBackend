// src/main/java/com/kirisamemarisa/blog/controller/UserSearchController.java
package com.kirisamemarisa.blog.controller;

import com.kirisamemarisa.blog.common.ApiResponse;
import com.kirisamemarisa.blog.dto.UserSimpleDTO;
import com.kirisamemarisa.blog.model.User;
import com.kirisamemarisa.blog.service.UserSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户搜索接口控制器。
 * 路径示例：
 * /api/users/search?username=cj_test1
 * /api/users/search?nickname=小明
 */
@RestController
public class UserSearchController {

    private final UserSearchService userSearchService;

    public UserSearchController(UserSearchService userSearchService) {
        this.userSearchService = userSearchService;
    }

    @GetMapping("/api/users/search")
    public ApiResponse<List<UserSimpleDTO>> search(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname) {

        List<User> list;
        if (username != null && !username.isBlank()) {
            list = userSearchService.searchByUsername(username);
        } else if (nickname != null && !nickname.isBlank()) {
            list = userSearchService.searchByNickname(nickname);
        } else {
            return new ApiResponse<>(400, "参数缺失：需提供 username 或 nickname", null);
        }

        List<UserSimpleDTO> dtoList = list.stream()
                .map(UserSimpleDTO::from)
                .collect(Collectors.toList());

        return new ApiResponse<>(200, "OK", dtoList);
    }
}

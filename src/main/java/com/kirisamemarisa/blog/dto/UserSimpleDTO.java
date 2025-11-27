// src/main/java/com/kirisamemarisa/blog/dto/UserSimpleDTO.java
package com.kirisamemarisa.blog.dto;

import com.kirisamemarisa.blog.model.User;
import com.kirisamemarisa.blog.model.UserProfile;

/**
 * 精简用户DTO（不包含密码）。
 */
public class UserSimpleDTO {
    private Long id;
    private String username;
    private String gender;
    private String nickname; // 兼容后续可能添加
    private String avatarUrl;
    private String bio;

    public static UserSimpleDTO from(User u) {
        if (u == null) return null;
        UserSimpleDTO dto = new UserSimpleDTO();
        dto.id = u.getId();
        dto.username = u.getUsername();
        dto.gender = u.getGender();
        UserProfile profile = null;
        try {
            // 若 User 有 getProfile()
            profile = (UserProfile) User.class.getMethod("getProfile").invoke(u);
        } catch (Exception ignored) {}
        if (profile != null) {
            // 反射尝试 nickname
            try {
                Object nick = UserProfile.class.getMethod("getNickname").invoke(profile);
                dto.nickname = nick == null ? null : nick.toString();
            } catch (Exception ignored) {}
            try {
                Object avatar = UserProfile.class.getMethod("getAvatarUrl").invoke(profile);
                dto.avatarUrl = avatar == null ? null : avatar.toString();
            } catch (Exception ignored) {}
            try {
                Object bio = UserProfile.class.getMethod("getBio").invoke(profile);
                dto.bio = bio == null ? null : bio.toString();
            } catch (Exception ignored) {}
        }
        return dto;
    }

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}

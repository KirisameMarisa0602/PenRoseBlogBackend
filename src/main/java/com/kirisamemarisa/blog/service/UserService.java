package com.kirisamemarisa.blog.service;

import com.kirisamemarisa.blog.common.ApiResponse;
import com.kirisamemarisa.blog.common.JwtUtil;
import com.kirisamemarisa.blog.dto.UserLoginDTO;
import com.kirisamemarisa.blog.dto.UserRegisterDTO;
import com.kirisamemarisa.blog.dto.UserProfileDTO;
import com.kirisamemarisa.blog.mapper.UserMapper;
import com.kirisamemarisa.blog.mapper.UserProfileMapper;
import com.kirisamemarisa.blog.model.User;
import com.kirisamemarisa.blog.model.UserProfile;
import com.kirisamemarisa.blog.repository.UserRepository;
import com.kirisamemarisa.blog.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserProfileMapper userProfileMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ApiResponse<Void> register(UserRegisterDTO dto) {
        if (dto == null) return new ApiResponse<>(400, "请求体为空", null);
        String username = dto.getUsername();
        String password = dto.getPassword();
        if (username == null || !username.matches("^[A-Za-z0-9_]{5,15}$"))
            return new ApiResponse<>(400, "用户名格式不合法", null);
        if (password == null || !password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,12}$"))
            return new ApiResponse<>(400, "密码格式不合法", null);
        if (userRepository.findByUsername(username) != null)
            return new ApiResponse<>(400, "用户名已存在", null);
        User user = userMapper.toUser(dto);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return new ApiResponse<>(200, "注册成功", null);
    }

    public ApiResponse<String> login(UserLoginDTO dto) {
        if (dto == null) return new ApiResponse<>(400, "请求体为空", null);
        String username = dto.getUsername();
        String password = dto.getPassword();
        if (username == null || password == null)
            return new ApiResponse<>(400, "用户名或密码为空", null);
        User dbUser = userRepository.findByUsername(username);
        if (dbUser == null) return new ApiResponse<>(404, "用户不存在", null);
        if (!passwordEncoder.matches(password, dbUser.getPassword()))
            return new ApiResponse<>(401, "密码错误", null);
        String token = JwtUtil.generateToken(dbUser.getUsername());
        return new ApiResponse<>(200, "登录成功", token);
    }

    public UserProfileDTO getUserProfileDTO(Long userId) {
        if (userId == null) return null;
        Optional<UserProfile> opt = userProfileRepository.findById(userId);
        return opt.map(userProfileMapper::toDTO).orElse(null);
    }

    // 通过显式传入 userId 更新资料（避免使用不存在的 dto.getUserId()）
    public boolean updateUserProfile(Long userId, UserProfileDTO dto) {
        if (userId == null || dto == null) return false;
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return false;

        UserProfile profile = userProfileRepository.findById(userId).orElseGet(() -> {
            UserProfile p = new UserProfile();
            p.setUser(userOpt.get()); // MapsId 会同步主键
            return p;
        });

        profile.setNickname(dto.getNickname());
        profile.setAvatarUrl(dto.getAvatarUrl());
        profile.setBackgroundUrl(dto.getBackgroundUrl());

        userProfileRepository.save(profile);
        return true;
    }
}

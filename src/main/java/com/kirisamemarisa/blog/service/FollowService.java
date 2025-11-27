package com.kirisamemarisa.blog.service;

import com.kirisamemarisa.blog.model.Follow;
import com.kirisamemarisa.blog.model.User;

import java.util.List;

public interface FollowService {
    Follow follow(User follower, User followee);
    void unfollow(User follower, User followee);
    boolean isFollowing(User follower, User followee);
    boolean areFriends(User a, User b);
    List<User> listFollowers(User user);
    List<User> listFollowing(User user);
}

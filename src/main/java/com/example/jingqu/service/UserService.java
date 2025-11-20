package com.example.jingqu.service;

import com.example.jingqu.entity.User;
import com.example.jingqu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User findOrCreateUser(String openid, String nickName, String avatarUrl) {
        Optional<User> existingUser = userRepository.findByOpenid(openid);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // 更新用户信息
            user.setNickName(nickName);
            user.setAvatarUrl(avatarUrl);
            return userRepository.save(user);
        } else {
            // 创建新用户
            User newUser = new User(openid, nickName, avatarUrl);
            return userRepository.save(newUser);
        }
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    public User getUserByOpenid(String openid) {
        return userRepository.findByOpenid(openid).orElse(null);
    }
    
    public boolean userExists(String openid) {
        return userRepository.existsByOpenid(openid);
    }
    
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public boolean isAdmin(Long userId) {
        User user = getUserById(userId);
        return user != null && Boolean.TRUE.equals(user.getIsAdmin());
    }
    
    public boolean isAdminByOpenid(String openid) {
        User user = getUserByOpenid(openid);
        return user != null && Boolean.TRUE.equals(user.getIsAdmin());
    }
    
    public boolean deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }
    
    public boolean deleteUserByOpenid(String openid) {
        Optional<User> user = userRepository.findByOpenid(openid);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            return true;
        }
        return false;
    }
}

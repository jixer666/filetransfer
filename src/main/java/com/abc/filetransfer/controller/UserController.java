package com.abc.filetransfer.controller;

import com.abc.filetransfer.entity.User;
import com.abc.filetransfer.entity.RegistrationAudit;
import com.abc.filetransfer.service.UserService;
import com.abc.filetransfer.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User loginUser, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User user = userService.login(loginUser.getUsername(), loginUser.getPassword());
        if (user != null) {
            // 检查账户状态
            if ("inactive".equals(user.getStatus())) {
                result.put("success", false);
                result.put("message", "账户已停用，请联系管理员");
            } else {
                // 生成JWT token
                String token = jwtUtil.generateToken(user.getUsername());
                result.put("success", true);
                result.put("message", "登录成功");
                result.put("user", user);
                result.put("token", token);
            }
        } else {
            result.put("success", false);
            result.put("message", "用户名或密码错误");
        }
        return result;
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Map<String, Object> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> result = new HashMap<>();
        // 对于JWT token，登出时只需清除前端的token即可
        result.put("success", true);
        result.put("message", "登出成功");
        return result;
    }

    /**
     * 用户注册申请
     */
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody RegistrationAudit registration) {
        Map<String, Object> result = new HashMap<>();
        // 检查用户名是否已存在
        User existingUser = userService.getOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>()
                .eq("username", registration.getUsername())
        );
        
        if (existingUser != null) {
            result.put("success", false);
            result.put("message", "用户名已存在");
            return result;
        }
        
        boolean success = userService.register(registration);
        if (success) {
            result.put("success", true);
            result.put("message", "注册申请已提交，请等待管理员审核");
        } else {
            result.put("success", false);
            result.put("message", "注册申请提交失败");
        }
        return result;
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public Map<String, Object> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> result = new HashMap<>();
        
        // 必须提供token
        if (token == null || !token.startsWith("Bearer ")) {
            result.put("success", false);
            result.put("message", "未提供有效的认证令牌");
            return result;
        }
        
        try {
            String username = jwtUtil.extractUsername(token.substring(7));
            User user = userService.getOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>()
                    .eq("username", username)
            );
            if (user != null) {
                result.put("success", true);
                result.put("user", user);
            } else {
                result.put("success", false);
                result.put("message", "用户不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "令牌无效或已过期");
        }
        return result;
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Map<String, Object> updateUserInfo(@RequestBody User user, 
                                              @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> result = new HashMap<>();
        
        // 必须提供token
        if (token == null || !token.startsWith("Bearer ")) {
            result.put("success", false);
            result.put("message", "未提供有效的认证令牌");
            return result;
        }
        
        try {
            String username = jwtUtil.extractUsername(token.substring(7));
            User currentUser = userService.getOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>()
                    .eq("username", username)
            );
            
            if (currentUser == null) {
                result.put("success", false);
                result.put("message", "用户未登录");
                return result;
            }

            // 只能更新自己的信息，且不能更改角色和状态
            user.setUserId(currentUser.getUserId());
            user.setRole(currentUser.getRole());
            user.setStatus(currentUser.getStatus());
            user.setUsername(currentUser.getUsername());

            boolean success = userService.updateUserInfo(user);
            if (success) {
                result.put("success", true);
                result.put("message", "信息更新成功");
            } else {
                result.put("success", false);
                result.put("message", "信息更新失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "令牌无效或已过期");
        }
        return result;
    }
}
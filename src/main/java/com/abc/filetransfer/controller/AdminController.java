package com.abc.filetransfer.controller;

import com.abc.filetransfer.entity.User;
import com.abc.filetransfer.entity.RegistrationAudit;
import com.abc.filetransfer.entity.File;
import com.abc.filetransfer.entity.DownloadLog;
import com.abc.filetransfer.service.UserService;
import com.abc.filetransfer.service.FileService;
import com.abc.filetransfer.mapper.DownloadLogMapper;
import com.abc.filetransfer.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private FileService fileService;

    @Autowired
    private DownloadLogMapper downloadLogMapper;

    /**
     * 从token中获取当前用户
     */
    private User getCurrentUser(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        
        try {
            String username = jwtUtil.extractUsername(token.substring(7));
            return userService.getOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>()
                    .eq("username", username)
            );
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 检查是否为管理员
     */
    private boolean isAdmin(@RequestHeader(value = "Authorization", required = false) String token) {
        User user = getCurrentUser(token);
        return user != null && "admin".equals(user.getRole());
    }

    /**
     * 获取待审核的注册申请
     */
    @GetMapping("/registrations/pending")
    public Map<String, Object> getPendingRegistrations(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> result = new HashMap<>();
        if (!isAdmin(token)) {
            result.put("success", false);
            result.put("message", "权限不足");
            return result;
        }

        List<RegistrationAudit> registrations = userService.findPendingRegistrations();
        result.put("success", true);
        result.put("data", registrations);
        return result;
    }

    /**
     * 审核用户注册申请
     */
    @PostMapping("/registrations/{id}/approve")
    public Map<String, Object> approveRegistration(@PathVariable Integer id, 
                                                   @RequestParam boolean approve,
                                                   @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> result = new HashMap<>();
        if (!isAdmin(token)) {
            result.put("success", false);
            result.put("message", "权限不足");
            return result;
        }

        User admin = getCurrentUser(token);
        boolean success = userService.approveRegistration(id, admin.getUserId(), approve);
        if (success) {
            result.put("success", true);
            result.put("message", approve ? "用户注册已批准" : "用户注册已拒绝");
        } else {
            result.put("success", false);
            result.put("message", "操作失败");
        }
        return result;
    }

    /**
     * 获取所有用户
     */
    @GetMapping("/users")
    public Map<String, Object> getAllUsers(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> result = new HashMap<>();
        if (!isAdmin(token)) {
            result.put("success", false);
            result.put("message", "权限不足");
            return result;
        }

        List<User> users = userService.findAllUsers();
        result.put("success", true);
        result.put("data", users);
        return result;
    }

    /**
     * 更新用户状态（停用/启用）
     */
    @PostMapping("/users/{id}/status")
    public Map<String, Object> updateUserStatus(@PathVariable Integer id,
                                                @RequestParam String status,
                                                @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> result = new HashMap<>();
        if (!isAdmin(token)) {
            result.put("success", false);
            result.put("message", "权限不足");
            return result;
        }

        boolean success = userService.updateUserStatus(id, status);
        if (success) {
            result.put("success", true);
            result.put("message", "用户状态更新成功");
        } else {
            result.put("success", false);
            result.put("message", "用户状态更新失败");
        }
        return result;
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/users/{id}")
    public Map<String, Object> deleteUser(@PathVariable Integer id, 
                                          @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> result = new HashMap<>();
        if (!isAdmin(token)) {
            result.put("success", false);
            result.put("message", "权限不足");
            return result;
        }

        boolean success = userService.deleteUser(id);
        if (success) {
            result.put("success", true);
            result.put("message", "用户删除成功");
        } else {
            result.put("success", false);
            result.put("message", "用户删除失败");
        }
        return result;
    }

    /**
     * 获取所有文件
     */
    @GetMapping("/files")
    public Map<String, Object> getAllFiles(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> result = new HashMap<>();
        if (!isAdmin(token)) {
            result.put("success", false);
            result.put("message", "权限不足");
            return result;
        }

        List<File> files = fileService.findAllFiles();
        // 为每个文件添加下载记录
        for (File file : files) {
            List<DownloadLog> downloadLogs = downloadLogMapper.findByFileId(file.getFileId());
            // 这里可以添加下载记录到文件对象中，如果需要的话
        }
        
        result.put("success", true);
        result.put("data", files);
        return result;
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/files/{id}")
    public Map<String, Object> deleteFile(@PathVariable Integer id, 
                                          @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> result = new HashMap<>();
        if (!isAdmin(token)) {
            result.put("success", false);
            result.put("message", "权限不足");
            return result;
        }

        boolean success = fileService.deleteFileById(id);
        if (success) {
            result.put("success", true);
            result.put("message", "文件删除成功");
        } else {
            result.put("success", false);
            result.put("message", "文件删除失败");
        }
        return result;
    }

    /**
     * 获取文件的下载记录
     */
    @GetMapping("/files/{id}/downloads")
    public Map<String, Object> getFileDownloads(@PathVariable Integer id, 
                                                @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> result = new HashMap<>();
        if (!isAdmin(token)) {
            result.put("success", false);
            result.put("message", "权限不足");
            return result;
        }

        List<DownloadLog> downloadLogs = downloadLogMapper.findByFileId(id);
        result.put("success", true);
        result.put("data", downloadLogs);
        return result;
    }
}
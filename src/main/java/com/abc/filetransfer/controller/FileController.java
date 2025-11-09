package com.abc.filetransfer.controller;

import com.abc.filetransfer.entity.File;
import com.abc.filetransfer.entity.User;
import com.abc.filetransfer.service.FileService;
import com.abc.filetransfer.service.UserService;
import com.abc.filetransfer.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "http://localhost:8081")
public class FileController {

    @Autowired
    private FileService fileService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;

    // 从配置文件中读取文件存储路径
    @Value("${file.upload.path}")
    private String uploadPath;

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
     * 上传文件
     */
    @PostMapping("/upload")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile multipartFile,
                                          @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> result = new HashMap<>();
        User user = getCurrentUser(token);
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户未登录");
            return result;
        }

        try {
            // 检查文件大小（不大于2GB）
            if (multipartFile.getSize() > 2L * 1024 * 1024 * 1024) {
                result.put("success", false);
                result.put("message", "文件大小不能超过2GB");
                return result;
            }

            // 检查文件类型（只能是单个文件或压缩包）
            String originalFilename = multipartFile.getOriginalFilename();
            if (originalFilename != null && !isValidFileType(originalFilename)) {
                result.put("success", false);
                result.put("message", "只能上传单个文件或压缩包");
                return result;
            }

            // 生成唯一的文件名
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String storedFilename = UUID.randomUUID().toString() + fileExtension;

            // 创建文件存储目录
            Path path = Paths.get(uploadPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            // 保存文件
            File file = new File();
            file.setUserId(user.getUserId());
            file.setOriginalName(originalFilename);
            file.setStoredName(storedFilename);
            file.setFileType(fileExtension);
            file.setFileSize(multipartFile.getSize());
            
            // 生成提取码和删除码（5位字母+数字随机组合）
            String extractCode = generateRandomCode(5);
            String deleteCode = generateRandomCode(5);
            file.setExtractCode(extractCode);
            file.setDeleteCode(deleteCode);
            
            // 设置文件状态为有效
            file.setStatus("active");
            
            // 设置上传时间和过期时间（48小时后）
            LocalDateTime uploadTime = LocalDateTime.now();
            LocalDateTime expireTime = uploadTime.plusHours(48);
            file.setUploadTime(uploadTime);
            file.setExpireTime(expireTime);
            
            file.setFilePath(uploadPath + java.io.File.separator + storedFilename);

            // 保存文件到磁盘
            Path filePath = path.resolve(storedFilename);
            multipartFile.transferTo(filePath.toFile());

            // 保存文件信息到数据库
            boolean success = fileService.uploadFile(file);
            if (success) {
                result.put("success", true);
                result.put("message", "文件上传成功");
                Map<String, Object> data = new HashMap<>();
                data.put("extractCode", extractCode);
                data.put("deleteCode", deleteCode);
                result.put("data", data);
            } else {
                result.put("success", false);
                result.put("message", "文件上传失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "文件上传异常: " + e.getMessage());
        }
        return result;
    }

    /**
     * 检查文件类型是否有效
     */
    private boolean isValidFileType(String filename) {
        // 允许的压缩包格式
        String[] allowedExtensions = {".zip", ".rar", ".7z", ".tar", ".gz"};
        
        // 检查是否为压缩包
        for (String ext : allowedExtensions) {
            if (filename.toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        
        // 允许普通文件
        return true;
    }

    /**
     * 生成随机码
     */
    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            code.append(chars.charAt(index));
        }
        return code.toString();
    }

    /**
     * 根据提取码下载文件（访客模式）
     */
    @GetMapping("/download/visitor")
    public void downloadFileByExtractCode(@RequestParam String extractCode,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {
        try {
            // 这里应该调用service层的方法来处理下载
            // 为了简化，我们直接处理文件下载逻辑
            com.abc.filetransfer.entity.File file = fileService.getOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.abc.filetransfer.entity.File>()
                    .eq("extract_code", extractCode)
            );
            
            if (file == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "文件不存在");
                return;
            }
            
            if (!"active".equals(file.getStatus())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "文件不可用");
                return;
            }
            
            // 检查文件是否过期
            if (file.getExpireTime().isBefore(LocalDateTime.now())) {
                // 更新文件状态为已过期
                file.setStatus("expired");
                fileService.updateById(file);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "文件已过期");
                return;
            }
            
            // 获取文件路径
            Path filePath = Paths.get(file.getFilePath());
            if (!Files.exists(filePath)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "文件不存在");
                return;
            }
            
            // 设置响应头
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + 
                              new String(file.getOriginalName().getBytes("UTF-8"), "ISO-8859-1") + "\"");
            response.setContentLengthLong(file.getFileSize());
            
            // 输出文件内容
            try (InputStream inputStream = Files.newInputStream(filePath);
                 OutputStream outputStream = response.getOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }
            
            // 增加下载次数
            file.setDownloadCount(file.getDownloadCount() + 1);
            fileService.updateById(file);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "下载失败");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**
     * 根据提取码下载文件（用户模式）
     */
    @GetMapping("/download")
    public void downloadFileByExtractCode(@RequestParam String extractCode,
                                          @RequestHeader(value = "Authorization", required = false) String token,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {
        User user = getCurrentUser(token);
        if (user == null) {
            try {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "用户未登录");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        
        downloadFileByExtractCode(extractCode, request, response);
    }

    /**
     * 根据删除码删除文件
     */
    @PostMapping("/delete")
    public Map<String, Object> deleteFile(@RequestParam String deleteCode, 
                                          @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> result = new HashMap<>();
        User user = getCurrentUser(token);
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户未登录");
            return result;
        }

        // 查找文件
        com.abc.filetransfer.entity.File file = fileService.getOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.abc.filetransfer.entity.File>()
                .eq("delete_code", deleteCode)
        );

        if (file == null) {
            result.put("success", false);
            result.put("message", "文件不存在");
            return result;
        }

        try {
            // 删除文件记录
            boolean success = fileService.removeById(file.getFileId());
            
            // 删除物理文件
            if (success) {
                Path filePath = Paths.get(file.getFilePath());
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
                
                result.put("success", true);
                result.put("message", "文件删除成功");
            } else {
                result.put("success", false);
                result.put("message", "文件删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "文件删除异常: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取当前用户上传的文件历史记录
     */
    @GetMapping("/history")
    public Map<String, Object> getFileHistory(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> result = new HashMap<>();
        User user = getCurrentUser(token);
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户未登录");
            return result;
        }

        List<com.abc.filetransfer.entity.File> files = fileService.findFilesByUser(user.getUserId());
        result.put("success", true);
        result.put("data", files);
        return result;
    }
}
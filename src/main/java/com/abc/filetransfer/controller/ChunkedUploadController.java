package com.abc.filetransfer.controller;

import com.abc.filetransfer.entity.User;
import com.abc.filetransfer.service.ChunkedUploadService;
import com.abc.filetransfer.service.UserService;
import com.abc.filetransfer.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/chunked")
public class ChunkedUploadController {

    @Autowired
    private ChunkedUploadService chunkedUploadService;
    
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
     * 上传文件分片
     */
    @PostMapping("/upload")
    public Map<String, Object> uploadChunk(
            @RequestParam("chunk") MultipartFile chunk,
            @RequestParam("chunkNumber") int chunkNumber,
            @RequestParam("totalChunks") int totalChunks,
            @RequestParam("filename") String filename,
            @RequestParam("fileSize") long fileSize,
            @RequestParam("identifier") String identifier,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Map<String, Object> result = new HashMap<>();
        User user = getCurrentUser(token);
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户未登录");
            return result;
        }

        try {
            // 创建临时目录存储分片
            Path tempDir = Paths.get(uploadPath, "temp", identifier);
            if (!Files.exists(tempDir)) {
                Files.createDirectories(tempDir);
            }

            // 保存分片
            Path chunkPath = tempDir.resolve("chunk_" + chunkNumber);
            chunk.transferTo(chunkPath.toFile());

            // 记录已上传的分片
            chunkedUploadService.markChunkUploaded(identifier, chunkNumber);

            // 更新文件信息
            ChunkedUploadService.FileInfo fileInfo = chunkedUploadService.getFileInfo(identifier);
            if (fileInfo == null) {
                fileInfo = new ChunkedUploadService.FileInfo(filename, fileSize, totalChunks);
                chunkedUploadService.saveFileInfo(identifier, fileInfo);
            }
            fileInfo.addUploadedSize(chunk.getSize());
            chunkedUploadService.saveFileInfo(identifier, fileInfo);

            // 检查是否所有分片都已上传
            if (isAllChunksUploaded(identifier, totalChunks)) {
                // 合并所有分片
                String storedFilename = UUID.randomUUID().toString() + "_" + filename;
                Path finalFilePath = Paths.get(uploadPath, storedFilename);
                
                try (OutputStream out = Files.newOutputStream(finalFilePath, StandardOpenOption.CREATE)) {
                    for (int i = 1; i <= totalChunks; i++) {
                        Path partPath = tempDir.resolve("chunk_" + i);
                        Files.copy(partPath, out);
                        // 删除已合并的分片
                        Files.delete(partPath);
                    }
                }
                
                // 删除临时目录
                Files.delete(tempDir);
                
                // 清理Redis记录
                chunkedUploadService.cleanUploadRecord(identifier);
                
                result.put("success", true);
                result.put("message", "文件上传完成");
                result.put("filePath", finalFilePath.toString());
                result.put("storedFilename", storedFilename);
            } else {
                result.put("success", true);
                result.put("message", "分片上传成功");
                result.put("uploadedSize", fileInfo.getUploadedSize());
                result.put("totalSize", fileInfo.getFileSize());
                result.put("progress", (double) fileInfo.getUploadedSize() / fileInfo.getFileSize() * 100);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "文件上传异常: " + e.getMessage());
        }
        return result;
    }

    /**
     * 检查所有分片是否已上传
     */
    private boolean isAllChunksUploaded(String identifier, int totalChunks) {
        for (int i = 1; i <= totalChunks; i++) {
            if (!chunkedUploadService.isChunkUploaded(identifier, i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查分片是否已上传
     */
    @GetMapping("/check")
    public Map<String, Object> checkChunk(
            @RequestParam("chunkNumber") int chunkNumber,
            @RequestParam("identifier") String identifier) {
        
        Map<String, Object> result = new HashMap<>();
        try {
            boolean exists = chunkedUploadService.isChunkUploaded(identifier, chunkNumber);
            result.put("success", true);
            result.put("exists", exists);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "检查分片异常: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取上传进度
     */
    @GetMapping("/progress")
    public Map<String, Object> getUploadProgress(@RequestParam("identifier") String identifier) {
        Map<String, Object> result = new HashMap<>();
        try {
            ChunkedUploadService.FileInfo fileInfo = chunkedUploadService.getFileInfo(identifier);
            if (fileInfo != null) {
                result.put("success", true);
                result.put("uploadedSize", fileInfo.getUploadedSize());
                result.put("totalSize", fileInfo.getFileSize());
                result.put("progress", (double) fileInfo.getUploadedSize() / fileInfo.getFileSize() * 100);
                result.put("uploadedChunks", chunkedUploadService.getUploadedChunks(identifier));
            } else {
                result.put("success", false);
                result.put("message", "未找到上传记录");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "获取进度异常: " + e.getMessage());
        }
        return result;
    }
}
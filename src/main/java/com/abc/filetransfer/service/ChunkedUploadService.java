package com.abc.filetransfer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class ChunkedUploadService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CHUNK_KEY_PREFIX = "chunked_upload:";
    private static final String UPLOADED_CHUNKS_KEY_PREFIX = "uploaded_chunks:";
    private static final String FILE_INFO_KEY_PREFIX = "file_info:";

    /**
     * 记录已上传的分片
     *
     * @param identifier 文件唯一标识
     * @param chunkNumber 分片编号
     */
    public void markChunkUploaded(String identifier, int chunkNumber) {
        String key = UPLOADED_CHUNKS_KEY_PREFIX + identifier;
        redisTemplate.opsForSet().add(key, chunkNumber);
        // 设置过期时间24小时
        redisTemplate.expire(key, 24, TimeUnit.HOURS);
    }

    /**
     * 检查分片是否已上传
     *
     * @param identifier 文件唯一标识
     * @param chunkNumber 分片编号
     * @return 是否已上传
     */
    public boolean isChunkUploaded(String identifier, int chunkNumber) {
        String key = UPLOADED_CHUNKS_KEY_PREFIX + identifier;
        return redisTemplate.opsForSet().isMember(key, chunkNumber);
    }

    /**
     * 获取已上传的分片集合
     *
     * @param identifier 文件唯一标识
     * @return 已上传的分片集合
     */
    public Set<Object> getUploadedChunks(String identifier) {
        String key = UPLOADED_CHUNKS_KEY_PREFIX + identifier;
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 保存文件信息
     *
     * @param identifier 文件唯一标识
     * @param fileInfo 文件信息
     */
    public void saveFileInfo(String identifier, FileInfo fileInfo) {
        String key = FILE_INFO_KEY_PREFIX + identifier;
        redisTemplate.opsForValue().set(key, fileInfo);
        // 设置过期时间24小时
        redisTemplate.expire(key, 24, TimeUnit.HOURS);
    }

    /**
     * 获取文件信息
     *
     * @param identifier 文件唯一标识
     * @return 文件信息
     */
    public FileInfo getFileInfo(String identifier) {
        String key = FILE_INFO_KEY_PREFIX + identifier;
        return (FileInfo) redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除上传记录
     *
     * @param identifier 文件唯一标识
     */
    public void cleanUploadRecord(String identifier) {
        redisTemplate.delete(UPLOADED_CHUNKS_KEY_PREFIX + identifier);
        redisTemplate.delete(FILE_INFO_KEY_PREFIX + identifier);
    }

    /**
     * 文件信息类
     */
    public static class FileInfo {
        private String filename;
        private long fileSize;
        private String filePath;
        private int totalChunks;
        private long uploadedSize;

        // 构造函数
        public FileInfo() {}

        public FileInfo(String filename, long fileSize, int totalChunks) {
            this.filename = filename;
            this.fileSize = fileSize;
            this.totalChunks = totalChunks;
            this.uploadedSize = 0;
        }

        // Getter和Setter方法
        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public int getTotalChunks() {
            return totalChunks;
        }

        public void setTotalChunks(int totalChunks) {
            this.totalChunks = totalChunks;
        }

        public long getUploadedSize() {
            return uploadedSize;
        }

        public void setUploadedSize(long uploadedSize) {
            this.uploadedSize = uploadedSize;
        }

        public void addUploadedSize(long size) {
            this.uploadedSize += size;
        }
    }
}
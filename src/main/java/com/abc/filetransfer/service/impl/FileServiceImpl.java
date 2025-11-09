package com.abc.filetransfer.service.impl;

import com.abc.filetransfer.entity.File;
import com.abc.filetransfer.entity.DownloadLog;
import com.abc.filetransfer.mapper.FileMapper;
import com.abc.filetransfer.mapper.DownloadLogMapper;
import com.abc.filetransfer.service.FileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private DownloadLogMapper downloadLogMapper;

    @Override
    @Transactional
    public boolean uploadFile(File file) {
        file.setUploadTime(LocalDateTime.now());
        // 设置过期时间为48小时后
        file.setExpireTime(file.getUploadTime().plusHours(48));
        file.setStatus("active");
        file.setDownloadCount(0);
        return fileMapper.insert(file) > 0;
    }

    @Override
    @Transactional
    public File downloadFile(String extractCode, Integer userId, String ipAddress) {
        File file = fileMapper.findByExtractCode(extractCode);
        if (file != null && "active".equals(file.getStatus())) {
            // 检查文件是否过期
            if (file.getExpireTime().isAfter(LocalDateTime.now())) {
                // 记录下载日志
                DownloadLog downloadLog = new DownloadLog();
                downloadLog.setFileId(file.getFileId());
                downloadLog.setUserId(userId);
                downloadLog.setDownloadTime(LocalDateTime.now());
                downloadLog.setIpAddress(ipAddress);
                downloadLogMapper.insert(downloadLog);

                // 增加下载次数
                fileMapper.incrementDownloadCount(file.getFileId());

                return file;
            } else {
                // 文件已过期，更新状态
                file.setStatus("expired");
                fileMapper.updateById(file);
            }
        }
        return null;
    }

    @Override
    @Transactional
    public boolean deleteFile(String deleteCode, Integer userId) {
        File file = fileMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<File>()
                .eq("delete_code", deleteCode)
        );
        
        if (file != null) {
            // 物理删除文件
            boolean deleted = this.removeById(file.getFileId());
            return deleted;
        }
        return false;
    }

    @Override
    public List<File> findFilesByUser(Integer userId) {
        return fileMapper.findByUserId(userId);
    }

    @Override
    public List<File> findAllFiles() {
        return fileMapper.selectList(null);
    }

    @Override
    public List<File> findFilesByStatus(String status) {
        return fileMapper.findByStatus(status);
    }

    @Override
    public boolean deleteFileById(Integer fileId) {
        return this.removeById(fileId);
    }
}
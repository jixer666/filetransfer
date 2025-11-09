package com.abc.filetransfer.service;

import com.abc.filetransfer.entity.File;
import com.abc.filetransfer.entity.DownloadLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface FileService extends IService<File> {
    /**
     * 上传文件
     * @param file 文件信息
     * @return 是否成功
     */
    boolean uploadFile(File file);

    /**
     * 根据提取码下载文件
     * @param extractCode 提取码
     * @param userId 用户ID
     * @param ipAddress IP地址
     * @return 文件对象
     */
    File downloadFile(String extractCode, Integer userId, String ipAddress);

    /**
     * 根据删除码删除文件
     * @param deleteCode 删除码
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteFile(String deleteCode, Integer userId);

    /**
     * 查找用户上传的所有文件
     * @param userId 用户ID
     * @return 文件列表
     */
    List<File> findFilesByUser(Integer userId);

    /**
     * 查找所有文件
     * @return 文件列表
     */
    List<File> findAllFiles();

    /**
     * 根据状态查找文件
     * @param status 状态
     * @return 文件列表
     */
    List<File> findFilesByStatus(String status);

    /**
     * 删除文件
     * @param fileId 文件ID
     * @return 是否成功
     */
    boolean deleteFileById(Integer fileId);
}
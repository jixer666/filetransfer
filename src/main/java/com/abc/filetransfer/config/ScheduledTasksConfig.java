package com.abc.filetransfer.config;

import com.abc.filetransfer.entity.File;
import com.abc.filetransfer.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduledTasksConfig {

    @Autowired
    private FileService fileService;

    /**
     * 每小时检查一次过期文件并更新状态
     */
    @Scheduled(fixedRate = 3600000) // 每小时执行一次
    public void checkExpiredFiles() {
        // 查找所有有效文件
        List<File> activeFiles = fileService.findFilesByStatus("active");
        
        LocalDateTime now = LocalDateTime.now();
        for (File file : activeFiles) {
            // 检查文件是否过期
            if (file.getExpireTime().isBefore(now)) {
                // 更新文件状态为已过期
                file.setStatus("expired");
                fileService.updateById(file);
            }
        }
    }
}
package com.abc.filetransfer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("download_logs")
public class DownloadLog {
    /**
     * 下载记录唯一标识（主键，自增）
     */
    @TableId(value = "download_id", type = IdType.AUTO)
    private Integer downloadId;

    /**
     * 被下载的文件ID
     */
    @TableField("file_id")
    private Integer fileId;

    /**
     * 下载用户ID
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 下载时间
     */
    @TableField("download_time")
    private LocalDateTime downloadTime;

    /**
     * 下载ip地址
     */
    @TableField("ip_address")
    private String ipAddress;
}
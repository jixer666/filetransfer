package com.abc.filetransfer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("files")
public class File {
    /**
     * 文件唯一标志（主键，自增）
     */
    @TableId(value = "file_id", type = IdType.AUTO)
    private Integer fileId;

    /**
     * 上传用户ID
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 原始文件名（可重复）
     */
    @TableField("original_name")
    private String originalName;

    /**
     * 存储文件名（服务器上）唯一
     */
    @TableField("stored_name")
    private String storedName;

    /**
     * 文件类型（扩展名）
     */
    @TableField("file_type")
    private String fileType;

    /**
     * 文件大小（字节->B、KB、MB、GB）
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 提取码（唯一）
     */
    @TableField("extract_code")
    private String extractCode;

    /**
     * 删除码（唯一）
     */
    @TableField("delete_code")
    private String deleteCode;

    /**
     * 上传时间
     */
    @TableField("upload_time")
    private LocalDateTime uploadTime;

    /**
     * 过期时间（上传后48小时）
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /**
     * 下载次数
     */
    @TableField("download_count")
    private Integer downloadCount;

    /**
     * 文件状态：有效或已删除
     */
    @TableField("status")
    private String status;

    /**
     * 文件存储路径
     */
    @TableField("file_path")
    private String filePath;
}
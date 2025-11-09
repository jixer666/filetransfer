package com.abc.filetransfer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {
    /**
     * 用户唯一标志（主键，自增）
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    /**
     * 用户名（唯一）
     */
    @TableField("username")
    private String username;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 用户角色（管理员/普通用户）
     */
    @TableField("role")
    private String role;

    /**
     * 主单位（本级主要单位）
     */
    @TableField("parent_unit")
    private String parentUnit;

    /**
     * 所属单位（下级所属单位）
     */
    @TableField("child_unit")
    private String childUnit;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    private String realName;

    /**
     * 单位固定电话
     */
    @TableField("telephone")
    private String telephone;

    /**
     * OA办公账号
     */
    @TableField("office_oa")
    private String officeOa;

    /**
     * 注册时间
     */
    @TableField("registration_time")
    private LocalDateTime registrationTime;

    /**
     * 上传累计次数
     */
    @TableField("upload_count")
    private Integer uploadCount;

    /**
     * 下载累计次数
     */
    @TableField("download_count")
    private Integer downloadCount;

    /**
     * 最后登录时间
     */
    @TableField("last_login")
    private LocalDateTime lastLogin;

    /**
     * 账户状态：激活或禁用
     */
    @TableField("status")
    private String status;

    /**
     * 审核通过的管理员用户ID
     */
    @TableField("approved_by")
    private Integer approvedBy;

    /**
     * 审核通过时间
     */
    @TableField("approved_time")
    private LocalDateTime approvedTime;
}
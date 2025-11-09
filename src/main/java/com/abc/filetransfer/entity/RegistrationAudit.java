package com.abc.filetransfer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("registration_audit")
public class RegistrationAudit {
    /**
     * 注册申请唯一标识（主键，自增）
     */
    @TableId(value = "registration_id", type = IdType.AUTO)
    private Integer registrationId;

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
     * 申请注册时间
     */
    @TableField("registration_time")
    private LocalDateTime registrationTime;

    /**
     * 审核状态：待处理、已同意、已拒绝
     */
    @TableField("status")
    private String status;
}